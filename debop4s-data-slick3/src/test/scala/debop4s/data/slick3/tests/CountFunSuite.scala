package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase.driver.api._

import debop4s.data.slick3._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * CountFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class CountFunSuite extends AbstractSlickFunSuite {

  test("simple count") {
    class T(tag: Tag) extends Table[Int](tag, "count_simple") {
      def id = column[Int]("id")
      def * = id
    }
    val ts = TableQuery[T]
    db.exec {
      ts.schema.create >>
      (ts ++= Seq(1, 2, 3, 4, 5))
    }

    db.exec(Query(ts.length).result) shouldBe Vector(5)
    db.exec(ts.length.result) shouldEqual 5
    db.exec(ts.filter(_.id < 3).length.result) shouldEqual 2
    db.exec(ts.take(2).length.result) shouldEqual 2

    db.exec {ts.schema.drop}
  }

  test("count - join") {
    class Categories(tag: Tag) extends Table[(Int, String)](tag, "count_join_categories") {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)
    }
    val categories = TableQuery[Categories]

    class Posts(tag: Tag) extends Table[(Int, String, Int)](tag, "count_join_posts") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def title = column[String]("title")
      def category = column[Int]("category")
      def * = (id, title, category)
    }
    val posts = TableQuery[Posts]

    val schema = categories.schema ++ posts.schema
    db.seq(
      schema.drop.asTry,
      schema.create,
      categories ++= Seq((1, "Scala"), (2, "JVM"), (3, "Java"), (4, "Erlang"), (5, "Haskell")),
      posts ++= Seq((1, "Shiny features", 1), (2, "HotSopt", 2))
    )

    val joinedQuery = for {
      c <- categories
      p <- posts if p.category === c.id
    } yield (c, p)
    val q1 = joinedQuery.length
    val q2 = Query(joinedQuery.length)

    db.seq(
      q1.result.map(_ shouldEqual 2),
      q2.result.map(_ shouldEqual Vector(2))
    )


    db.exec {schema.drop}
  }

  test("count - join 2") {
    class A(tag: Tag) extends Table[Long](tag, "count_join_a") {
      def id = column[Long]("id", O.PrimaryKey)
      def * = id
    }
    lazy val as = TableQuery[A]
    class B(tag: Tag) extends Table[(Long, String)](tag, "count_join_b") {
      def aId = column[Long]("a_id")
      def data = column[String]("data")
      def * = (aId, data)
    }
    lazy val bs = TableQuery[B]

    lazy val schema = as.schema ++ bs.schema
    db.seq(
      schema.drop.asTry,
      schema.create,
      as ++= Seq(1L, 2L),
      bs ++= Seq((1L, "1a"), (1L, "1b"), (2L, "2"))
    )

    val qDirectLength = for {
      a <- as if a.id === 1L
    } yield (a, (for {
        b <- bs if b.aId === a.id
      } yield b).length)

    val qJoinLength = for {
      a <- as if a.id === 1L
      l <- Query((for {b <- bs if b.aId === a.id} yield b).length)
    } yield (a, l)

    val qOuterJoinLength = (for {
      (a, b) <- as joinLeft bs on (_.id === _.aId)
    } yield (a.id, b.map(_.data))).length

    db.exec(qDirectLength.result) shouldEqual Seq((1L, 2))
    db.exec(qJoinLength.result) shouldEqual Seq((1L, 2))
    db.exec(qOuterJoinLength.result) shouldBe 3

    db.exec(schema.drop)
  }
}
