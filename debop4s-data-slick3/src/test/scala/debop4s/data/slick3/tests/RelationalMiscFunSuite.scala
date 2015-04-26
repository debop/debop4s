package debop4s.data.slick3.tests

import debop4s.data.slick3.SlickContext
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._

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

    commit {
      DBIO.seq(ts.schema.drop.asTry,
               ts.schema.create,
               ts ++= Seq(("1", "a"), ("2", "a"), ("3", "b"))
      )
    }

    val q1 = for (t <- ts if t.a === "1" || t.a === "2") yield t
    readonly { q1.to[Set].result } shouldEqual Set(("1", "a"), ("2", "a"))

    val q2 = for (t <- ts if (t.a =!= "1") || (t.b =!= "a")) yield t
    readonly { q2.to[Set].result } shouldEqual Set(("2", "a"), ("3", "b"))

    // No need to test that the unexpected result is actually unexpected
    // now that the compiler prints a warning about it

    val q4 = for (t <- ts if t.a =!= "1" || t.b =!= "a") yield t
    readonly { q4.to[Set].result } shouldEqual Set(("2", "a"), ("3", "b"))

    commit { ts.schema.drop }
  }

  test("like") {
    class T1(tag: Tag) extends Table[String](tag, "t1_2") {
      def a = column[String]("a")
      def * = a
    }
    lazy val t1s = TableQuery[T1]

    commit {
      DBIO.seq(t1s.schema.drop.asTry,
               t1s.schema.create,
               t1s ++= Seq("foo", "bar", "foobar", "foo%")
      )
    }

    val q1 = for {t1 <- t1s if t1.a like "foo"} yield t1.a
    readonly(q1.result) shouldEqual Seq("foo")

    val q2 = for {t1 <- t1s if t1.a like "foo%"} yield t1.a
    readonly(q2.result) shouldEqual Seq("foo", "foobar", "foo%")

    readonly {
      ifCap(rcap.likeEscape) {
        val q3 = for {t1 <- t1s if t1.a.like("foo^%", '^')} yield t1.a
        q3.result.map(_ shouldEqual Seq("foo%"))
      }
    }

    commit { t1s.schema.drop }
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

    commit {
      DBIO.seq(ts.schema.drop.asTry,
               ts.schema.create,
               ts ++= Seq(("a2", "b2", "c2"), ("a1", "b1", "c1"))
      )
    }

    val q1 = (
             for {
               t1 <- ts
             } yield t1.c ->(t1.a, t1.b)
             ).sortedValues
    // val q1 = ts.map(x => (x.c, (x.a, x.b))).sortBy(_._1).map(_._2)
    readonly { q1.result } shouldEqual Seq(("a1", "b1"), ("a2", "b2"))

    commit { ts.schema.drop }
  }

  test("conditional") {
    class T1(tag: Tag) extends Table[Int](tag, "conditional_t") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T1]

    commit {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts ++= Seq(1, 2, 3, 4))
    }

    val q1 = ts.map { t => (t.a, Case If (t.a < 3) Then 1 Else 0) }
    readonly { q1.to[Set].result } shouldEqual Set((1, 1), (2, 1), (3, 0), (4, 0))

    val q2 = ts.map { t => (t.a, Case If (t.a < 3) Then 1) }
    readonly { q2.to[Set].result } shouldEqual Set((1, Some(1)), (2, Some(1)), (3, None), (4, None))

    val q3 = ts.map { t => (t.a, Case If (t.a < 3) Then 1 If (t.a < 4) Then 2 Else 0) }
    readonly { q3.to[Set].result } shouldEqual Set((1, 1), (2, 1), (3, 2), (4, 0))

    commit { ts.schema.drop }
  }

  test("cast") {
    class T1(tag: Tag) extends Table[(String, Int)](tag, "cast_t") {
      def a = column[String]("a")
      def b = column[Int]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T1]

    commit {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts ++= Seq(("foo", 1), ("bar", 2)))
    }

    // HINT: MariaDB에서는 VARCHAR(255) 도 안되고, CHAR(255) 형식으로 해야 합니다.
    val q1 = ts.map(t => t.a ++ (if (SlickContext.isMySQL) t.b.asColumnOfType[String]("CHAR(255)") else t.b.asColumnOf[String]))
    readonly { q1.to[Set].result } shouldEqual Set("foo1", "bar2")

    commit { ts.schema.drop }
  }

  test("option conversions") {
    class T1(tag: Tag) extends Table[(Int, Option[Int])](tag, "t1_optconv") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T1]

    commit {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts ++= Seq((1, Some(10)), (2, None)))
    }

    // GetOrElse in ResultSetMapping on client side ( 아닌데??? )
    val q1 = ts.map(t => (t.a, t.b.getOrElse(0)))

    // GetOrElse in query on the DB side
    val q2 = ts.map(t => (t.a, t.b.getOrElse(0) + 1))

    readonly {
      q1.to[Set].result.map { _ shouldEqual Set((1, 10), (2, 0)) } >>
      q2.to[Set].result.map { _ shouldEqual Set((1, 11), (2, 1)) }
    }

    commit { ts.schema.drop }
  }

  test("init errors") {
    case class Id(toInt: Int)
    case class Customer(id: Id)

    // Before making `shipped` and `toNode` in `TableQuery` lazy,
    // putting `Tables` before `A` caused a StackOverflowException
    object Tables {
      val as = TableQuery[A]
      implicit val idMapper = MappedColumnType.base[Id, Int](_.toInt, Id)
    }
    class A(tag: Tag) extends Table[Customer](tag, "init_a") {
      def id = column[Id]("id", O.PrimaryKey, O.AutoInc)(Tables.idMapper)

      import Tables.idMapper

      def * = id <>(Customer.apply, Customer.unapply)
    }
    Tables.as.schema

    case class Id2(toInt: Int)
    implicit val id2Mapper = null.asInstanceOf[BaseColumnType[Id2]]
    class B(tag: Tag) extends Table[Id2](tag, "INIT_A") {
      def id = column[Id2]("ID", O.PrimaryKey, O.AutoInc)
      def * = id
    }
    val bs = TableQuery[B]

    try {
      bs.map(_.id)
      bs.schema
      ???
    } catch {
      case t: NullPointerException if (t.getMessage ne null) && (t.getMessage contains "initialization order") =>
      // This is the expected error message from RelationalTableComponent.Table.column
    }

    try {
      MappedColumnType.base[Id, Int](_.toInt, Id)(implicitly, null.asInstanceOf[BaseColumnType[Int]])
      ???
    } catch {
      case t: NullPointerException if (t.getMessage ne null) && (t.getMessage contains "initialization order") =>
      // This is the expected error message from RelationalTypesComponent.MappedColumnTypeFactory.assertNonNullType
    }
  }
}
