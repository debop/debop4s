package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.AbstractSlickFunSuite

/**
 * TemplateFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class TemplateFunSuite extends AbstractSlickFunSuite {

  test("parameters") {
    class Users(tag: Tag) extends Table[(Int, String)](tag, "users") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def first = column[String]("first")
      def * = (id, first)
    }
    lazy val users = TableQuery[Users]

    class Orders(tag: Tag) extends Table[(Int, Int, String)](tag, "orders") {
      def userId = column[Int]("userId")
      def orderId = column[Int]("orderId", O.PrimaryKey, O.AutoInc)
      def product = column[String]("product")
      def * = (userId, orderId, product)
    }
    lazy val orders = TableQuery[Orders]

    def userNameByID1(id: Int) = users.filter(_.id === id.bind).map(_.first)
    def q1 = userNameByID1(3)

    val userNameByID2 = for {
      id <- Parameters[Int]
      u <- users if u.id === id
    } yield u.first
    val q2 = userNameByID2(3)

    val userNameByIDRange = for {
      (min, max) <- Parameters[(Int, Int)]
      u <- users if u.id.between(min, max) // u.id >= min && u.id <= max
    } yield u.first
    val q3 = userNameByIDRange(2, 5)

    val userNameByIDRangeAndProduct = for {
      (min, (max, product)) <- Parameters[(Int, (Int, String))]
      u <- users if u.id.between(min, max) && orders.filter(o => (o.userId === u.id) && (o.product === product)).exists
    } yield u.first
    val q4 = userNameByIDRangeAndProduct(2, (5, "Product A"))

    def userNameByIDOrAll(id: Option[Int]) =
      users.filter(u => id.map(u.id === _.bind).getOrElse(LiteralColumn(true))).map(_.first)
    val q5a = userNameByIDOrAll(Some(3))
    val q5b = userNameByIDOrAll(None)

    val schema = users.schema ++ orders.schema

    commit {
      DBIO.seq(
        schema.drop.asTry,
        schema.create,
        users.map(_.first) ++= Seq("Homer", "Marge", "Apu", "Carl", "Lenny")
      )
    }

    val uids = readonly { users.map(_.id).result }

    commit {
      DBIO.seq(uids.map(uid => orders.map(o => (o.userId, o.product)) +=(uid, if (uid < 4) "Product A" else "Product B")): _*)
    }

    readonly {
      DBIO.seq(
        q1.result.map(_ shouldEqual Seq("Apu")),
        q2.result.map(_ shouldEqual Seq("Apu")),
        q3.result.map(_.toSet shouldEqual Set("Marge", "Apu", "Carl", "Lenny")),
        q4.result.map(_.toSet shouldEqual Set("Marge", "Apu")),
        q5a.result.map(_ shouldEqual Seq("Apu")),
        q5b.result.map(_.toSet shouldEqual Set("Homer", "Marge", "Apu", "Carl", "Lenny"))
      )
    }

    commit { schema.drop }
  }

  test("compiled") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "lifted_t") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s")
      def * = (id, s)
    }
    lazy val ts = TableQuery[T]

    val byIdAndS = { (id: Rep[Int], s: ConstColumn[String]) => ts.filter(t => t.id === id && t.s === s) }
    val byIdAndSC = Compiled(byIdAndS)
    val byIdAndFixedSC = byIdAndSC.map { f => f(_: Rep[Int], "b") }
    val byIdC = Compiled { id: Rep[Int] => ts.filter(_.id === id) }
    val byId = byIdC.extract
    val byIdC3 = byIdC(3)
    val byId3 = byIdC3.extract
    val countBelow = { (id: Rep[Int]) => ts.filter(_.id < id).length }
    val countBelowC = Compiled(countBelow)
    val joinC = Compiled { (id: Rep[Int]) => ts.filter(_.id === id) join ts.filter(_.id === id) }

    // implicitly 는 implicit 로 전달받는 인자를 정의한 함수를 implicit 변수를 다른 방법으로 표현하는 것입니다.
    //
    implicitly[slick.lifted.Executable[(Rep[Int], Rep[Int]), _]]
    implicitly[slick.lifted.Compilable[(Rep[Int], Rep[Int]), _]]
    val impShaped = (ts.length, ts.length)
    val impShapedC = Compiled(impShaped)

    implicitly[slick.lifted.Executable[slick.lifted.ShapedValue[(Rep[Int], Rep[Int]), (Int, Int)], _]]
    implicitly[slick.lifted.Compilable[slick.lifted.ShapedValue[(Rep[Int], Rep[Int]), (Int, Int)], _]]
    val expShaped = impShaped.shaped
    val expShapedC = Compiled(expShaped)

    commit {
      ts.schema.drop.asTry >>
      ts.schema.create
    }

    readonly {
      DBIO.seq(
        Compiled(ts.map(identity)) +=(1, "a"),
        Compiled(ts) ++= Seq((2, "b"), (3, "c")),
        byIdAndS(1, "a").result.map(_.toSet shouldEqual Set((1, "a"))),
        byIdAndSC(1, "a").result.map(_.toSet shouldEqual Set((1, "a"))),
        byIdAndFixedSC(2).result.map(_.toSet shouldEqual Set((2, "b"))),
        byIdC3.result.map(_.toSet shouldEqual Set((3, "c"))),
        byId3.result.map(_.toSet shouldEqual Set((3, "c"))),
        countBelow(3).result.map(_ shouldEqual 2),
        countBelowC(3).result.map(_ shouldEqual 2),
        joinC(1).result.map(_ shouldEqual Seq(((1, "a"), (1, "a")))),
        impShapedC.result.map(_ shouldEqual(3, 3)),
        expShapedC.result.map(_ shouldEqual(3, 3))
      )
    }

    commit { ts.schema.drop }
  }

}
