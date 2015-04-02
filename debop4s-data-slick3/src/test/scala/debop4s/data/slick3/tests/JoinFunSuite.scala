package debop4s.data.slick3.tests

import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * JoinFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JoinFunSuite extends AbstractSlickFunSuite {

  test("join") {
    class Categories(tag: Tag) extends Table[(Int, String)](tag, "join_categories") {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)
    }
    lazy val categories = TableQuery[Categories]
    class Posts(tag: Tag) extends Table[(Int, String, Int)](tag, "join_posts") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def title = column[String]("title")
      def categoryId = column[Int]("categoryId")
      def * = (id, title, categoryId)
      def ins = (title, categoryId)
    }
    lazy val posts = TableQuery[Posts]

    val schema = categories.schema ++ posts.schema

    db.seq(
      schema.create,
      categories ++= Seq((1, "Scala"), (2, "ScalaQuery"), (3, "Windows"), (4, "Software")),
      posts.map(_.ins) ++= Seq(
        ("Test Post", -1),
        ("Formal Language Processing in Scala, Part 5", 1),
        ("Efficient Parameterized Queries in ScalaQuery", 2),
        ("Removing Libraries and HomeGroup icons from the Windows 7 desktop", 3),
        ("A ScalaQuery Update", 2)
      )
    )

    // Implicit join
    /*
    ┇ select x2."id", x3."id"
    ┇ from "join_categories" x3, "join_posts" x2
    ┇ where x2."categoryId" = x3."id"
    ┇ order by x2."id"
     */
    val q1 = (for {
      c <- categories
      p <- posts if p.categoryId === c.id
    } yield (p.id, c.id, c.name, p.title)).sortBy(_._1)

    db.result(q1.map(r => (r._1, r._2))) shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

    // Explicit inner join
    /*
    ┇ select x2.x3, x4.x5
    ┇ from (
    ┇   select x6."id" as x5, x6."name" as x7
    ┇   from "join_categories" x6
    ┇ ) x4
    ┇ inner join (
    ┇   select x8."id" as x3, x8."title" as x9, x8."categoryId" as x10
    ┇   from "join_posts" x8
    ┇ ) x2
    ┇ on x4.x5 = x2.x10
    ┇ order by x2.x3
     */
    val q2 = (for {
      (c, p) <- categories join posts on (_.id === _.categoryId)
    } yield (p.id, c.id, c.name, p.title)).sortBy(_._1)

    db.result(q2.map(r => (r._1, r._2))) shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

    // NOTE: Left outer join (null first)
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select (case when (x5.x6 = 1) then x5.x7 else null end) as x8, (case when ((case when (x5.x6 = 1) then x5.x7 else null end) is null) then 0 else (case when (x5.x6 = 1) then x5.x7 else null end) end) as x3, x9.x10 as x4, x9.x11 as x12, (case when ((case when (x5.x6 = 1) then x5.x13 else null end) is null) then '' else (case when (x5.x6 = 1) then x5.x13 else null end) end) as x14
    ┇   from (
    ┇     select x15."id" as x10, x15."name" as x11
    ┇     from "join_categories" x15
    ┇   ) x9
    ┇   left outer join (
    ┇     select 1 as x6, x16."id" as x7, x16."title" as x13, x16."categoryId" as x17
    ┇     from "join_posts" x16
    ┇   ) x5
    ┇   on x9.x10 = x5.x17
    ┇ ) x2
    ┇ order by x2.x8 nulls first
     */
    val q3 = (for {
      (c, p) <- categories joinLeft posts on (_.id === _.categoryId)
    } yield (p.map(_.id), (p.map(_.id).getOrElse(0), c.id, c.name, p.map(_.title).getOrElse("")))).sortBy(_._1.nullsFirst).map(_._2)

    db.result(q3.map(r => (r._1, r._2))) shouldEqual Seq((0, 4), (2, 1), (3, 2), (4, 3), (5, 2))

    // NOTE: Read NULL from non-nullable column
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select (case when (x5.x6 = 1) then x5.x7 else null end) as x3, x8.x9 as x4
    ┇   from (
    ┇     select x10."id" as x9, x10."name" as x11
    ┇     from "join_categories" x10
    ┇   ) x8
    ┇   left outer join (
    ┇     select 1 as x6, x12."id" as x7, x12."title" as x13, x12."categoryId" as x14
    ┇     from "join_posts" x12
    ┇   ) x5
    ┇   on x8.x9 = x5.x14
    ┇ ) x2
    ┇ order by x2.x3 nulls first
     */
    val q3a = (for {
      (c, p) <- categories joinLeft posts on (_.id === _.categoryId)
    } yield (p.map(_.id), c.id, c.name, p.map(_.title))).sortBy(_._1.nullsFirst)

    db.result(q3a.map(r => (r._1, r._2))) shouldBe List((None, 4), (Some(2), 1), (Some(3), 2), (Some(4), 3), (Some(5), 2))

    // NOTE: Left outer join (null last)
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select (case when (x5.x6 = 1) then x5.x7 else null end) as x8, (case when ((case when (x5.x6 = 1) then x5.x7 else null end) is null) then 0 else (case when (x5.x6 = 1) then x5.x7 else null end) end) as x3, x9.x10 as x4, x9.x11 as x12, (case when ((case when (x5.x6 = 1) then x5.x13 else null end) is null) then '' else (case when (x5.x6 = 1) then x5.x13 else null end) end) as x14
    ┇   from (
    ┇     select x15."id" as x10, x15."name" as x11
    ┇     from "join_categories" x15
    ┇   ) x9
    ┇   left outer join (
    ┇     select 1 as x6, x16."id" as x7, x16."title" as x13, x16."categoryId" as x17
    ┇     from "join_posts" x16
    ┇   ) x5
    ┇   on x9.x10 = x5.x17
    ┇ ) x2
    ┇ order by x2.x8 nulls last
     */
    val q3b = (for {
      (c, p) <- categories joinLeft posts on (_.id === _.categoryId)
    } yield (p.map(_.id), (p.map(_.id).getOrElse(0), c.id, c.name, p.map(_.title).getOrElse("")))).sortBy(_._1.nullsLast).map(_._2)

    db.result(q3b.map(r => (r._1, r._2))) shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2), (0, 4))


    // NOTE: Right outer join
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select x5.x6 as x3, (case when ((case when (x7.x8 = 1) then x7.x9 else null end) is null) then 0 else (case when (x7.x8 = 1) then x7.x9 else null end) end) as x4
    ┇   from (
    ┇     select 1 as x8, x10."id" as x9, x10."name" as x11
    ┇     from "join_categories" x10
    ┇   ) x7
    ┇   right outer join (
    ┇     select x12."id" as x6, x12."title" as x13, x12."categoryId" as x14
    ┇     from "join_posts" x12
    ┇   ) x5
    ┇   on x7.x9 = x5.x14
    ┇ ) x2
    ┇ order by x2.x3
     */
    val q4 = (for {
      (c, p) <- categories joinRight posts on (_.id === _.categoryId)
    } yield (p.id, c.map(_.id).getOrElse(0), c.map(_.name).getOrElse(""), p.title)).sortBy(_._1)

    db.result(q4.map(r=>(r._1, r._2))) shouldBe List((1, 0), (2, 1), (3, 2), (4, 3), (5, 2))


    // NOTE: Full outer join
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select (case when ((case when (x5.x6 = 1) then x5.x7 else null end) is null) then 0 else (case when (x5.x6 = 1) then x5.x7 else null end) end) as x3, (case when ((case when (x5.x8 = 1) then x5.x9 else null end) is null) then 0 else (case when (x5.x8 = 1) then x5.x9 else null end) end) as x4
    ┇   from (
    ┇     select x10.x11 as x8, x10.x12 as x9, x10.x13 as x14, x15.x16 as x6, x15.x17 as x7, x15.x18 as x19
    ┇     from (
    ┇       select 1 as x11, x20."id" as x12, x20."name" as x13
    ┇       from "join_categories" x20
    ┇     ) x10
    ┇     left outer join (
    ┇       select 1 as x16, x21."id" as x17, x21."title" as x18, x21."categoryId" as x22
    ┇       from "join_posts" x21
    ┇     ) x15
    ┇     on x10.x12 = x15.x22
    ┇     union all select null as x8, null as x9, null as x14, 1 as x6, x23."id" as x7, x23."title" as x19
    ┇     from "join_posts" x23
    ┇     where not exists(
    ┇       select 1, x24."id", x24."name"
    ┇       from "join_categories" x24
    ┇       where x24."id" = x23."categoryId"
    ┇     )
    ┇   ) x5
    ┇ ) x2
    ┇ order by x2.x3
     */
    val q5 = (for {
      (c, p) <- categories joinFull posts on (_.id === _.categoryId)
    } yield (p.map(_.id).getOrElse(0), c.map(_.id).getOrElse(0), c.map(_.name).getOrElse(""), p.map(_.title).getOrElse(""))).sortBy(_._1)

    db.result(q5.map(r=>(r._1, r._2))) shouldBe List((0, 4), (1, 0), (2, 1), (3, 2), (4, 3), (5, 2))

    db.exec { schema.drop }
  }

  test("optionn extended join") {

  }
}
