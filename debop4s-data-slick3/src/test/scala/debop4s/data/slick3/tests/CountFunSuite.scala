package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase.driver.api._

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
    db.run(DBIO.seq(
                     ts.schema.create,
                     ts ++= Seq(1, 2, 3, 4, 5)
                   ))

    db.run(Query(ts.length).result.map(_ shouldBe Vector(5))).await
    db.run(ts.length.result.map(_ shouldEqual 5)).await
    db.run(ts.filter(_.id < 3).length.result.map(_ shouldEqual 2)).await
    db.run(ts.take(2).length.result.map(_ shouldEqual 2)).await

    db.run(ts.schema.drop).await
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
    db.run(schema.drop.asTry).await
    db.run(schema.create).await

    db.run(categories ++= Seq((1, "Scala"), (2, "JVM"), (3, "Java"), (4, "Erlang"), (5, "Haskell"))).await
    db.run(posts ++= Seq((1, "Shiny features", 1), (2, "HotSopt", 2))).await

    val joinedQuery = for {
      c <- categories
      p <- posts if p.category === c.id
    } yield (c, p)
    val q1 = joinedQuery.length
    val q2 = Query(joinedQuery.length)

    db.run(DBIO.seq(
                     q1.result.map(_ shouldEqual 2),
                     q2.result.map(_ shouldEqual Vector(2))
                   ))


    db.run(schema.drop).await
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
    db.run(schema.drop.asTry).await
    db.run(schema.create).await

    db.run(DBIO.seq(
                     as ++= Seq(1L, 2L),
                     bs ++= Seq((1L, "1a"), (1L, "1b"), (2L, "2"))
                   )).await

    val qDirectLength = for {
      a <- as if a.id === 1L
    } yield (a, ( for {
        b <- bs if b.aId === a.id
      } yield b ).length)

    val qJoinLength = for {
      a <- as if a.id === 1L
      l <- Query(( for {b <- bs if b.aId === a.id} yield b ).length)
    } yield (a, l)

    val qOuterJoinLength = ( for {
      (a, b) <- as joinLeft bs on ( _.id === _.aId )
    } yield (a.id, b.map(_.data)) ).length

    db.run(qDirectLength.result.map(_ shouldBe Seq((1L, 2)))).await
    db.run(qJoinLength.result.map(_ shouldBe Seq((1L, 2)))).await
    db.run(qOuterJoinLength.result.map(_ shouldBe 3)).await


    db.run(schema.drop).await
  }
}
