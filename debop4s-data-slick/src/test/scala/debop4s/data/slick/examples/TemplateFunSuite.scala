package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.slick.lifted.CompiledExecutable
import scala.util.Try

/**
 * TemplateFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 27.
 */
class TemplateFunSuite extends AbstractSlickFunSuite {

  test("Template parameters") {

    class Users(tag: Tag) extends Table[(Int, String)](tag, "template_users") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def first = column[String]("first")
      def * = (id, first)
    }
    val users = TableQuery[Users]

    class Orders(tag: Tag) extends Table[(Int, Int, String)](tag, "template_orders") {
      def orderId = column[Int]("orderId", O.PrimaryKey, O.AutoInc)
      def userId = column[Int]("userId")
      def product = column[String]("product")
      def * = (userId, orderId, product)
    }
    lazy val orders = TableQuery[Orders]

    val ddl = users.ddl ++ orders.ddl

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      users.map(_.first) ++= Seq("Homer", "Marge", "Apu", "Carl", "Lenny")
      users.map(_.id).run.foreach { userId =>
        orders.map(o => (o.userId, o.product)) +=(userId, if (userId < 4) "Product A" else "Product B")
      }

      def userNameById(id: Int) = users.filter(_.id === id.bind).map(_.first)
      userNameById(3).run shouldEqual Seq("Apu")

      // 요렇게 Parameter[T] 를 사용할 수 있네요^^
      val userNameByID2 = for {
        id <- Parameters[Int]
        u <- users if u.id === id
      } yield u.first
      userNameByID2(3).run shouldEqual Seq("Apu")

      val userNameByIDRange = for {
        (min, max) <- Parameters[(Int, Int)]
        u <- users if u.id >= min && u.id <= max
      } yield u.first
      userNameByIDRange(2, 5).run shouldEqual Seq("Marge", "Apu", "Carl", "Lenny")

      val userNameByIDBetween = for {
        (lower, upper) <- Parameters[(Int, Int)]
        u <- users if u.id.between(lower, upper)
      } yield u.first
      userNameByIDBetween(2, 5).run shouldEqual Seq("Marge", "Apu", "Carl", "Lenny")

      val userNameByIDRangeAndProduct = for {
        (min, (max, product)) <- Parameters[(Int, (Int, String))]
        u <- users if u.id >= min && u.id <= max && orders.filter(o => ( o.userId === u.id ) && ( o.product === product )).exists
      } yield u.first
      userNameByIDRangeAndProduct(2, (4, "Product A")).run shouldEqual Seq("Marge", "Apu")

      val userNameByIDRangeAndProduct2 = for {
        (min, max, product) <- Parameters[(Int, Int, String)]
        u <- users if u.id >= min && u.id <= max && orders.filter(o => ( o.userId === u.id ) && ( o.product === product )).exists
      } yield u.first
      userNameByIDRangeAndProduct2(2, 4, "Product A").run shouldEqual Seq("Marge", "Apu")

      def userNameByIDOrAll(id: Option[Int]) = for {
        u <- users if id.map(u.id === _.bind).getOrElse(LiteralColumn(true))
      } yield u.first
      userNameByIDOrAll(Some(3)).run shouldEqual Seq("Apu")
      userNameByIDOrAll(None).run
    }
  }

  test("compiled template") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "template_compiled_t") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s")
      def * = (id, s)
    }
    lazy val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      Compiled(ts.map(identity)) +=(1, "a")
      Compiled(ts) ++= Seq((2, "b"), (3, "c"))

      val byIdAndS = { (id: Column[Int], s: ConstColumn[String]) => ts.filter(t => t.id === id && t.s === s) }
      val byIdAndSC = Compiled(byIdAndS)
      val byIdAndFixedSC = byIdAndSC.map(f => f(_: Column[Int], "b"))
      val byIdC = Compiled { id: Column[Int] => ts.filter(_.id === id) }
      val byId = byIdC.extract
      val byIdC3 = byIdC(3)
      val byId3 = byIdC3.extract
      val countBelow = { (id: Column[Int]) => ts.filter(_.id < id).length }
      val countBelowC = Compiled(countBelow)

      byIdAndS(1, "a").run.toSet shouldEqual Set((1, "a"))
      byIdAndSC(1, "a").run.toSet shouldEqual Set((1, "a"))

      byIdAndFixedSC(2).run.toSet shouldEqual Set((2, "b"))

      byIdC(3).run.toSet shouldEqual Set((3, "c"))
      byId(3).run.toSet shouldEqual Set((3, "c"))
      byIdC3.run.toSet shouldEqual Set((3, "c"))
      byId3.run.toSet shouldEqual Set((3, "c"))

      countBelow(3).run shouldEqual 2
      countBelowC(3).run shouldEqual 2

      val joinC = Compiled { id: Column[Int] => ts.filter(_.id === id).innerJoin(ts.filter(_.id === id)) }
      joinC(1).run shouldEqual Seq(((1, "a"), (1, "a")))

      // implicitly 는 implicit 로 전달받는 인자를 정의한 함수를 implicit 변수를 다른 방법으로 표현하는 것입니다.
      //
      /*
      class Pair[T: Ordering](val first: T, val second: T) {
        def smaller(implicit ord:Ordering[T]) =
          if(ord.compare(first, second) < 0) first else second

        def smaller2 =
          if(implicitly[Ordering[T]].compare(first, second) < 0) first else second
      }
      */

      implicitly[scala.slick.lifted.Executable[(Column[Int], Column[Int]), _]]
      implicitly[scala.slick.lifted.Compilable[(Column[Int], Column[Int]), _]]

      val impShaped: (Column[Int], Column[Int]) = (ts.length, ts.length)
      val impShapedC: CompiledExecutable[(Column[Int], Column[Int]), (Int, Int)] = Compiled(impShaped)
      val impShapedR: (Int, Int) = impShapedC.run
      val impShapedT = impShapedR: (Int, Int)

      impShapedT shouldEqual(3, 3)

      implicitly[scala.slick.lifted.Executable[scala.slick.lifted.ShapedValue[(Column[Int], Column[Int]), (Int, Int)], _]]
      implicitly[scala.slick.lifted.Compilable[scala.slick.lifted.ShapedValue[(Column[Int], Column[Int]), (Int, Int)], _]]

      val expShaped = impShaped.shaped
      val expShapedC = Compiled(expShaped)
      val expShapedR = expShapedC.run
      val expShapedT = expShapedR: (Int, Int)
      expShapedT shouldEqual(3, 3)
    }
  }
}
