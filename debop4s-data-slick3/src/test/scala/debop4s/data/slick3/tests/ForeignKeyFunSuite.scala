package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase.driver.api._

/**
 * ForeignKeyFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ForeignKeyFunSuite extends AbstractSlickFunSuite {

  test("simple") {
    class Categories(tag: Tag) extends Table[(Int, String)](tag, "categories") {
      def id = column[Int]("id", O.PrimaryKey)
      def name = column[String]("name")
      def * = (id, name)
    }
    val categories = TableQuery[Categories]

    class Posts(tag: Tag) extends Table[(Int, String, Option[Int])](tag, "posts") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def title = column[String]("title")
      def category = column[Option[Int]]("category")
      def * = (id, title, category)
      def categoryFK = foreignKey("category_fk", category, categories)(_.id.?)
      def categoryJoin = categories.filter(_.id === category)
    }
    val posts = TableQuery[Posts]

    val schema = categories.schema ++ posts.schema

    db.run(DBIO.seq(schema.drop.asTry, schema.create)).await

    db.run {
      categories ++= Seq((1, "Scala"), (2, "ScalaQuery"), (3, "Windows"), (4, "Software"))
    }.await

    db.run {
      posts.map(p => (p.title, p.category)) ++= Seq(
                                                     ("Test Post", None),
                                                     ("Formal Language Processing in Scala, Part 5", Some(1)),
                                                     ("Efficient Parameterized Queries in ScalaQuery", Some(2)),
                                                     ("Removing Libraries and HomeGroup icons from the Windows 7 desktop", Some(3)),
                                                     ("A ScalaQuery Update", Some(2))
                                                   )
    }.await

    val q1 = ( for {
      p <- posts
      c <- p.categoryJoin
    } yield (p.id, c.id, c.name, p.title) ).sortBy(_._1)

    db.run(q1.map(p => (p._1, p._2)).result).await shouldEqual List((2, 1), (3, 2), (4, 3), (5, 2))

    val q2 = ( for {
      p <- posts
      c <- p.categoryFK
    } yield (p.id, c.id, c.name, p.title) ).sortBy(_._1)

    db.run(q2.map(p => (p._1, p._2)).result).await shouldEqual List((2, 1), (3, 2), (4, 3), (5, 2))

    db.run(schema.drop).await
  }

}
