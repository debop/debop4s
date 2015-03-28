package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try


/**
 * ExecutorFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ExecutorFunSuite extends AbstractSlickFunSuite {

  test("executor") {
    class T(tag: Tag) extends Table[Int](tag, "executor_t") {
      def a = column[Int]("a", O.NotNull)
      def * = a
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create
      ts ++= Seq(2, 3, 1, 5, 4)
    }

    withReadOnly { implicit session =>
      val q = ts.sortBy(_.a).map(_.a)
      val r2 = q.run
      val r2t = r2
      r2t shouldEqual List(1, 2, 3, 4, 5)
      q.length.run shouldEqual 5
    }
  }

  test("collections") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "executor_t_coll") {
      def a = column[Int]("a", O.NotNull)
      def b = column[String]("b")
      def * = (a, b)
    }
    lazy val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create
      ts ++= Seq(2 -> "a", 3 -> "b", 1 -> "c", 5 -> "d", 4 -> "e")
    }

    withReadOnly { implicit session =>
      val q1 = ts.sortBy(_.a).map(_.a)
      q1.run shouldEqual Seq(1, 2, 3, 4, 5)

      val q2a = ts.sortBy(_.a).map(_.a).to[Set]
      val q2b = ts.sortBy(_.a).to[Set].map(_.a)
      val q2c = ts.to[Set].sortBy(_.a).map(_.a)
      val e2 = Set(1, 2, 3, 4, 5)

      q2a.run shouldEqual e2
      q2b.run shouldEqual e2
      q2c.run shouldEqual e2

      ts.to[Array].run.isInstanceOf[Array[(Int, String)]] shouldEqual true
      ts.to[Array].map(_.a).run.isInstanceOf[Array[Int]] shouldEqual true
    }
  }

}
