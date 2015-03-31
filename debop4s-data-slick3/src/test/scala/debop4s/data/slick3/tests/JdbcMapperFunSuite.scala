package debop4s.data.slick3.tests

import debop4s.data.slick3._
import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

/**
 * JdbcMapperFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcMapperFunSuite extends AbstractSlickFunSuite {

  test("mapped entity") {

    case class User(id: Option[Int], first: String, last: String)
    case class Foo[T](value: T)

    class Users(tag: Tag) extends Table[User](tag, "users") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def first = column[String]("first")
      def last = column[String]("last")

      def * = (id.?, first, last) <>(User.tupled, User.unapply)
      def baseProjection = (first, last)

      // AutoInc 가 있는 엔티티를 update 할 때
      def forUpdate = baseProjection.shaped <>
                      ( { case (f, l) => User(None, f, l) }, { u: User => Some((u.first, u.last)) })

      // 다른 수형으로 변경하고자 할 때
      def asFoo = forUpdate <>((u: User) => Foo(u), (f: Foo[User]) => Some(f.value))
    }
    object users extends TableQuery(new Users(_)) {
      val byID = this.findBy(_.id)
    }

    val updateQ = users.filter(_.id === 2.bind).map(_.forUpdate)
    updateQ.updateStatement.length should be > 0

    val q1 = for {
      u <- users
      u2 <- users
    } yield u2

    db.seq(
      users.schema.drop.asTry,
      users.schema.create,
      users.map(_.baseProjection) +=("Homer", "Simpson"),
      users ++= Seq(
        User(None, "Marge", "Bovier"),
        User(None, "Carl", "Carlson")
      ),
      users.map(_.asFoo) += Foo(User(None, "Lenny", "Leonard"))
    )
    db.exec(users.filter(_.last inSet Set("Bovier", "Ferdinand")).size.result) shouldEqual 1
    db.exec(updateQ.update(User(None, "Marge", "Simpson")))
    db.result(Query(users.filter(_.id === 1).exists)).head shouldEqual true
    db.result(users.filter(_.id between(1, 2))).to[Set] shouldEqual Set(User(Some(1), "Homer", "Simpson"), User(Some(2), "Marge", "Simpson"))
    db.result(users.filter(_.id between(1, 2)).map(_.asFoo)).to[Set] shouldEqual Set(Foo(User(None, "Homer", "Simpson")), Foo(User(None, "Marge", "Simpson")))
    db.exec(users.byID(3).result.head) shouldBe User(Some(3), "Carl", "Carlson")

    db.exec { users.schema.drop }
  }

  test("update") {
    case class Data(a: Int, b: Int)
    class T(tag: Tag) extends Table[Data](tag, "mapper_update_t") {
      def a = column[Int]("a")
      def b = column[Int]("b")
      def * = (a, b) <>(Data.tupled, Data.unapply)
    }
    val ts = TableQuery[T]

    val updateQ = ts.filter(_.a === 1)
    val updateQ2 = ts.filter(_.a === 3).map(identity)

    db.seq(
      ts.schema.create,
      ts ++= Seq(Data(1, 2), Data(3, 4), Data(5, 6)),
      updateQ.update(Data(7, 8)),
      updateQ2.update(Data(9, 10))
    )
    db.result(ts).to[Set] shouldEqual Set(Data(7, 8), Data(9, 10), Data(5, 6))

    db.exec { ts.schema.drop }
  }

  test("wide mapped entity") {
    case class Part(i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int)
    case class Whole(id: Int, p1: Part, p2: Part, p3: Part, p4: Part)

    class T(tag: Tag) extends Table[Whole](tag, "mapped_wide_t") {
      def id = column[Int]("id", O.PrimaryKey)
      def p1i1 = column[Int]("p1i1")
      def p1i2 = column[Int]("p1i2")
      def p1i3 = column[Int]("p1i3")
      def p1i4 = column[Int]("p1i4")
      def p1i5 = column[Int]("p1i5")
      def p1i6 = column[Int]("p1i6")

      def p2i1 = column[Int]("p2i1")
      def p2i2 = column[Int]("p2i2")
      def p2i3 = column[Int]("p2i3")
      def p2i4 = column[Int]("p2i4")
      def p2i5 = column[Int]("p2i5")
      def p2i6 = column[Int]("p2i6")

      def p3i1 = column[Int]("p3i1")
      def p3i2 = column[Int]("p3i2")
      def p3i3 = column[Int]("p3i3")
      def p3i4 = column[Int]("p3i4")
      def p3i5 = column[Int]("p3i5")
      def p3i6 = column[Int]("p3i6")

      def p4i1 = column[Int]("p4i1")
      def p4i2 = column[Int]("p4i2")
      def p4i3 = column[Int]("p4i3")
      def p4i4 = column[Int]("p4i4")
      def p4i5 = column[Int]("p4i5")
      def p4i6 = column[Int]("p4i6")

      def * = (
                id,
                (p1i1, p1i2, p1i3, p1i4, p1i5, p1i6),
                (p2i1, p2i2, p2i3, p2i4, p2i5, p2i6),
                (p3i1, p3i2, p3i3, p3i4, p3i5, p3i6),
                (p4i1, p4i2, p4i3, p4i4, p4i5, p4i6)
                ).shaped <>
              ( {
                case (id, p1, p2, p3, p4) =>
                  // NOTE: .shaped 를 쓰지 않는다면, 모든 파라미터에 수형을 지정해줘야 합니다.
                  Whole(id, Part.tupled(p1), Part.tupled(p2), Part.tupled(p3), Part.tupled(p4))
              }, { w: Whole =>
                def f(p: Part) = Part.unapply(p).get
                Some((w.id, f(w.p1), f(w.p2), f(w.p3), f(w.p4)))
              })
    }
    val ts = TableQuery[T]

    val oData = Whole(0,
      Part(11, 12, 13, 14, 15, 16),
      Part(21, 22, 23, 24, 25, 26),
      Part(31, 32, 33, 34, 35, 36),
      Part(41, 42, 43, 44, 45, 46)
    )

    db.seq(
      ts.schema.create,
      ts += oData,
      ts.result.head.map(_ shouldEqual oData),
      ts.schema.drop
    )
  }

  test("entity 에 여러 개의 component - embeddable 처럼") {
    case class Part1(i1: Int, i2: String)
    case class Part2(i1: String, i2: Int)
    case class Whole(id: Int, p1: Part1, p2: Part2)

    class T(tag: Tag) extends Table[Whole](tag, "nested_t") {
      def id = column[Int]("id", O.PrimaryKey)
      def p1 = column[Int]("p1")
      def p2 = column[String]("p2")
      def p3 = column[String]("p3")
      def p4 = column[Int]("p4")
      def part1 = (p1, p2) <>(Part1.tupled, Part1.unapply)
      def part2 = (p3, p4) <>(Part2.tupled, Part2.unapply)
      def * = (id, part1, part2) <>(Whole.tupled, Whole.unapply)
    }
    val ts = TableQuery[T]

    val data = Seq(Whole(1, Part1(1, "2"), Part2("3", 4)))

    db.exec {
      ts.schema.create >>
      (ts ++= data) >>
      (ts.result.map(_ shouldEqual data)) >>
      ts.schema.drop
    }
  }

  test("mapped join") {

    case class A(id: Int, value: Int)
    case class B(id: Int, value: Option[String])

    class ARow(tag: Tag) extends Table[A](tag, "mapped_join_a") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def data = column[Int]("data")
      def * = (id, data) <>(A.tupled, A.unapply)
    }
    lazy val as = TableQuery[ARow]

    class BRow(tag: Tag) extends Table[B](tag, "mapped_join_b") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def data = column[String]("data")
      def * = (id, Rep.Some(data)) <>(B.tupled, B.unapply)
    }
    lazy val bs = TableQuery[BRow]

    val q1 = for {
      a <- as if a.data === 2
      b <- bs if b.id === a.id
    } yield (a, b)

    val q2 = as joinLeft bs

    db.exec {
      (as.schema ++ bs.schema).create >>
      (as.map(_.data) ++= Seq(1, 2)) >>
      (bs.map(_.data) ++= Seq("a", "b"))
    }

    /*
    ┇ select x2.x3, x2.x4, x5.x6, x5.x7, x5.x8
    ┇ from (
    ┇   select x9."id" as x3, x9."data" as x4
    ┇   from "mapped_join_a" x9
    ┇ ) x2
    ┇ left outer join (
    ┇   select 1 as x6, x10."id" as x7, x10."data" as x8
    ┇   from "mapped_join_b" x10
    ┇ ) x5
    */
    db.result(q1).to[Set] shouldEqual Set((A(2, 2), B(2, Some("b"))))
    db.result(q2).to[Set] shouldEqual Set(
      (A(1, 1), Some(B(1, Some("a")))),
      (A(1, 1), Some(B(2, Some("b")))),
      (A(2, 2), Some(B(1, Some("a")))),
      (A(2, 2), Some(B(2, Some("b"))))
    )

    db.exec { (as.schema ++ bs.schema).drop }
  }

  test("case class shape") {
    case class C(a: Int, b: String)
    case class LiftedC(a: Rep[Int], b: Rep[String])
    implicit object cShape extends CaseClassShape(LiftedC.tupled, C.tupled)

    class A(tag: Tag) extends Table[C](tag, "case_class_shape_a") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s")
      def * = LiftedC(id, s)
    }
    lazy val as = TableQuery[A]
    val data = Seq(C(1, "a"), C(2, "b"))

    db.seq(
      as.schema.create,
      as ++= data,
      as.sortBy(_.id).result.map { _ shouldEqual data },
      as.schema.drop
    )
  }

  test("product class shape") {
    def columnShape[T](implicit s: Shape[FlatShapeLevel, Rep[T], T, Rep[T]]) = s
    class C(val a: Int, val b: Option[String]) extends Product {
      def canEqual(that: Any): Boolean = that.isInstanceOf[C]
      def productArity: Int = 2
      def productElement(n: Int): Any = Seq(a, b)(n)
      override def equals(a: Any) = a match {
        case that: C => this.a == that.a && this.b == that.b
        case _ => false
      }
    }

    class LiftedC(val a: Rep[Int], val b: Rep[Option[String]]) extends Product {
      def canEqual(that: Any) = that.isInstanceOf[LiftedC]
      def productArity: Int = 2
      def productElement(n: Int): Any = Seq(a, b)(n)
      override def equals(a: Any) = a match {
        case that: C => this.a == that.a && this.b == that.b
        case _ => false
      }
    }
    implicit object cShape extends ProductClassShape(
      Seq(columnShape[Int], columnShape[Option[String]]),
      seq => new LiftedC(seq(0).asInstanceOf[Rep[Int]], seq(1).asInstanceOf[Rep[Option[String]]]),
      seq => new C(seq(0).asInstanceOf[Int], seq(1).asInstanceOf[Option[String]])
    )
    class A(tag: Tag) extends Table[C](tag, "product_class_shape_a") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[Option[String]]("s", O.Length(255))
      def * = new LiftedC(id, s)
    }
    val as = TableQuery[A]
    val data = Seq(new C(1, Some("a")), new C(2, Some("b")))

    db.exec {
      as.schema.create >>
      (as ++= data) >>
      as.sortBy(_.id).result.map(_ shouldEqual data) >>
      as.schema.drop
    }
  }

  test("custom shape") {

    case class Pair[A, B](a: A, b: B)
    final class PairShape[Level <: ShapeLevel, M <: Pair[_, _], U <: Pair[_, _] : ClassTag, P <: Pair[_, _]](val shapes: Seq[Shape[_, _, _, _]])
      extends MappedScalaProductShape[Level, Pair[_, _], M, U, P] {
      def buildValue(elems: IndexedSeq[Any]) = Pair(elems(0), elems(1))
      def copy(shapes: Seq[Shape[_ <: ShapeLevel, _, _, _]]) = new PairShape(shapes)
    }
    implicit def pairShape[Level <: ShapeLevel, M1, M2, U1, U2, P1, P2]
    (implicit s1: Shape[_ <: Level, M1, U1, P1], s2: Shape[_ <: Level, M2, U2, P2]) =
      new PairShape[Level, Pair[M1, M2], Pair[U1, U2], Pair[P1, P2]](Seq(s1, s2))

    class A(tag: Tag) extends Table[Pair[Int, String]](tag, "shape_a") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s")
      def * = Pair(id, s)
    }
    val as = TableQuery[A]

    // use it for returning data from query
    val q2 = as
             .map { case a => Pair(a.id, (a.s ++ a.s)) }
             .filter { case Pair(id, _) => id =!= 1 }
             .sortBy { case Pair(_, ss) => ss }
             .map { case Pair(id, ss) => Pair(id, Pair(42, ss)) }

    db.seq(
      as.schema.create,
      as ++= Seq(Pair(1, "a"), Pair(2, "c"), Pair(3, "b")),
      q2.result.map(_ shouldEqual Seq(Pair(3, Pair(42, "bb")), Pair(2, Pair(42, "cc")))),
      as.schema.drop
    )
  }

  test("HList") {
    import slick.collection.heterogeneous._
    import slick.collection.heterogeneous.syntax._

    class B(tag: Tag) extends Table[Int :: Boolean :: String :: HNil](tag, "hlist_b") {
      def id = column[Int]("id", O.PrimaryKey)
      def b = column[Boolean]("b")
      def s = column[String]("s")
      def * = id :: b :: s :: HNil
    }
    val bs = TableQuery[B]

    val q1 = (
               for {
                 id :: b :: s :: HNil <- (for {b <- bs} yield b.id :: b.b :: b.s :: HNil) if !b
               } yield id :: b :: (s ++ s) :: HNil)
             .sortBy(h => h(2))
             .map { case id :: b :: ss :: HNil => id :: ss :: (42 :: HNil) :: HNil }

    val q2 = bs
             .map { case b => b.id :: b.b :: (b.s ++ b.s) :: HNil }
             .filter { h => !h(1) }
             .sortBy { case _ :: _ :: ss :: HNil => ss }
             .map { case id :: b :: ss :: HNil => id :: ss :: (42 :: HNil) :: HNil }

    db.seq(
      bs.schema.create,
      bs += (1 :: true :: "a" :: HNil),
      bs += (2 :: false :: "c" :: HNil),
      bs += (3 :: false :: "b" :: HNil),

      q1.result.map(_ shouldEqual Seq(3 :: "bb" :: (42 :: HNil) :: HNil, 2 :: "cc" :: (42 :: HNil) :: HNil)),
      q2.result.map(_ shouldEqual Seq(3 :: "bb" :: (42 :: HNil) :: HNil, 2 :: "cc" :: (42 :: HNil) :: HNil)),

      bs.schema.drop
    )
  }

  test("single element") {
    import slick.collection.heterogeneous._
    import slick.collection.heterogeneous.syntax._

    class A(tag: Tag) extends Table[String](tag, "single_a") {
      def b = column[String]("b")
      def * = b
    }
    lazy val as = TableQuery[A]

    class B(tag: Tag) extends Table[Tuple1[String]](tag, "single_b") {
      def b = column[String]("b")
      def * = Tuple1(b)
    }
    lazy val bs = TableQuery[B]

    class C(tag: Tag) extends Table[String :: HNil](tag, "single_c") {
      def b = column[String]("b")
      def * = b :: HNil
    }
    lazy val cs = TableQuery[C]

    lazy val schema = as.schema ++ bs.schema ++ cs.schema
    db.seq(
      schema.create,

      as += "Foo",
      as.result.head.map { _ shouldEqual "Foo" },
      as.map(a => a :: a :: HNil).result.head.map { _ shouldEqual "Foo" :: "Foo" :: HNil },

      bs += Tuple1("Foo"),
      bs.update(Tuple1("Foo")),
      bs.result.head.map { _ shouldEqual Tuple1("Foo") },

      cs += ("Foo" :: HNil),
      cs.update("Foo" :: HNil),
      cs.result.head map { _ shouldEqual "Foo" :: HNil },

      schema.drop
    )
  }

  /**
   * Fast Path Result Converters
   * -. remove boxing and allocation overhead
   */
  test("fast path") {

    case class Data(a: Int, b: Int)
    class T(tag: Tag) extends Table[Data](tag, "fastpath_t") {
      def a = column[Int]("A")
      def b = column[Int]("B")
      def * = (a, b) <>(Data.tupled, Data.unapply) fastPath (new FastPath(_) {
        val (a, b) = (next[Int], next[Int])
        override def read(r: Reader) = Data(a.read(r), b.read(r))
      })
    }
    val ts = TableQuery[T]

    db.seq(
      ts.schema.create,
      ts ++= Seq(Data(1, 2), Data(3, 4), Data(5, 6)),
      ts.filter(_.a === 1).update(Data(7, 8)),
      ts.filter(_.a === 3).map(identity).update(Data(9, 10)),
      ts.to[Set].result.map { _ shouldEqual Set(Data(7, 8), Data(9, 10), Data(5, 6)) },
      ts.schema.drop
    )
  }

}
