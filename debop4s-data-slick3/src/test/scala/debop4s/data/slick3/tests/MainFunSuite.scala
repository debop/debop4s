package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._

import slick.backend.DatabasePublisher

/**
 * MainFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class MainFunSuite extends AbstractSlickFunSuite {

  case class User(id: Int, first: String, last: String)

  class Users(tag: Tag) extends Table[(Int, String, Option[String])](tag, "main_users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def first = column[String]("first", O.SqlType("VARCHAR(64)"))
    def last = column[Option[String]]("last")
    def * = (id, first, last)
    def getOrders = orders.filter(_.userId === id)
    def ins = (first, last)
  }
  lazy val users = TableQuery[Users]

  class Orders(tag: Tag) extends Table[(Int, Int, String, Boolean, Option[Boolean])](tag, "main_orders") {
    def userId = column[Int]("userId")
    def orderId = column[Int]("orderId", O.PrimaryKey, O.AutoInc)
    def product = column[String]("product")
    def shipped = column[Boolean]("shipped")
    def rebate = column[Option[Boolean]]("rebate")
    def * = (userId, orderId, product, shipped, rebate)
  }
  lazy val orders = TableQuery[Orders]

  test("main test") {
    val schema = users.schema ++ orders.schema

    println(users.map(_.ins).insertStatement)

    val q1 = (for (u <- users) yield (u.id, u.first, u.last)).sortBy(_._1)
    val q1a = users.map(u => (u.id, u.first, u.last)).sortBy(_._1)
    q1.result.statements.toSeq.length should be >= 1

    val q1b = for (u <- users) yield (u.id, u.first.?, u.last,
      Case If u.id < 3 Then "low" If u.id < 6 Then "medium" Else "high")
    q1b.result.statements.toSeq.length should be >= 1

    val q2 = users.filter(_.first === "Apu".bind).map(u => (u.last, u.id))
    q2.result.statements.toSeq.length should be >= 1

    val expectedUserTuples = List(
      (1, "Homer", Some("Simpson")),
      (2, "Marge", Some("Simpson")),
      (3, "Apu", Some("Nahasapeemapetilon")),
      (4, "Carl", Some("Carlson")),
      (5, "Lenny", Some("Leonard")),
      (6, "Santa's Little Helper", None),
      (7, "Snowball", None)
    )

    commit {
      DBIO.seq(
        schema.drop.asTry,
        schema.create,
        users.map(_.ins) +=("Homer", Some("Simpson")),
        users.map(_.ins) ++= Seq(
          ("Marge", Some("Simpson")),
          ("Apu", Some("Nahasapeemapetilon")),
          ("Carl", Some("Carlson")),
          ("Lenny", Some("Leonard"))
        ),
        users.map(_.first) ++= Seq("Santa's Little Helper", "Snowball")
      )
    }
    readonly { users.length.result } shouldBe 7

    readonly { q1.result } shouldEqual expectedUserTuples

    val p1 = db.stream(q1.result)
    val allUsers = materialize(p1.mapResult { case (id, f, l) => User(id, f, l.orNull) }).await
    allUsers shouldEqual expectedUserTuples.map { case (id, f, l) => User(id, f, l.orNull) }

    readonly { q1b.result } shouldEqual expectedUserTuples.map {
      case (id, f, l) => (id, Some(f), l, if (id < 3) "low" else if (id < 6) "medium" else "high")
    }
    readonly { q2.result }.head shouldEqual(Some("Nahasapeemapetilon"), 3)

    val ordersInserts =
      for (u <- allUsers if u.first != "Apu" && u.first != "Snowball"; i <- 1 to 2)
        yield orders.map(o => (o.userId, o.product, o.shipped, o.rebate)) +=
              (u.id, "Gizmo " + ((scala.math.random * 10) + 1).toInt, i == 2, Some(u.first == "Marge"))

    commit {
      DBIO.seq(ordersInserts: _*)
    }

    val q3 = for {
      u <- users.sortBy(_.first) if u.last.isDefined
      o <- u.getOrders
    } yield (u.first, u.last, o.orderId, o.product, o.shipped, o.rebate)

    materialize(db.stream(q3.result)).map(s => s.length shouldBe 8)

    val q4 = for {
      u <- users
      o <- u.getOrders if o.orderId === (for {o2 <- orders filter (o.userId === _.userId)} yield o2.orderId).max
    } yield (u.first, o.orderId)

    q4.result.statements.toSeq.length should be >= 1

    def maxOfPer[T <: Table[_], C[_]](c: Query[T, _, C])(m: (T => Rep[Int]), p: (T => Rep[Int])) =
      c filter { o => m(o) === (for {o2 <- c if p(o) === p(o2)} yield m(o2)).max }

    val q4b = for {
      u <- users
      o <- maxOfPer(orders)(_.orderId, _.userId) if o.userId === u.id
    } yield (u.first, o.orderId)

    q4b.result.statements.toSeq.length should be >= 1

    val q4d = for {
      u <- users if u.first inSetBind List("Homer", "Marge")
      o <- orders if o.userId === u.id
    } yield (u.first, (LiteralColumn(1) + o.orderId, 1), o.product)

    readonly { q4.to[Set].result } shouldBe Set(("Homer", 2), ("Marge", 4), ("Carl", 6), ("Lenny", 8), ("Santa's Little Helper", 10))
    readonly { q4b.to[Set].result } shouldBe Set(("Homer", 2), ("Marge", 4), ("Carl", 6), ("Lenny", 8), ("Santa's Little Helper", 10))

    val b1 = orders.filter(o => o.shipped && o.shipped).map(o => o.shipped && o.shipped)
    val b2 = orders.filter(o => o.shipped && o.rebate).map(o => o.shipped && o.rebate)
    val b3 = orders.filter(o => o.rebate && o.shipped).map(o => o.rebate && o.shipped)
    val b4 = orders.filter(o => o.rebate && o.rebate).map(o => o.rebate && o.rebate)
    val b5 = orders.filter(o => !o.shipped).map(o => !o.shipped)
    val b6 = orders.filter(o => !o.rebate).map(o => !o.rebate)
    val b7 = orders.map(o => o.shipped === o.shipped)
    val b8 = orders.map(o => o.rebate === o.shipped)
    val b9 = orders.map(o => o.shipped === o.rebate)
    val b10 = orders.map(o => o.rebate === o.rebate)

    b1.result.statements.toSeq.length should be >= 1
    b2.result.statements.toSeq.length should be >= 1
    b3.result.statements.toSeq.length should be >= 1
    b4.result.statements.toSeq.length should be >= 1
    b5.result.statements.toSeq.length should be >= 1
    b6.result.statements.toSeq.length should be >= 1
    b7.result.statements.toSeq.length should be >= 1
    b8.result.statements.toSeq.length should be >= 1
    b9.result.statements.toSeq.length should be >= 1
    b10.result.statements.toSeq.length should be >= 1

    val q5 = users filterNot { _.id in orders.map(_.userId) }
    q5.result.statements.toSeq.length should be >= 1
    q5.delete.statements.toSeq.length should be >= 1

    val q6 = Query(q5.length)
    q6.result.statements.toSeq.length should be >= 1

    q5.to[Set].exec shouldBe Set((3, "Apu", Some("Nahasapeemapetilon")), (7, "Snowball", None))
    commit { q5.delete } shouldBe 2
    readonly { q6.result }.head shouldBe 0

    val q7 = Compiled { (s: Rep[String]) => users.filter(_.first === s).map(_.first) }
    println(q7("Homer").updateStatement)
    val q7b = Compiled { users.filter(_.first === "Homer Jay").map(_.first) }
    println(q7b.updateStatement)

    commit {
      q7("Homer").update("Homer Jay").map(_ shouldBe 1) >>
      q7b.update("Homer").map(_ shouldBe 1) >>
      q7("Marge").map(_.length).result.map(_ shouldBe 1) >>
      q7("Marge").delete >>
      q7("Marge").map(_.length).result.map(_ shouldBe 0)
    }

    val q8 = for (u <- users if u.last.isEmpty) yield (u.first, u.last)
    println(q8.updateStatement)
    val q9 = users.length
    q9.result.statements.toSeq.length should be >= 1
    val q10 = users.filter(_.last inSetBind Seq()).map(u => (u.first, u.last))

    commit {
      q8.update("n/a", Some("n/a")).map(_ shouldBe 1) >>
      q9.result.map(_ shouldBe 4) >>
      q10.result.map(_ shouldBe Nil)
    }

    commit { schema.drop }
  }
}
