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

      val q2 = for (t <- ts if ( t.a =!= "1" ) || ( t.b =!= "a" )) yield t
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

      // select x2."a" from "relational_misc_t_like" x2 where x2."a" like 'foo'
      val q1 = for (t1 <- ts if t1.a like "foo") yield t1.a
      q1.run shouldEqual Seq("foo")

      // select x2."a" from "relational_misc_t_like" x2 where x2."a" like 'foo'
      ts.filter(_.a like "foo").run shouldEqual Seq("foo")

      // select x2."a" from "relational_misc_t_like" x2 where x2."a" like 'foo%'
      val q2 = for (t1 <- ts if t1.a like "foo%") yield t1.a
      q2.run.toSet shouldEqual Set("foo", "foobar", "foo%")

      // select x2."a" from "relational_misc_t_like" x2 where x2."a" like 'foo%'
      ts.filter(_.a like "foo%").run.toSet shouldEqual Set("foo", "foobar", "foo%")

      // '%' 가 특수문자이므로, escape 사용하여 '%' 가 들어간 문장을 검색합니다.
      // select x2."a" from "relational_misc_t_like" x2 where x2."a" like 'foo^%' escape '^'
      ifCap(rcap.likeEscape) {
        val q3 = for (t <- ts if t.a.like("foo^%", '^')) yield t.a
        q3.run shouldEqual Seq("foo%")

        // select x2."a" from "relational_misc_t_like" x2 where x2."a" like 'foo^%' escape '^'
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
        def sortedValues(implicit ordered: ( E1 => scala.slick.lifted.Ordered ),
                         shape: Shape[FlatShapeLevel, E2, U2, E2]): Query[E2, U2, C] =
          q.sortBy(_._1).map(_._2)
      }

      // sortBy(t.c).map(t => (t.a, t.b)) 와 같다
      val q1 = ( for (t <- ts) yield t.c ->(t.a, t.b) ).sortedValues
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

      // H2
      // select x2."a", (case when (x2."a" < 3) then 1 else 0 end) from "relational_misc_t_conditional" x2
      val q1 = ts.map(t => (t.a, Case.If(t.a < 3) Then 1 Else 0))
      q1.run.toSet shouldEqual Set((1, 1), (2, 1), (3, 0), (4, 0))

      // H2
      // select x2."a", (case when (x2."a" < 3) then 1 end) from "relational_misc_t_conditional" x2
      val q2 = ts.map(t => (t.a, Case.If(t.a < 3) Then 1))
      q2.run.toSet shouldEqual Set((1, Some(1)), (2, Some(1)), (3, None), (4, None))

      // H2
      // select x2."a", (case when (x2."a" < 3) then 1 when (x2."a" < 4) then 2 else 0 end) from "relational_misc_t_conditional" x2
      val q3 = ts.map { t => (t.a, Case.If(t.a < 3) Then 1 If ( t.a < 4 ) Then 2 Else 0) }
      q3.run.toSet shouldEqual Set((1, 1), (2, 1), (3, 2), (4, 0))
    }
  }

  test("cast") {
    class T(tag: Tag) extends Table[(String, Int)](tag, "relational_misc_t_cast") {
      def a = column[String]("a")
      def b = column[Int]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq(("foo", 1), ("bar", 2))

      // H2:
      // select x2."a"||cast(x2."b" as CHAR) from "relational_misc_t_cast" x2
      val q1 = ts.map(t => t.a ++ t.b.asColumnOfType[String]("CHAR"))
      q1.run.toSet shouldEqual Set("foo1", "bar2")

      // H2:
      // select x2."a"||cast(x2."b" as VARCHAR) from "relational_misc_t_cast" x2
      val q2 = ts.map(t => t.a ++ t.b.asColumnOfType[String]("VARCHAR(255)"))
      q2.run.toSet shouldEqual Set("foo1", "bar2")
    }
  }

}
