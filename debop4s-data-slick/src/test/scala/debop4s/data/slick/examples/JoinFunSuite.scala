package debop4s.data.slick.examples

import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._
import debop4s.data.slick.{ AbstractSlickFunSuite, SlickContext }

import scala.util.Try

/**
 * JoinFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JoinFunSuite extends AbstractSlickFunSuite {

  class Categories(tag: Tag) extends Table[(Int, String)](tag, "join_categories") {
    def id = column[Int]("id")
    def name = column[String]("name")
    def * = (id, name)
  }
  object Categories extends TableQuery(new Categories(_)) {
    val byID = this.findBy(_.id)
  }

  class Posts(tag: Tag) extends Table[(Int, String, Int)](tag, "join_posts") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def categoryId = column[Int]("categoryId")

    def * = (id, title, categoryId)
    // def categoryFK = foreignKey("fk_posts_categories", categoryId, Categories)(_.id)
  }
  object Posts extends TableQuery(new Posts(_)) {
    val byID = this.findBy(_.id)
    val byCategory = this.findBy(_.categoryId)
  }

  override def beforeAll(): Unit = {
    super.beforeAll()

    val ddl = Categories.ddl ++ Posts.ddl

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      Categories ++= Seq(
                          (1, "Scala"),
                          (2, "ScalaQuery"),
                          (3, "Windows"),
                          (4, "Software")
                        )
      Posts.map(p => (p.title, p.categoryId)) ++= Seq(
                                                       ("Test Post", -1),
                                                       ("Formal Language Processing in Scala, Part 5", 1),
                                                       ("Efficient Parameterized Queries in ScalaQuery", 2),
                                                       ("Removing Libraries and HomeGroup icons from the Windows 7 desktop", 3),
                                                       ("A ScalaQuery Update", 2)
                                                     )
    }
  }

  test("join") {

    withReadOnly { implicit session =>
      val q1 = ( for {
        c <- Categories
        p <- Posts if p.categoryId === c.id
      } yield (p.id, c.id, c.name, p.title) )
               .sortBy(_._1)

      LOG.debug("Implicit join")
      q1.run.foreach { x => LOG.debug(s"  $x") }
      q1.map(p => (p._1, p._2)).run shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

      val q2 = ( for {
        (c, p) <- Categories innerJoin Posts on ( _.id === _.categoryId )
      } yield (p.id, c.id, c.name, p.title) )
               .sortBy(_._1)

      LOG.debug("Explicit join")
      q2.run.foreach(x => LOG.debug(s"  $x"))
      q2.map(p => (p._1, p._2)).run shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

      val q3 = ( for {
        (c, p) <- Categories leftJoin Posts on ( _.id === _.categoryId )
      } yield (p.id, (p.id.?.getOrElse(0), c.id, c.name, p.title.?.getOrElse(""))) )
               .sortBy(_._1.nullsFirst)
               .map(_._2)

      LOG.debug("Left outer join (nulls first)")
      q3.run.foreach(x => LOG.debug(s"  $x"))
      q3.map(p => (p._1, p._2)).run shouldEqual Seq((0, 4), (2, 1), (3, 2), (4, 3), (5, 2))

      val q3a = ( for {
        (c, p) <- Categories leftJoin Posts on ( _.id === _.categoryId )
      } yield (p.id, c.id, c.name, p.title) ).sortBy(_._1.nullsFirst)

      // NOT NULL 컬럼에 NULL 값이 있으므로 예외가 발생한다. p.id.?.getOrElse(0) 를 써줘야 한다.
      intercept[Exception] { q3a.run }

      val q3b = ( for {
        (c, p) <- Categories leftJoin Posts on ( _.id === _.categoryId )
      } yield (p.id, (p.id.?.getOrElse(0), c.id, c.name, p.title.?.getOrElse(""))) )
                .sortBy(_._1.nullsLast)
                .map(_._2)

      LOG.debug("Left outer join (nulls last)")
      q3b.run.foreach(x => LOG.debug(s"  $x"))
      q3b.map(p => (p._1, p._2)).run shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2), (0, 4))

      val q4 = ( for {
        (c, p) <- Categories rightJoin Posts on ( _.id === _.categoryId )
      } yield (p.id, c.id.?.getOrElse(0), c.name.?.getOrElse(""), p.title) )
               .sortBy(_._1)

      LOG.debug("Right outer join")
      q4.run.foreach(x => LOG.debug(s"  $x"))
      q4.map(r => (r._1, r._2)).run shouldEqual Seq((1, 0), (2, 1), (3, 2), (4, 3), (5, 2))

      val q5 = ( for {
        (c, p) <- Categories outerJoin Posts on ( _.id === _.categoryId )
      } yield (p.id.?.getOrElse(0), c.id.?.getOrElse(0), c.name.?.getOrElse(""), p.title.?.getOrElse("")) )
               .sortBy(_._1)

      LOG.debug("Outer join")
      q5.run.foreach(x => LOG.debug(s"  $x"))
      q5.map(r => (r._1, r._2)).run shouldEqual Seq((0, 4), (1, 0), (2, 1), (3, 2), (4, 3), (5, 2))

    }
  }

  test("zip") {
    ifNotCap(rcap.zip) {
      cancel("zip 기능을 지원하지 않는 DB 입니다.")
    }

    withSession { implicit session =>
      // zipWithIndex 는 0부터 시작하는 인덱스를 추가해준다.

      // H2: select x2.x3, rownum - 1 from (select x4."id" as x3 from "join_categories" x4 order by x4."id") x2
      val q1 = for {
        (c, i) <- Categories.sortBy(_.id).zipWithIndex
      } yield (c.id, i)

      val r1 = q1.run
      r1.foreach(x => LOG.debug(s"  $x"))
      r1 shouldEqual Seq((1, 0), (2, 1), (3, 2), (4, 3))

      // FixMe: MariaDB 에서 Posts.sortBy(_.categoryId) 를 독립적으로 사용하면 되는데,
      // FixMe: zip 을 사용하면, sorting이 안된다. => MariaDB의 문제인가?
      // if(!SlickContext.isMySQL) {

      // H2:
      // select x2.x3, x4.x5
      // from (select x6.x7 as x3, rownum as x8
      //         from (select x9."id" as x7 from "join_categories" x9 o
      //         rder by x9."id") x6) x2
      // inner join (select x10.x11 as x5, rownum as x12
      //               from (select x13."categoryId" as x11 from "join_posts" x13 order by x13."categoryId") x10) x4
      //   on x2.x8 = x4.x12
      if (!SlickContext.isMySQL) {
        val q2 = for {
          (c, p) <- Categories.sortBy(_.id) zip Posts.sortBy(_.categoryId)
        } yield (c.id, p.categoryId)
        val r2 = q2.run
        r2.foreach(x => LOG.debug(s"  $x"))
        r2 shouldEqual Seq((1, -1), (2, 1), (3, 2), (4, 2))
      }
      // }

      // H2:
      // select x2.x3, x4.x5
      //   from (select x6."id" as x3, rownum as x7 from "join_categories" x6) x2
      //  inner join (select x8."categoryId" as x5, rownum as x9 from "join_posts" x8) x4
      //     on x2.x7 = x4.x9
      val q3 = for {
        (c, p) <- Categories zip Posts
      } yield (c.id, p.categoryId)
      val r3 = q3.run
      r3.foreach(x => LOG.debug(s"  $x"))
      r3 shouldEqual Seq((1, -1), (2, 1), (3, 2), (4, 3))

      // H2:
      // select x2.x3, x4.x5
      //   from (select x6."id" as x3, rownum as x7 from "join_categories" x6) x2
      //  inner join (select x8."categoryId" as x5, rownum as x9 from "join_posts" x8) x4
      //     on x2.x7 = x4.x9
      val q4 = for {
        res <- Categories.zipWith(Posts, (c: Categories, p: Posts) => (c.id, p.categoryId))
      } yield res

      val r4 = q4.run
      r4 foreach { x => LOG.debug(s"  $x") }
      r4 shouldEqual Seq((1, -1), (2, 1), (3, 2), (4, 3))

      // H2:
      // select x2.x3, rownum - 1
      //   from (select x4."id" as x3 from "join_categories" x4 order by x4."id") x2
      val q5 = for {
        (c, i) <- Categories.zipWithIndex
      } yield (c.id, i)

      val r5 = q1.run
      r5.foreach(x => LOG.debug(s"  $x"))
      r5 shouldEqual Seq((1, 0), (2, 1), (3, 2), (4, 3))

      val q6 = for {
        ((c, p), i) <- ( Categories zip Posts ).zipWithIndex
      } yield (c.id, p.categoryId, i)

      val r6 = q6.run
      r6 foreach { x => LOG.debug(s"  $x") }
      r6 shouldEqual Seq((1, -1, 0), (2, 1, 1), (3, 2, 2), (4, 3, 3))
    }
  }

  test("no join condition") {
    class T(tag: Tag) extends Table[Int](tag, "join_no_join_condition") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      // select x2.x3, x4.x5 from (select x6."id" as x3 from "join_no_join_condition" x6) x2 left outer join (select x7."id" as x5 from "join_no_join_condition" x7) x4
      val q1 = ts leftJoin ts
      q1.run

      // select x2.x3, x4.x5 from (select x6."id" as x3 from "join_no_join_condition" x6) x2 right outer join (select x7."id" as x5 from "join_no_join_condition" x7) x4
      val q2 = ts rightJoin ts
      q2.run

      // select x2.x3, x4.x5 from (select x6."id" as x3 from "join_no_join_condition" x6) x2 inner join (select x7."id" as x5 from "join_no_join_condition" x7) x4
      val q3 = ts innerJoin ts
      q3.run
    }
  }
}
