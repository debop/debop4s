package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * MainFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 25.
 */
class MainFunSuite extends AbstractSlickFunSuite {

  case class User(id: Int, first: String, last: String)

  class Users(tag: Tag) extends Table[(Int, String, Option[String])](tag, "main_users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def first = column[String]("first", O.Length(64, true))
    def last = column[Option[String]]("last", O.Length(254, true))
    def * = (id, first, last)

    def orders = Orders.filter(_.userId === id)
  }
  object Users extends TableQuery(new Users(_)) {
    val byId = this.findBy(_.id)
  }

  class Orders(tag: Tag) extends Table[(Int, Int, String, Boolean, Option[Boolean])](tag, "main_orders") {
    def orderId = column[Int]("orderId", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("userId")
    def product = column[String]("product")
    def shipped = column[Boolean]("shipped")
    def rebate = column[Option[Boolean]]("rebate")

    def * = (userId, orderId, product, shipped, rebate)
  }
  object Orders extends TableQuery(new Orders(_)) {
    val byId = this.findBy(_.orderId)
    val byUserId = this.findBy(_.userId)
  }

  test("main test") {
    val ddl = Users.ddl ++ Orders.ddl
    ddl.createStatements foreach println

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      val insQuery = Users.map(u => (u.first, u.last))
      LOG.debug(s"Insert SQL: ${ insQuery.insertStatement }")
      val ins1 = insQuery.insert("Homer", Some("Simpson"))
      val ins2 = insQuery.insertAll(("Marge", Some("Simpson")),
                                     ("Apu", Some("Nahasapeemapetilon")),
                                     ("Carl", Some("Carlson")),
                                     ("Lenny", Some("Leonard")))
      val ins3 = Users.map(u => u.first).insertAll("Santa's Little Helper", "Snowball")
      val total: Option[Int] = for (i2 <- ins2; i3 <- ins3) yield ins1 + i2 + i3
      LOG.debug(s"Inserted ${ total.getOrElse("<unknown>") } users")

      total shouldEqual Some(7)

      val q1 = Users.map(x => (x.id, x.first, x.last))
      LOG.debug(s"q1: ${ q1.selectStatement }")
      q1.run foreach { x => LOG.debug(s"User tuple: $x") }

      val allUsers = q1.mapResult { case (id, f, l) => User(id, f, l.orNull) }.list
      allUsers foreach { u => LOG.debug(s"User object: $u") }

      val expectedUserTuples = Seq(
                                    (1, "Homer", Some("Simpson")),
                                    (2, "Marge", Some("Simpson")),
                                    (3, "Apu", Some("Nahasapeemapetilon")),
                                    (4, "Carl", Some("Carlson")),
                                    (5, "Lenny", Some("Leonard")),
                                    (6, "Santa's Little Helper", None),
                                    (7, "Snowball", None)
                                  )

      q1.list shouldEqual expectedUserTuples
      allUsers shouldEqual expectedUserTuples.map { case (id, f, l) => User(id, f, l.orNull) }

      // select x2."id",
      //        x2."first",
      //        x2."last",
      //        (case when (x2."id" < 3) then 'low' when (x2."id" < 6) then 'medium' else 'high' end)
      // from "main_users" x2
      val q1b =
        for {u <- Users}
          yield (u.id,
            u.first.?,
            u.last,
            Case If u.id < 3 Then "low" If u.id < 6 Then "medium" Else "high")
      LOG.debug(s"case statement: ${ q1b.selectStatement }")
      val r1b = q1b.run
      r1b foreach { u => LOG.debug(s"With options and sequence: $u") }
      r1b shouldEqual expectedUserTuples.map {
        case (id, f, l) => (id, Some(f), l, if (id < 3) "low" else if (id < 6) "medium" else "high")
      }

      val q2 = for (u <- Users if u.first === "Apu".bind) yield (u.last, u.id)
      LOG.debug(s"q2: ${ q2.selectStatement }")
      LOG.debug(s"Apu's last name and id are: ${ q2.first }")
      q2.first shouldEqual(Some("Nahasapeemapetilon"), 3)

      // TODO: verifyable non-random test
      for (u <- allUsers if u.first != "Apu" && u.first != "Snowball"; i <- 1 to 2)
        Orders.map(o => (o.userId, o.product, o.shipped, o.rebate))
        .insert(u.id, s"Gizmo ${ ( math.random * 10 ).toInt }", i == 2, Some(u.first == "Marge"))

      // H2:
      // select x2."first", x2."last", x3."orderId", x3."product", x3."shipped", x3."rebate"
      // from (select x4."id" as "id", x4."first" as "first", x4."last" as "last"
      //         from "main_users" x4
      //        order by x4."first"
      //      ) x2,
      //      "main_orders" x3
      // where (x2."last" is not null)
      //   and (x3."userId" = x2."id")
      val q3 = for {
        u <- Users.sortBy(_.first) if u.last.isDefined
        o <- u.orders
      } yield (u.first, u.last, o.orderId, o.product, o.shipped, o.rebate)
      LOG.debug(s"q3=${ q3.selectStatement }")
      LOG.debug("All Orders by Users with a last name by first name:")
      q3.list.foreach { o => LOG.debug("\t" + o) }

      // H2:
      // select x2."first", x3."orderId"
      //   from "main_users" x2, "main_orders" x3
      //  where (x3."userId" = x2."id")
      //    and (x3."orderId" = (select max(x4."orderId") from "main_orders" x4 where x4."userId" = x3."userId"))
      val q4 = for {
        u <- Users
        o <- u.orders if o.orderId === ( for {o2 <- Orders.filter(_.userId === o.userId)} yield o2.orderId ).max
      } yield (u.first, o.orderId)
      LOG.debug(s"q4=${ q4.selectStatement }")
      LOG.debug("Latest Order per User: ")
      q4.list foreach { x => LOG.debug("\t" + x) }
      q4.list.toSet shouldEqual Set(("Homer", 2), ("Marge", 4), ("Carl", 6), ("Lenny", 8), ("Santa's Little Helper", 10))


      // custom 함수
      def maxOfPer[T <: Table[_], C[_]](c: Query[T, _, C])(m: T => Column[Int], p: T => Column[Int]) = {
        c filter { o => m(o) === ( for (o2 <- c if p(o) === p(o2)) yield m(o2) ).max }
      }
      // H2:
      // select x2."first", x3."orderId"
      //   from "main_users" x2, "main_orders" x3
      //  where (x3."orderId" = (select max(x4."orderId") from "main_orders" x4 where x3."userId" = x4."userId"))
      //    and (x3."userId" = x2."id")
      val q4b = for {
        u <- Users
        o <- maxOfPer(Orders)(_.orderId, _.userId) if o.userId === u.id
      } yield (u.first, o.orderId)
      LOG.debug(s"q4b: ${ q4b.selectStatement }")
      q4b.foreach(o => LOG.debug("  " + o))
      q4b.list.toSet shouldEqual Set(("Homer", 2), ("Marge", 4), ("Carl", 6), ("Lenny", 8), ("Santa's Little Helper", 10))


      // H2:
      // select x2.x3, x2.x4, x2.x5, x2.x6
      //   from (select x7."first" as x3, 1 + x8."orderId" as x4, 1 as x5, x8."product" as x6
      //           from "main_users" x7, "main_orders" x8
      //          where (x7."first" in (?, ?))
      //            and (x8."userId" = x7."id")) x2
      val q4d = for {
        u <- Users if u.first inSetBind Seq("Homer", "Marge")
        o <- Orders if o.userId === u.id
      } yield (u.first, (LiteralColumn(1) + o.orderId, 1), o.product)
      LOG.debug(s"q4d: ${ q4d.selectStatement }")
      LOG.debug("Orders for Homer and Marge:")
      q4d.run.foreach { o => LOG.debug("  " + o) }

      // && 는 and 로 변환, || 는 or 로 변환
      val b1 = Orders.filter(o => o.shipped && o.shipped).map(o => o.shipped && o.shipped)
      val b2 = Orders.filter(o => o.shipped && o.rebate).map(o => o.shipped && o.rebate)
      val b3 = Orders.filter(o => o.rebate && o.shipped).map(o => o.rebate && o.shipped)
      val b4 = Orders.filter(o => o.rebate && o.rebate).map(o => o.rebate && o.rebate)
      val b5 = Orders.filter(o => !o.shipped).map(o => !o.shipped)
      val b6 = Orders.filter(o => !o.rebate).map(o => !o.rebate)
      val b7 = Orders.map(o => o.shipped === o.shipped)
      val b8 = Orders.map(o => o.rebate === o.shipped)
      val b9 = Orders.map(o => o.shipped === o.rebate)
      val b10 = Orders.map(o => o.rebate === o.rebate)

      LOG.debug("b1: " + b1.selectStatement)
      LOG.debug("b2: " + b2.selectStatement)
      LOG.debug("b3: " + b3.selectStatement)
      LOG.debug("b4: " + b4.selectStatement)
      LOG.debug("b5: " + b5.selectStatement)
      LOG.debug("b6: " + b6.selectStatement)
      LOG.debug("b7: " + b7.selectStatement)
      LOG.debug("b8: " + b8.selectStatement)
      LOG.debug("b9: " + b9.selectStatement)
      LOG.debug("b10: " + b10.selectStatement)


      // H2:
      // select x2."id", x2."first", x2."last"
      //   from "main_users" x2
      //  where not (x2."id" in (select x3."userId" from "main_orders" x3))
      val q5 = Users filterNot { _.id in Orders.map(_.userId) }
      LOG.debug(s"q5 = ${ q5.selectStatement }")
      LOG.debug("Order가 없는 사용자:")
      q5.run foreach { u => LOG.debug("  " + u) }
      q5.run shouldEqual Seq((3, "Apu", Some("Nahasapeemapetilon")), (7, "Snowball", None))

      LOG.debug(s"q5 delete: ${ q5.deleteStatement }")
      LOG.debug("delete users...")
      val deleted = q5.delete
      LOG.debug(s"Deleted $deleted users")
      deleted shouldEqual 2

      val q6 = Query(q5.length)
      LOG.debug(s"q6: ${ q6.selectStatement }")
      LOG.debug("Order가 없는 사용자:" + q6.first)
      q6.first shouldEqual 0

      // H2 :
      // update "main_users" set "first" = ? where "main_users"."first" = ?
      val q7 = Compiled { (s: Column[String]) => Users.filter(_.first === s).map(_.first) }
      LOG.debug("q7: " + q7("Homer").updateStatement)
      val updated1 = q7("Homer").update("Homer Jay")
      LOG.debug(s"Updated $updated1 row(s)")
      updated1 shouldEqual 1

      val q7b = Compiled { Users.filter(_.first === "Homer Jay").map(_.first) }
      LOG.debug("q7b: " + q7b.updateStatement)
      val updated1b = q7b.update("Homer")
      LOG.debug(s"Updated $updated1b row(s)")
      updated1b shouldEqual 1

      // H2: select x2.x3 from (select count(1) as x3 from (select x4."first" as x5 from "main_users" x4 where x4."first" = 'Marge') x6) x2
      q7("Marge").map(_.length).run shouldEqual 1
      q7("Marge").map(_.exists).run shouldEqual true
      q7("Marge").delete
      q7("Marge").map(_.length).run shouldEqual 0
      q7("Marge").map(_.exists).run shouldEqual false


      val q8 = for (u <- Users if u.last.isEmpty) yield (u.first, u.last)
      LOG.debug("q8: " + q8.updateStatement)
      val updated2 = q8.update("n/a", Some("n/a"))
      LOG.debug(s"Updated $updated2 row(s)")
      updated2 shouldEqual 1

      // H2:
      // select x2.x3 from (select count(1) as x3 from (select x4."id" as x5, x4."first" as x6, x4."last" as x7 from "main_users" x4) x8) x2
      Users.list
      val q9 = Users.length
      q9.run shouldEqual 4

      // H2:
      // select x2."first", x2."last" from "main_users" x2 where false
      val q10 = Users.filter(_.last inSetBind Seq()).map(u => (u.first, u.last))
      q10.run shouldEqual Nil

    }

  }

}
