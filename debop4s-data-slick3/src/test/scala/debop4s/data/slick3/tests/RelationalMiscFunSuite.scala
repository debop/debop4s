package debop4s.data.slick3.tests

import debop4s.data.slick3._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase.driver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * RelationalMiscFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RelationalMiscFunSuite extends AbstractSlickFunSuite {

  test("is not and or") {
    class T(tag: Tag) extends Table[(String, String)](tag, "users") {
      def a = column[String]("a")
      def b = column[String]("b")
      def * = (a, b)
    }
    lazy val ts = TableQuery[T]

    db.seq(
      ts.schema.create,
      ts ++= Seq(("1", "a"), ("2", "a"), ("3", "b"))
    )

    val q1 = for (t <- ts if t.a === "1" || t.a === "2") yield t
    db.result(q1).toSet shouldEqual Set(("1", "a"), ("2", "a"))

    val q2 = for (t <- ts if (t.a =!= "1") || (t.b =!= "a")) yield t
    db.result(q2).toSet shouldEqual Set(("2", "a"), ("3", "b"))

    // No need to test that the unexpected result is actually unexpected
    // now that the compiler prints a warning about it

    val q4 = for (t <- ts if t.a =!= "1" || t.b =!= "a") yield t
    db.result(q4).toSet shouldEqual Set(("2", "a"), ("3", "b"))

    db.exec { ts.schema.drop }
  }

  test("like") {
    class T1(tag: Tag) extends Table[String](tag, "t1_2") {
      def a = column[String]("a")
      def * = a
    }
    lazy val t1s = TableQuery[T1]

    db.exec {
      t1s.schema.create >>
      (t1s ++= Seq("foo", "bar", "foobar", "foo%"))
    }

    val q1 = for {t1 <- t1s if t1.a like "foo"} yield t1.a
    db.result(q1) shouldEqual Seq("foo")

    val q2 = for {t1 <- t1s if t1.a like "foo%"} yield t1.a
    db.result(q2) shouldEqual Seq("foo", "foobar", "foo%")

    db.exec {
      ifCap(rcap.likeEscape) {
        val q3 = for {t1 <- t1s if t1.a.like("foo^%", '^')} yield t1.a
        q3.result.map(_ shouldEqual Seq("foo%"))
      }
    }

    db.exec { t1s.schema.drop }
  }

  test("sorting") {
    import slick.lifted.{Shape, ShapeLevel, Ordered}

    class T1(tag: Tag) extends Table[(String, String, String)](tag, "t1_3") {
      def a = column[String]("a")
      def b = column[String]("b")
      def c = column[String]("c")
      def * = (a, b, c)
    }
    lazy val ts = TableQuery[T1]

    implicit class TupledQueryExtensionMethods[E1, E2, U1, U2, C[_]](q: Query[(E1, E2), (U1, U2), C]) {
      def sortedValues(implicit ordered: (E1 => Ordered),
                       shape: Shape[FlatShapeLevel, E2, U2, E2]): Query[E2, U2, C] =
        q.sortBy(_._1).map(_._2)
    }

    db.exec {
      ts.schema.create >>
      (ts ++= Seq(("a2", "b2", "c2"), ("a1", "b1", "c1")))
    }

    val q1 = (for {
      t1 <- ts
    } yield t1.c ->(t1.a, t1.b)).sortedValues
    db.result(q1) shouldEqual Seq(("a1", "b1"), ("a2", "b2"))

    db.exec { ts.schema.drop }
  }

  test("conditional") {

  }

  test("cast") {

  }

  test("option conversions") {

  }

  test("init errors") {

  }
}
