package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * RelationalMiscFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RelationalMiscFunSuite extends AbstractSlickFunSuite {

  test("isNot and OR") {
    class T(tag: Tag) extends Table[(String, String)](tag, "relational_misc_t") {
      def a = column[String]("a")
      def b = column[String]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      val data = Seq(("1", "a"), ("2", "a"), ("3", "b"))
      ts ++= data

      val q1 = for (t <- ts if t.a === "1" || t.a === "2") yield t
      q1.run.toSet shouldEqual Set(("1", "a"), ("2", "a"))

      val q2 = for (t <- ts if (t.a =!= "1") || (t.b =!= "a")) yield t
      q2.run.toSet shouldEqual Set(("2", "a"), ("3", "b"))

      val q4 = for (t <- ts if t.a =!= "1" || t.b =!= "a") yield t
      q4.run.toSet shouldEqual Set(("2", "a"), ("3", "b"))
    }
  }

  test("like") {
    class T(tag: Tag) extends Table[String](tag, "relational_misc_t_like") {
      def a = column[String]("a")
      def * = a
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq("foo", "bar", "foobar", "foo%")

      val q1 = for (t1 <- ts if t1.a like "foo") yield t1.a
      q1.run shouldEqual Seq("foo")

      ts.filter(_.a like "foo").run shouldEqual Seq("foo")

      val q2 = for (t1 <- ts if t1.a like "foo%") yield t1.a
      q2.run.toSet shouldEqual Set("foo", "foobar", "foo%")

      ts.filter(_.a like "foo%").run.toSet shouldEqual Set("foo", "foobar", "foo%")

      // '%' 가 특수문자이므로, escape 사용하여 '%' 가 들어간 문장을 검색합니다.
      ifCap(rcap.likeEscape) {
        val q3 = for (t <- ts if t.a.like("foo^%", '^')) yield t.a
        q3.run shouldEqual Seq("foo%")

        ts.filter(_.a.like("foo^%", '^')).run shouldEqual Seq("foo%")
      }
    }
  }

  test("sorting") {
    class T(tag: Tag) extends Table[(String, String, String)](tag, "relational_misc_t_sorting") {
      def a = column[String]("a")
      def b = column[String]("b")
      def c = column[String]("c")
      def * = (a, b, c)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq(("a2", "b2", "c2"), ("a1", "b1", "c1"))

      implicit class TupledQueryExtensionMethods[E1, E2, U1, U2, C[_]](q: Query[(E1, E2), (U1, U2), C]) {
        def sortedValues(implicit ordered: (E1 => scala.slick.lifted.Ordered),
                         shape: Shape[FlatShapeLevel, E2, U2, E2]): Query[E2, U2, C] =
          q.sortBy(_._1).map(_._2)
      }

      // sortBy(t.c).map(t => (t.a, t.b)) 와 같다
      val q1 = (for (t <- ts) yield t.c ->(t.a, t.b)).sortedValues
      q1.run shouldEqual Seq(("a1", "b1"), ("a2", "b2"))
    }
  }

  test("conditional") {
    class T(tag: Tag) extends Table[Int](tag, "relational_misc_t_conditional") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq(1, 2, 3, 4)

      val q1 = ts.map(t => (t.a, Case.If(t.a < 3) Then 1 Else 0))
      q1.run.toSet shouldEqual Set((1, 1), (2, 1), (3, 0), (4, 0))
    }
  }

}
