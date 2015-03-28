package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * Count 함수 테스트
 * @author sunghyouk.bae@gmail.com
 */
class CountFunSuite extends AbstractSlickFunSuite {

  test("count") {
    class TestTable(tag: Tag) extends Table[Int](tag, "count_test") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val testTable = TableQuery[TestTable]

    withSession { implicit session =>
      Try { testTable.ddl.drop }
      testTable.ddl.create

      testTable ++= Seq(1, 2, 3, 4, 5)
    }

    withReadOnly { implicit session =>
      Query(testTable.length).run shouldEqual Vector(5)

      testTable.length.run shouldEqual 5

      testTable.filter(_.id < 3).length.run shouldEqual 2

      testTable.take(2).length.run shouldEqual 2
    }
  }

  test("count with join") {
    class Categories(tag: Tag) extends Table[(Int, String)](tag, "count_join_categories") {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)
    }
    lazy val categories = TableQuery[Categories]
    class Posts(tag: Tag) extends Table[(Int, String, Int)](tag, "count_join_posts") {
      def id = column[Int]("id")
      def title = column[String]("title")
      def categoryId = column[Int]("categoryId")

      def * = (id, title, categoryId)
    }
    lazy val posts = TableQuery[Posts]

    lazy val ddl = categories.ddl ++ posts.ddl
    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      categories ++= Seq((1, "scala"), (2, "JVM"), (3, "Java"), (4, "Erlang"), (5, "Haskell"))
      posts ++= Seq((1, "Shiny features", 1), (2, "Hot Spot", 2))
    }

    withSession { implicit session =>
      val joinedQuery =
        for {
          c <- categories
          p <- posts if p.categoryId === c.id
        } yield (c, p)

      // select x2.x3 from (select count(1) as x3 from (select x4."id" as x5, x4."name" as x6, x7."id" as x8, x7."title" as x9, x7."categoryId" as x10 from "count_join_categories" x4, "count_join_posts" x7 where x7."categoryId" = x4."id") x11) x2
      joinedQuery.length.run shouldEqual 2

      // select x2.x3 from (select count(1) as x3 from (select x4."id" as x5, x4."name" as x6, x7."id" as x8, x7."title" as x9, x7."categoryId" as x10 from "count_join_categories" x4, "count_join_posts" x7 where x7."categoryId" = x4."id") x11) x2
      Query(joinedQuery.length).run shouldEqual Vector(2)

      // select x2.x3 from (select count(1) as x3 from (select x4."id" as x5, x4."name" as x6, x7."id" as x8, x7."title" as x9, x7."categoryId" as x10 from "count_join_categories" x4, "count_join_posts" x7 where x7."categoryId" = x4."id") x11) x2
      Compiled(joinedQuery.length).run shouldEqual 2
    }
  }
}
