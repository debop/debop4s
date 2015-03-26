package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.reflect.ClassTag
import scala.util.Try

/**
 * JdbcMapperFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcMapperFunSuite extends AbstractSlickFunSuite {

  test("mapped entity") {
    case class User(id: Option[Int], first: String, last: String)
    case class Foo[T](value: T)

    class Users(tag: Tag) extends Table[User](tag, "jdbc_mapper_users") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def first = column[String]("first")
      def last = column[String]("last")

      def * = (id.?, first, last) <>(User.tupled, User.unapply)

      def baseProjection = (first, last)
      def forUpdate = baseProjection.shaped <>
                      ( { case (f, l) => User(None, f, l) }, { u: User => Some((u.first, u.last)) })

      // 다른 객체로 casting 할 때 사용합니다.
      def asFoo = forUpdate <>((u: User) => Foo(u), (f: Foo[User]) => Some(f.value))
    }

    object users extends TableQuery(new Users(_)) {
      val byID = this.findBy(_.id)
    }

    withSession { implicit session =>
      Try { users.ddl.drop }
      users.ddl.create

      users.map(_.baseProjection).insert("Homer", "Simpson")
      users.insertAll(
                       User(None, "Marge", "Bouvier"),
                       User(None, "Carl", "Carlson")
                     )
      users.map(_.asFoo) += Foo(User(None, "Lenny", "Leonard"))

      val lastNames = Set("Bouvier", "Ferdinand")
      users.filter(_.last inSet lastNames).length.run shouldEqual 1

      val updateQ = users.filter(_.id === 2.bind).map(_.forUpdate)
      LOG.debug("Update: " + updateQ.updateStatement)
      updateQ.update(User(None, "Marge", "Simpson"))

      Query(users.filter(_.id === 1.bind).exists).first shouldEqual true
      users.filter(_.id === 1.bind).exists.run shouldEqual true

      users.filter(_.id between(1, 2)) foreach println
      LOG.debug("ID 3 ->" + users.byID(3).run)

      users.filter(_.id between(1, 2)).list.toSet shouldEqual
      Set(User(Some(1), "Homer", "Simpson"), User(Some(2), "Marge", "Simpson"))

      users.filter(_.id between(1, 2)).map(_.asFoo).list.toSet shouldEqual
      Set(Foo(User(None, "Homer", "Simpson")), Foo(User(None, "Marge", "Simpson")))

      users.byID(3).run.head shouldEqual User(Some(3), "Carl", "Carlson")

      // select x2."id", x2."first", x2."last" from "jdbc_mapper_users" x3, "jdbc_mapper_users" x2
      val q1 = for {
        u <- users
        u2 <- users
      } yield u2

      val r1 = q1.run.head
      r1.isInstanceOf[User] shouldEqual true
    }
  }

  test("update") {
    case class Data(a: Int, b: Int)

    class T(tag: Tag) extends Table[Data](tag, "jdbc_mapper_update") {
      def a = column[Int]("A")
      def b = column[Int]("B")
      def * = (a, b) <>(Data.tupled, Data.unapply)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.insertAll(Data(1, 2), Data(3, 4), Data(5, 6))

      val updateQ = ts.filter(_.a === 1.bind)
      updateQ.update(Data(7, 8))

      val updateQ2 = ts.filter(_.a === 3.bind).map(identity)
      updateQ2.update(Data(9, 10))

      ts.list.toSet shouldEqual Set(Data(7, 8), Data(9, 10), Data(5, 6))
    }
  }

  test("JPA Embeddable이나 Hibernate Component 처럼 여러 컬럼을 하나의 Component로 변환하기") {
    //
    // JPA의 Embeddable 을 변환하기 위한 예제입니다.
    //
    case class Part(i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int)
    case class Whole(id: Int, p1: Part, p2: Part, p3: Part, p4: Part)

    class T(tag: Tag) extends Table[Whole](tag, "t_wide") {
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

      def * = (id,
                (p1i1, p1i2, p1i3, p1i4, p1i5, p1i6),
                (p2i1, p2i2, p2i3, p2i4, p2i5, p2i6),
                (p3i1, p3i2, p3i3, p3i4, p3i5, p3i6),
                (p4i1, p4i2, p4i3, p4i4, p4i5, p4i6)
                ).shaped <>( {
        // tupled
        case (id, p1, p2, p3, p4) =>
          // We could do this without .shaped but then we'd have to write a type annotation for the parameters
          Whole(id, Part.tupled.apply(p1), Part.tupled.apply(p2), Part.tupled.apply(p3), Part.tupled.apply(p4))
      }, {
        // unapply
        w: Whole =>
          def f(p: Part) = Part.unapply(p).get
          Some(w.id, f(w.p1), f(w.p2), f(w.p3), f(w.p4))
      })
    }

    val ts = TableQuery[T]
    val oData = Whole(0,
                       Part(11, 12, 13, 14, 15, 16),
                       Part(21, 22, 23, 24, 25, 26),
                       Part(31, 32, 33, 34, 35, 35),
                       Part(41, 42, 43, 44, 45, 46)
                     )

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.insert(oData)

      ts.first shouldEqual oData
      ts.filter(_.p1i1 === 11.bind).first shouldEqual oData
      ts.filter(_.p1i1 === 99999.bind).length.run shouldEqual 0
      ts.filter(_.p1i1 === 11.bind).map(_.p1i1).first shouldEqual 11

      // 아래 custom shape 예제 처럼 하려면 implicit pairShape 메소드 같은 것을 정의해야 합니다.
      // ts.filter { case Whole(_, p1, _, _, _) => p1.i1 === 11.bind }.first shouldEqual oData
    }
  }

  test("mapped join") {
    case class A(id: Int, value: Int)
    case class B(id: Int, value: Option[String])

    class ARow(tag: Tag) extends Table[A](tag, "mapped_join_t4_a") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def data = column[Int]("data")
      def * = (id, data) <>(A.tupled, A.unapply)
    }
    val as = TableQuery[ARow]

    class BRow(tag: Tag) extends Table[B](tag, "mapped_join_t4_b") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def data = column[String]("data")
      def * = (id, data.?) <>(B.tupled, B.unapply)
    }
    val bs = TableQuery[BRow]

    val ddl = as.ddl ++ bs.ddl

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      as.map(_.data).insertAll(1, 2)
      bs.map(_.data).insertAll("a", "b")

      val q = for {
        a <- as if a.data === 2
        b <- bs if b.id === a.id
      } yield (a, b)

      q.run.toList shouldEqual List((A(2, 2), B(2, Some("b"))))
    }
  }

  test("custom shape") {
    // A custom record class (generic class)
    case class Pair[A, B](a: A, b: B)

    // A shape that maps Pair to a ProductNode
    final class PairShape[Level <: ShapeLevel, M <: Pair[_, _], U <: Pair[_, _] : ClassTag, P <: Pair[_, _]]
    (val shapes: Seq[Shape[_, _, _, _]])
      extends MappedScalaProductShape[Level, Pair[_, _], M, U, P] {
      def buildValue(elems: IndexedSeq[Any]) = Pair(elems(0), elems(1))
      def copy(shapes: Seq[Shape[_ <: ShapeLevel, _, _, _]]) = new PairShape(shapes)
    }

    implicit def pairShape[Level <: ShapeLevel, M1, M2, U1, U2, P1, P2]
    (implicit s1: Shape[_ <: Level, M1, U1, P1], s2: Shape[_ <: Level, M2, U2, P2]) =
      new PairShape[Level, Pair[M1, M2], Pair[U1, U2], Pair[P1, P2]](Seq(s1, s2))

    class A(tag: Tag) extends Table[Pair[Int, String]](tag, "mapper_custom_shape_a") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s")
      def * = Pair(id, s)
    }
    val as = TableQuery[A]

    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create

      // Insert data with the custom shape
      as += Pair(1, "a")
      as += Pair(2, "c")
      as += Pair(3, "b")

      val q2 = as
               .map { case a => Pair(a.id, a.s ++ a.s) }
               .filter { case Pair(id, _) => id =!= 1 }
               .sortBy { case Pair(_, ss) => ss }
               .map { case Pair(id, ss) => Pair(id, Pair(42, ss)) }

      q2.run shouldEqual Seq(Pair(3, Pair(42, "bb")), Pair(2, Pair(42, "cc")))
    }
  }

  test("레코드를 튜플이 아닌 리스트 형태로 표현") {
    import scala.slick.collection.heterogenous._
    import scala.slick.collection.heterogenous.syntax._

    class B(tag: Tag) extends Table[Int :: Boolean :: String :: HNil](tag, "mapped_hlist_b") {
      def id = column[Int]("id", O.PrimaryKey)
      def b = column[Boolean]("b")
      def s = column[String]("s")
      def * = id :: b :: s :: HNil
    }
    val bs = TableQuery[B]

    withSession { implicit session =>
      Try { bs.ddl.drop }
      bs.ddl.create

      bs += (1 :: true :: "a" :: HNil)
      bs += (2 :: false :: "c" :: HNil)
      bs += (3 :: false :: "b" :: HNil)

      val q1 = (
                 for {
                   id :: b :: s :: HNil <- for {b <- bs} yield b.id :: b.b :: b.s :: HNil if !b
                 } yield id :: b :: (s ++ s) :: HNil
                 )
               .sortBy(h => h(2))
               .map { case id :: b :: ss :: HNil => id :: ss :: (42 :: HNil) :: HNil }

      q1.run shouldEqual Seq(3 :: "bb" :: (42 :: HNil) :: HNil,
                              2 :: "cc" :: (42 :: HNil) :: HNil)
    }

  }

  test("single element") {
    import scala.slick.collection.heterogenous._
    import scala.slick.collection.heterogenous.syntax._

    class A(tag: Tag) extends Table[String](tag, "mapped_single_a") {
      def b = column[String]("b")
      def * = b
    }
    val as = TableQuery[A]

    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create

      as += "Foo"
      as.run.head shouldEqual "Foo"
      as.update("Foo")

      class B(tag: Tag) extends Table[Tuple1[String]](tag, "mapped_single_b") {
        def b = column[String]("b")
        def * = Tuple1(b)
      }
      val bs = TableQuery[B]

      Try { bs.ddl.drop }
      bs.ddl.create

      bs += Tuple1("Foo")
      bs.update(Tuple1("Foo"))
      val Tuple1(bres) = bs.run.head
      bres shouldEqual "Foo"

      class C(tag: Tag) extends Table[String :: HNil](tag, "mapped_single_c") {
        def b = column[String]("b")
        def * = b :: HNil
      }
      val cs = TableQuery[C]
      Try { cs.ddl.drop }
      cs.ddl.create
      cs += ("Foo" :: HNil)
      cs.update("Foo" :: HNil)
      val cres :: HNil = cs.run.head
      cres shouldEqual "Foo"
    }
  }

  /**
   * [[scala.slick.jdbc.JdbcFastPath]] 에 대한 예제
   */
  test("fast path") {
    case class Data(a: Int, b: Int)
    class T(tag: Tag) extends Table[Data](tag, "mapped_fastpath_t") {
      def a = column[Int]("A")
      def b = column[Int]("B")
      def * = (a, b) <>(Data.tupled, Data.unapply) fastPath (new FastPath(_) {
        val (a, b) = (next[Int], next[Int])
        // HINT: ResultSet 을 읽어, 다른 변환을 할 수 있도록 합니다.
        override def read(r: Reader) = Data(a.read(r), b.read(r))
      })
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.insertAll(Data(1, 2), Data(3, 4), Data(5, 6))

      val updateQ = ts.filter(_.a === 1.bind)
      updateQ.update(Data(7, 8))

      val updateQ2 = ts.filter(_.a === 3.bind).map(identity)
      updateQ2.update(Data(9, 10))

      ts.list.toSet shouldEqual Set(Data(7, 8), Data(9, 10), Data(5, 6))
    }
  }


}
