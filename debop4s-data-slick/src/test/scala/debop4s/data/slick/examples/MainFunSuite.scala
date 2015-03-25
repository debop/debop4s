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
      println(s"Insert SQL: ${ insQuery.insertStatement }")
      val ins1 = insQuery.insert("Homer", Some("Simpson"))
      val ins2 = insQuery.insertAll(("Marge", Some("Simpson")),
                                     ("Apu", Some("Nahasapeemapetilon")),
                                     ("Carl", Some("Carlson")),
                                     ("Lenny", Some("Leonard")))
      val ins3 = Users.map(u => u.first).insertAll("Santa's Little Helper", "Snowball")
      val total: Option[Int] = for (i2 <- ins2; i3 <- ins3) yield ins1 + i2 + i3
      println(s"Inserted ${ total.getOrElse("<unknown>") } users")

      total shouldEqual Some(7)

      val q1 = Users.map(x => (x.id, x.first, x.last))
      println(s"q1: ${ q1.selectStatement }")
      q1.run foreach { x => println(s"User tuple: $x") }

      val allUsers = q1.mapResult { case (id, f, l) => User(id, f, l.orNull) }.list
      allUsers foreach { u => println(s"User object: $u") }

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
      println(s"case statement: ${ q1b.selectStatement }")
      val r1b = q1b.run
      r1b foreach { u => println(s"With options and sequence: $u") }
      r1b shouldEqual expectedUserTuples.map {
        case (id, f, l) => (id, Some(f), l, if (id < 3) "low" else if (id < 6) "medium" else "high")
      }

      val q2 = for (u <- Users if u.first === "Apu".bind) yield (u.last, u.id)
      println(s"q2: ${ q2.selectStatement }")
      println(s"Apu's last name and id are: ${ q2.first }")
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
      println(s"q3=${ q3.selectStatement }")
      println("All Orders by Users with a last name by first name:")
      q3.list.foreach { o => println("\t" + o) }

      // H2:
      // select x2."first", x3."orderId"
      //   from "main_users" x2, "main_orders" x3
      //  where (x3."userId" = x2."id")
      //    and (x3."orderId" = (select max(x4."orderId") from "main_orders" x4 where x4."userId" = x3."userId"))
      val q4 = for {
        u <- Users
        o <- u.orders if o.orderId === ( for {o2 <- Orders.filter(_.userId === o.userId)} yield o2.orderId ).max
      } yield (u.first, o.orderId)
      println(s"q4=${ q4.selectStatement }")
      println("Latest Order per User: ")
      q4.list foreach { x => println("\t" + x) }
      q4.list.toSet shouldEqual Set(("Homer", 2), ("Marge", 4), ("Carl", 6), ("Lenny", 8), ("Santa's Little Helper", 10))
    }

  }

}
