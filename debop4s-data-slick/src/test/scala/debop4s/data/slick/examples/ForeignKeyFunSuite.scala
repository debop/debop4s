package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * ForeignKeyFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ForeignKeyFunSuite extends AbstractSlickFunSuite {

  test("foreign key") {
    class Categories(tag: Tag) extends Table[(Int, String)](tag, "fk_t_categories") {
      def id = column[Int]("id", O.PrimaryKey)
      def name = column[String]("name")
      def * = (id, name)
    }
    lazy val categories = TableQuery[Categories]

    class Posts(tag: Tag) extends Table[(Int, String, Option[Int])](tag, "fk_t_posts") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def title = column[String]("title", O.Length(255, true))
      def categoryId = column[Option[Int]]("categoryId")

      def * = (id, title, categoryId)

      // 암시적인 join
      def categoryFK = foreignKey("fk_post_category", categoryId, TableQuery[Categories])(_.id)

      // 명시적인 join
      def categoryJoin = categories.filter(_.id === categoryId)
    }
    lazy val posts = TableQuery[Posts]

    lazy val ddl = categories.ddl ++ posts.ddl
    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      categories ++= Seq((1, "Scala"),
                          (2, "ScalaQuery"),
                          (3, "Windows"),
                          (4, "Software"))
      posts.map(p => (p.title, p.categoryId)) ++= Seq(
                                                       ("Test Post", None),
                                                       ("Formal Language Processing in Scala, Part 5", Some(1)),
                                                       ("Efficient Parameterized Queriies in ScalaQuery", Some(2)),
                                                       ("Removing Libraries and HomeGroup icons from the Windows 7 desktop", Some(3)),
                                                       ("A ScalaQuery Update", Some(2))
                                                     )
    }

    withReadOnly { implicit session =>
      // manual join
      val q1 = (
                 for {
                   p <- posts
                   c <- p.categoryJoin
                 } yield (p.id, c.id, c.name, p.title) )
               .sortBy(_._1)

      q1.map(r => (r._1, r._2)).run shouldEqual List((2, 1), (3, 2), (4, 3), (5, 2))

      // foreign key 에 의한 join
      val q2 = (
                 for {
                   p <- posts
                   c <- p.categoryFK
                 } yield (p.id, c.id, c.name, p.title) )
               .sortBy(_._1)
      q2.map(r => (r._1, r._2)).run shouldEqual List((2, 1), (3, 2), (4, 3), (5, 2))
    }
  }

  test("foreign key by multi columns") {
    class A(tag: Tag) extends Table[(Int, Int, String)](tag, "fk_t_a") {
      def k1 = column[Int]("k1")
      def k2 = column[Int]("k2")
      def s = column[String]("s")
      def * = (k1, k2, s)

      // NOTE : foreign key 정의 시 Action 에 따라 cascade 를 지정할 수 있다.
      def bFK = foreignKey("fk_fk_t_a_fk_t_b", (k1, k2), bs)(b => (b.f1, b.f2), onDelete = ForeignKeyAction.Cascade)
    }
    lazy val as = TableQuery[A]

    class B(tag: Tag) extends Table[(Int, Int, String)](tag, "fk_t_b") {
      def f1 = column[Int]("f1")
      def f2 = column[Int]("f2")
      def s = column[String]("s")
      def * = (f1, f2, s)
      def bIdx = index("ix_fk_t_b_f1_f2", (f1, f2), unique = true)
    }
    lazy val bs = TableQuery[B]

    val ddl = as.ddl ++ bs.ddl

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      // 테이블 Schema 정보를 얻을 수 있다.
      as.baseTableRow.foreignKeys foreach println
      as.baseTableRow.foreignKeys.map(_.name).toSet shouldEqual Set("fk_fk_t_a_fk_t_b")

      bs ++= Seq((1, 2, "b12"), (3, 4, "b34"), (5, 6, "b56"))
      as ++= Seq((1, 2, "a12"), (3, 4, "a34"))
    }
  }

  test("foreign key by combined join") {
    class A(tag: Tag) extends Table[(Int, String)](tag, "fk_combine_join_a") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s", O.Length(255, true))
      def * = (id, s)
    }
    lazy val as = TableQuery[A]

    class Dep(tag: Tag, n: String) extends Table[(Int, Int)](tag, n) {
      def id = column[Int]("id", O.PrimaryKey)
      def aRef = column[Int]("aRef")
      def * = (id, aRef)
      def a = foreignKey(s"fk_${ n }_a", aRef, as)(_.id)
    }

    val bs = TableQuery(new Dep(_, "b2"))
    val cs = TableQuery(new Dep(_, "c2"))

    val ddl = as.ddl ++ bs.ddl ++ cs.ddl
    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      as ++= Seq((1, "a"), (2, "b"), (3, "c"))
      bs ++= Seq((1, 1), (2, 1), (3, 2))
      cs ++= Seq((1, 1), (2, 3))
    }

    withReadOnly { implicit session =>
      val q1 = for {
        b <- bs
        a <- b.a
      } yield a.s
      q1.sorted.run shouldEqual Seq("a", "a", "b")

      val q2 = for {
        c <- cs
        a <- c.a
      } yield a.s
      q2.sorted.run shouldEqual Seq("a", "c")

      val q3 = for {
        b <- bs
        c <- cs
        a <- b.a & c.a // combine
      } yield a.s
      q3.sorted.run shouldEqual Seq("a", "a")
    }
  }

  /**
   * many-to-many 로 작업할 때의 가장 좋은 방법이다.
   */
  test("many to many") {
    class A(tag: Tag) extends Table[(Int, String)](tag, "fk_m2m_a3") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s", O.Length(255, true))
      def * = (id, s)
      // 이거 좋다.
      def bs = aToB.filter(_.aId === id).flatMap(_.bFK)
    }
    lazy val as = TableQuery[A]

    class B(tag: Tag) extends Table[(Int, String)](tag, "fk_m2m_b3") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s")
      def * = (id, s)
      // 이거 좋다.
      def as = aToB.filter(_.bId === id).flatMap(_.aFK)
    }
    lazy val bs = TableQuery[B]

    class AToB(tag: Tag) extends Table[(Int, Int)](tag, "fk_m2m_a3_to_b3") {
      def aId = column[Int]("aId", O.NotNull)
      def bId = column[Int]("bId", O.NotNull)
      def * = (aId, bId)

      def aFK = foreignKey("fk_m2m_atob_a3", aId, as)(a => a.id)
      def bFK = foreignKey("fk_m2m_atob_b3", bId, bs)(b => b.id)
    }
    lazy val aToB = TableQuery[AToB]

    lazy val ddl = as.ddl ++ bs.ddl ++ aToB.ddl

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      as ++= Seq(1 -> "a", 2 -> "b", 3 -> "c")
      bs ++= Seq(1 -> "x", 2 -> "y", 3 -> "z")
      aToB ++= Seq(1 -> 1, 1 -> 2, 2 -> 2, 2 -> 3)
    }

    withReadOnly { implicit session =>

      // select x2."s", x3."s"
      // from "fk_m2m_a3" x2, "fk_m2m_a3_to_b3" x4, "fk_m2m_b3" x3
      // where ((x2."id" >= 2) and (x4."aId" = x2."id"))
      //   and (x3."id" = x4."bId")
      val q1 = for {
        a <- as if a.id >= 2
        b <- a.bs
      } yield (a.s, b.s)

      q1.run.toSet shouldEqual Set(("b", "y"), ("b", "z"))
    }
  }
}
