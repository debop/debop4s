package debop4s.data.slick3.tests

import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
      schema.drop.asTry,
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

    q1.map(r => (r._1, r._2)).exec shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

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

    q2.map(r => (r._1, r._2)).exec shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

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

    q3.map(r => (r._1, r._2)).exec shouldEqual Seq((0, 4), (2, 1), (3, 2), (4, 3), (5, 2))

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

    q3a.map(r => (r._1, r._2)).exec shouldEqual List((None, 4), (Some(2), 1), (Some(3), 2), (Some(4), 3), (Some(5), 2))

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

    q3b.map(r => (r._1, r._2)).exec shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2), (0, 4))


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

    q4.map(r => (r._1, r._2)).exec shouldEqual List((1, 0), (2, 1), (3, 2), (4, 3), (5, 2))


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

    q5.map(r => (r._1, r._2)).exec shouldEqual List((0, 4), (1, 0), (2, 1), (3, 2), (4, 3), (5, 2))

    schema.drop.exec
  }

  test("option extended join") {

    class Data(name: String)(tag: Tag) extends Table[(Int, String)](tag, name) {
      def a = column[Int]("a")
      def b = column[String]("b")
      def * = (a, b)
    }
    val xs = TableQuery(new Data("join_xs")(_))
    val ys = TableQuery(new Data("join_ys")(_))

    val schema = xs.schema ++ ys.schema

    db.seq(
      schema.drop.asTry,
      schema.create,
      xs ++= Seq((1, "a"), (2, "b"), (3, "b"), (4, "c"), (5, "c")),
      ys ++= Seq((1, "a"), (2, "b"), (3, "b"), (4, "d"), (5, "d"))
    )

    // Left outer, lift primitive value
    /*
    ┇ select x2.x3, x4.x5
    ┇ from (
    ┇   select x6."b" as x3
    ┇   from "join_xs" x6
    ┇ ) x2
    ┇ left outer join (
    ┇   select x7."b" as x5
    ┇   from "join_ys" x7
    ┇ ) x4
    ┇ on x2.x3 = x4.x5
     */
    val q1 = (xs.map(_.b) joinLeft ys.map(_.b) on (_ === _)).to[Set]
    q1.exec shouldEqual Set(("a", Some("a")), ("b", Some("b")), ("c", None))

    // Nested left outer, lift primitive value
    /*
    ┇ select x2.x3, x2.x4, x2.x5
    ┇ from (
    ┇   select x6.x7 as x3, x6.x8 as x4, x9.x10 as x5
    ┇   from (
    ┇     select x11.x12 as x7, x13.x14 as x8
    ┇     from (
    ┇       select x15."b" as x12
    ┇       from "join_xs" x15
    ┇     ) x11
    ┇     left outer join (
    ┇       select x16."b" as x14
    ┇       from "join_ys" x16
    ┇     ) x13
    ┇     on x11.x12 = x13.x14
    ┇   ) x6
    ┇   left outer join (
    ┇     select x17."b" as x10
    ┇     from "join_ys" x17
    ┇   ) x9
    ┇   on x6.x7 = x9.x10
    ┇ ) x2
     */
    val q2 = ((xs.map(_.b) joinLeft ys.map(_.b) on (_ === _)) joinLeft ys.map(_.b) on (_._1 === _)).to[Set]
    q2.exec shouldEqual Set((("a", Some("a")), Some("a")), (("b", Some("b")), Some("b")), (("c", None), None))

    // Left outer, lift non-primitive value
    /*
    ┇ select x2.x3, x2.x4, x2.x5, x2.x6, x2.x7
    ┇ from (
    ┇   select x8.x9 as x3, x8.x10 as x4, x11.x12 as x5, x11.x13 as x6, x11.x14 as x7
    ┇   from (
    ┇     select x15."a" as x9, x15."b" as x10
    ┇     from "join_xs" x15
    ┇   ) x8
    ┇   left outer join (
    ┇     select 1 as x12, x16."a" as x13, x16."b" as x14
    ┇     from "join_ys" x16
    ┇   ) x11
    ┇   on x8.x10 = x11.x14
    ┇ ) x2
     */
    val q3 = (xs joinLeft ys on (_.b === _.b)).to[Set]
    q3.exec shouldBe Set(
      ((1, "a"), Some((1, "a"))),
      ((2, "b"), Some((2, "b"))),
      ((2, "b"), Some((3, "b"))),
      ((3, "b"), Some((2, "b"))),
      ((3, "b"), Some((3, "b"))),
      ((4, "c"), None),
      ((5, "c"), None)
    )

    // Left outer, lift non-primitive value, then map to primitive
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select x5.x6 as x3, (case when (x7.x8 = 1) then x7.x9 else null end) as x4
    ┇   from (
    ┇     select x10."a" as x6, x10."b" as x11
    ┇     from "join_xs" x10
    ┇   ) x5
    ┇   left outer join (
    ┇     select 1 as x8, x12."a" as x9, x12."b" as x13
    ┇     from "join_ys" x12
    ┇   ) x7
    ┇   on x5.x11 = x7.x13
    ┇ ) x2
     */
    val q4 = (xs joinLeft ys on (_.b === _.b)).map { case (x, yo) => (x.a, yo.map(_.a)) }.to[Set]
    q4.exec shouldBe Set(
      (1, Some(1)),
      (2, Some(2)),
      (2, Some(3)),
      (3, Some(2)),
      (3, Some(3)),
      (4, None),
      (5, None)
    )

    // Nested left outer, lift non-primitive value
    /*
    ┇ select x2.x3, x2.x4, x2.x5, x2.x6, x2.x7, x2.x8, x2.x9, x2.x10
    ┇ from (
    ┇   select x11.x12 as x3, x11.x13 as x4, x11.x14 as x5, x11.x15 as x6, x11.x16 as x7, x17.x18 as x8, x17.x19 as x9, x17.x20 as x10
    ┇   from (
    ┇     select x21.x22 as x12, x21.x23 as x13, x24.x25 as x14, x24.x26 as x15, x24.x27 as x16
    ┇     from (
    ┇       select x28."a" as x22, x28."b" as x23
    ┇       from "join_xs" x28
    ┇     ) x21
    ┇     left outer join (
    ┇       select 1 as x25, x29."a" as x26, x29."b" as x27
    ┇       from "join_ys" x29
    ┇     ) x24
    ┇     on x21.x23 = x24.x27
    ┇   ) x11
    ┇   left outer join (
    ┇     select 1 as x18, x30."a" as x19, x30."b" as x20
    ┇     from "join_ys" x30
    ┇   ) x17
    ┇   on x11.x13 = x17.x20
    ┇ ) x2
     */
    val q5 = ((xs joinLeft ys on (_.b === _.b)) joinLeft ys on (_._1.b === _.b)).to[Set]
    q5.exec shouldBe Set(
      (((1, "a"), Some((1, "a"))), Some((1, "a"))),
      (((2, "b"), Some((2, "b"))), Some((2, "b"))),
      (((2, "b"), Some((2, "b"))), Some((3, "b"))),
      (((2, "b"), Some((3, "b"))), Some((2, "b"))),
      (((2, "b"), Some((3, "b"))), Some((3, "b"))),
      (((3, "b"), Some((2, "b"))), Some((2, "b"))),
      (((3, "b"), Some((2, "b"))), Some((3, "b"))),
      (((3, "b"), Some((3, "b"))), Some((2, "b"))),
      (((3, "b"), Some((3, "b"))), Some((3, "b"))),
      (((4, "c"), None), None),
      (((5, "c"), None), None)
    )

    // Right outer, lift primitive value
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select x5.x6 as x3, x7.x8 as x4
    ┇   from (
    ┇     select x9."b" as x6
    ┇     from "join_ys" x9
    ┇   ) x5
    ┇   right outer join (
    ┇     select x10."b" as x8
    ┇     from "join_xs" x10
    ┇   ) x7
    ┇   on x5.x6 = x7.x8
    ┇ ) x2
     */
    val q6 = (ys.map(_.b) joinRight xs.map(_.b) on (_ === _)).to[Set]
    q6.exec shouldBe Set((Some("a"), "a"), (Some("b"), "b"), (None, "c"))

    // Nested right outer, lift primitive value
    // (left-associative; non symmetrical to then nested left outer case)
    /*
    ┇ select x2.x3, x2.x4, x2.x5, x2.x6
    ┇ from (
    ┇   select x7.x8 as x3, x7.x9 as x4, x7.x10 as x5, x11.x12 as x6
    ┇   from (
    ┇     select 1 as x8, x13.x14 as x9, x15.x16 as x10
    ┇     from (
    ┇       select x17."b" as x14
    ┇       from "join_ys" x17
    ┇     ) x13
    ┇     right outer join (
    ┇       select x18."b" as x16
    ┇       from "join_xs" x18
    ┇     ) x15
    ┇     on x13.x14 = x15.x16
    ┇   ) x7
    ┇   right outer join (
    ┇     select x19."b" as x12
    ┇     from "join_xs" x19
    ┇   ) x11
    ┇   on x7.x10 = x11.x12
    ┇ ) x2
     */
    val q7 = ((ys.map(_.b) joinRight xs.map(_.b) on (_ === _)) joinRight xs.map(_.b) on (_._2 === _)).to[Set]
    q7.exec shouldBe Set(
      (Some((Some("a"), "a")), "a"),
      (Some((Some("b"), "b")), "b"),
      (Some((None, "c")), "c")
    )

    // Right outer, lift non-primitive value
    /*
    ┇ select x2.x3, x2.x4, x2.x5, x2.x6, x2.x7
    ┇ from (
    ┇   select x8.x9 as x3, x8.x10 as x4, x8.x11 as x5, x12.x13 as x6, x12.x14 as x7
    ┇   from (
    ┇     select 1 as x9, x15."a" as x10, x15."b" as x11
    ┇     from "join_ys" x15
    ┇   ) x8
    ┇   right outer join (
    ┇     select x16."a" as x13, x16."b" as x14
    ┇     from "join_xs" x16
    ┇   ) x12
    ┇   on x8.x11 = x12.x14
    ┇ ) x2
     */
    val q8 = (ys joinRight xs on (_.b === _.b)).to[Set]
    q8.exec shouldBe Set(
      (Some((1, "a")), (1, "a")),
      (Some((2, "b")), (2, "b")),
      (Some((2, "b")), (3, "b")),
      (Some((3, "b")), (2, "b")),
      (Some((3, "b")), (3, "b")),
      (None, (4, "c")),
      (None, (5, "c"))
    )

    // Right outer, lift non-primitive value, then map to primitive
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select (case when (x5.x6 = 1) then x5.x7 else null end) as x3, x8.x9 as x4
    ┇   from (
    ┇     select 1 as x6, x10."a" as x7, x10."b" as x11
    ┇     from "join_ys" x10
    ┇   ) x5
    ┇   right outer join (
    ┇     select x12."a" as x9, x12."b" as x13
    ┇     from "join_xs" x12
    ┇   ) x8
    ┇   on x5.x11 = x8.x13
    ┇ ) x2
     */
    val q9 = (ys joinRight xs on (_.b === _.b)).map { case (yo, x) => (yo.map(_.a), x.a) }.to[Set]
    q9.exec shouldBe Set(
      (Some(1), 1),
      (Some(2), 2),
      (Some(2), 3),
      (Some(3), 2),
      (Some(3), 3),
      (None, 4),
      (None, 5)
    )

    // Nested right outer, lift non-primitives value
    // (left-associative, not symmetrical to the nsted left outer case)
    /*
    ┇ select x2.x3, x2.x4, x2.x5, x2.x6, x2.x7, x2.x8, x2.x9, x2.x10
    ┇ from (
    ┇   select x11.x12 as x3, x11.x13 as x4, x11.x14 as x5, x11.x15 as x6, x11.x16 as x7, x11.x17 as x8, x18.x19 as x9, x18.x20 as x10
    ┇   from (
    ┇     select 1 as x12, x21.x22 as x13, x21.x23 as x14, x21.x24 as x15, x25.x26 as x16, x25.x27 as x17
    ┇     from (
    ┇       select 1 as x22, x28."a" as x23, x28."b" as x24
    ┇       from "join_ys" x28
    ┇     ) x21
    ┇     right outer join (
    ┇       select x29."a" as x26, x29."b" as x27
    ┇       from "join_xs" x29
    ┇     ) x25
    ┇     on x21.x24 = x25.x27
    ┇   ) x11
    ┇   right outer join (
    ┇     select x30."a" as x19, x30."b" as x20
    ┇     from "join_xs" x30
    ┇   ) x18
    ┇   on (case when (x11.x13 = 1) then x11.x15 else null end) = x18.x20
    ┇ ) x2
     */
    val q10 = ((ys joinRight xs on (_.b === _.b)) joinRight xs on (_._1.map(_.b) === _.b)).to[Set]
    q10.exec shouldBe Set(
      (Some((Some((1, "a")), (1, "a"))), (1, "a")),
      (Some((Some((2, "b")), (2, "b"))), (2, "b")),
      (Some((Some((2, "b")), (2, "b"))), (3, "b")),
      (Some((Some((2, "b")), (3, "b"))), (2, "b")),
      (Some((Some((2, "b")), (3, "b"))), (3, "b")),
      (Some((Some((3, "b")), (2, "b"))), (2, "b")),
      (Some((Some((3, "b")), (2, "b"))), (3, "b")),
      (Some((Some((3, "b")), (3, "b"))), (2, "b")),
      (Some((Some((3, "b")), (3, "b"))), (3, "b")),
      (None, (4, "c")),
      (None, (5, "c"))
    )

    // Full outer, lift primitive values
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select x5.x6 as x3, x5.x7 as x4
    ┇   from (
    ┇     select x8.x9 as x6, x10.x11 as x7
    ┇     from (
    ┇       select x12."b" as x9
    ┇       from "join_xs" x12
    ┇     ) x8
    ┇     left outer join (
    ┇       select x13."b" as x11
    ┇       from "join_ys" x13
    ┇     ) x10
    ┇     on x8.x9 = x10.x11
    ┇     union all select null as x6, x14."b" as x7
    ┇     from "join_ys" x14
    ┇     where not exists(
    ┇       select x15."b"
    ┇       from "join_xs" x15
    ┇       where x15."b" = x14."b"
    ┇     )
    ┇   ) x5
    ┇ ) x2
     */
    val q11 = (xs.map(_.b) joinFull ys.map(_.b) on (_ === _)).to[Set]
    q11.exec shouldBe Set(
      (Some("a"), Some("a")),
      (Some("b"), Some("b")),
      (Some("c"), None),
      (None, Some("d"))
    )

    // Full outer, lift non-primitive values
    /*
    ┇ select x2.x3, x2.x4, x2.x5, x2.x6, x2.x7, x2.x8
    ┇ from (
    ┇   select x9.x10 as x3, x9.x11 as x4, x9.x12 as x5, x9.x13 as x6, x9.x14 as x7, x9.x15 as x8
    ┇   from (
    ┇     select x16.x17 as x10, x16.x18 as x11, x16.x19 as x12, x20.x21 as x13, x20.x22 as x14, x20.x23 as x15
    ┇     from (
    ┇       select 1 as x17, x24."a" as x18, x24."b" as x19
    ┇       from "join_xs" x24
    ┇     ) x16
    ┇     left outer join (
    ┇       select 1 as x21, x25."a" as x22, x25."b" as x23
    ┇       from "join_ys" x25
    ┇     ) x20
    ┇     on x16.x19 = x20.x23
    ┇     union all select null as x10, null as x11, null as x12, 1 as x13, x26."a" as x14, x26."b" as x15
    ┇     from "join_ys" x26
    ┇     where not exists(
    ┇       select 1, x27."a", x27."b"
    ┇       from "join_xs" x27
    ┇       where x27."b" = x26."b"
    ┇     )
    ┇   ) x9
    ┇ ) x2
     */
    if (!SlickContext.isMySQL) {
      val q12 = (xs joinFull ys on (_.b === _.b)).to[Set]
      q12.exec shouldBe Set(
        (Some((1, "a")), Some((1, "a"))),
        (Some((2, "b")), Some((2, "b"))),
        (Some((2, "b")), Some((3, "b"))),
        (Some((3, "b")), Some((2, "b"))),
        (Some((3, "b")), Some((3, "b"))),
        (Some((4, "c")), None),
        (Some((5, "c")), None),
        (None, Some((4, "d"))),
        (None, Some((5, "d")))
      )
    }

    db.exec(schema.drop)
  }

  test("computed star projection") {
    class X(tag: Tag) extends Table[(Int, Int)](tag, "star_proj_x") {
      def a = column[Int]("a")
      def b = column[Int]("b", O.Default(2))
      def * = (a, b * 10)
    }
    val xs = TableQuery[X]
    db.seq(
      xs.schema.create,
      xs.map(_.a) += 1
    )

    /*
    ┇ select x2.x3, x2.x4 * 10, x5.x6, x5.x7, (x5.x8 * 10)
    ┇ from (
    ┇   select x9."a" as x3, x9."b" as x4
    ┇   from "star_proj_x" x9
    ┇ ) x2
    ┇ left outer join (
    ┇   select 1 as x6, x10."a" as x7, x10."b" as x8
    ┇   from "star_proj_x" x10
    ┇ ) x5
     */
    val q1 = xs joinLeft xs
    db.result(q1) shouldBe Vector(((1, 20), Some((1, 20))))

    xs.schema.drop.exec
  }

  test("zip") {
    ifNotCapF(rcap.zip) {
      cancel("not support zip")
      Future {}
    }

    class Categories(tag: Tag) extends Table[(Int, String)](tag, "cat_z") {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)
    }
    lazy val categories = TableQuery[Categories]

    class Posts(tag: Tag) extends Table[(Int, String, Int)](tag, "post_z") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def title = column[String]("title")
      def categoryId = column[Int]("categoryId")
      def * = (id, title, categoryId)
    }
    lazy val posts = TableQuery[Posts]

    val schema = categories.schema ++ posts.schema

    db.exec {
      schema.drop.asTry >>
      schema.create >>
      (categories ++= Seq((1, "Scala"), (3, "Windows"), (2, "ScalaQuery"), (4, "Software"))) >>
      (posts.map(p => (p.title, p.categoryId)) ++= Seq(
        ("Test Post", -1),
        ("Formal Language Processing in Scala, Part 5", 1),
        ("Efficient Parameterized Queries in ScalaQuery", 2),
        ("Removing Libraries and HomeGroup icons from the Windows 7 desktop", 3),
        ("A ScalaQuery Update", 2)
      ))
    }

    /*
    ┇ select x2.x3, rownum - 1
    ┇ from (
    ┇   select x4."id" as x3
    ┇   from "cat_z" x4
    ┇   order by x4."id"
    ┇ ) x2
     */
    if (!SlickContext.isMySQL) {
      val q1 = for {
        (c, i) <- categories.sortBy(_.id).zipWithIndex
      } yield (c.id, i)
      q1.exec shouldEqual List((1, 0), (2, 1), (3, 2), (4, 3))
    }

    /*
    ┇ select x2.x3, x4.x5
    ┇ from (
    ┇   select x6.x7 as x3, rownum as x8
    ┇   from (
    ┇     select x9."id" as x7
    ┇     from "cat_z" x9
    ┇     order by x9."id"
    ┇   ) x6
    ┇ ) x2
    ┇ inner join (
    ┇   select x10.x11 as x5, rownum as x12
    ┇   from (
    ┇     select x13."categoryId" as x11
    ┇     from "post_z" x13
    ┇     order by x13."categoryId"
    ┇   ) x10
    ┇ ) x4
    ┇ on x2.x8 = x4.x12
     */
    if (!SlickContext.isMySQL) {
      val q2 = for {
        (c, p) <- categories.sortBy(_.id) zip posts.sortBy(_.categoryId)
      } yield (c.id, p.categoryId)
      q2.exec shouldEqual List((1, -1), (2, 1), (3, 2), (4, 2))
    }

    /*
    ┇ select x2.x3, x4.x5
    ┇ from (
    ┇   select x6."id" as x3, rownum as x7
    ┇   from "cat_z" x6
    ┇ ) x2
    ┇ inner join (
    ┇   select x8."categoryId" as x5, rownum as x9
    ┇   from "post_z" x8
    ┇ ) x4
    ┇ on x2.x7 = x4.x9
     */
    val q3 = for {
      (c, p) <- categories zip posts
    } yield (c.id, p.categoryId)
    q3.exec shouldEqual List((1, -1), (3, 1), (2, 2), (4, 3))

    /*
    ┇ select x2.x3, x4.x5
    ┇ from (
    ┇   select x6."id" as x3, rownum as x7
    ┇   from "cat_z" x6
    ┇ ) x2
    ┇ inner join (
    ┇   select x8."categoryId" as x5, rownum as x9
    ┇   from "post_z" x8
    ┇ ) x4
    ┇ on x2.x7 = x4.x9
     */
    val q4 = for {
      res <- categories.zipWith(posts, (c: Categories, p: Posts) => (c.id, p.categoryId))
    } yield res
    q4.exec shouldEqual List((1, -1), (3, 1), (2, 2), (4, 3))

    /*
    ┇ select x2."id", rownum - 1
    ┇ from "cat_z" x2
     */
    val q5 = for {
      (c, i) <- categories.zipWithIndex
    } yield (c.id, i)
    q5.exec shouldEqual List((1, 0), (3, 1), (2, 2), (4, 3))

    /*
    ┇ select x2.x3, x4.x5, rownum - 1
    ┇ from (
    ┇   select x6."id" as x3, x6."name" as x7, rownum as x8
    ┇   from "cat_z" x6
    ┇ ) x2
    ┇ inner join (
    ┇   select x9."id" as x10, x9."title" as x11, x9."categoryId" as x5, rownum as x12
    ┇   from "post_z" x9
    ┇ ) x4
    ┇ on x2.x8 = x4.x12
     */
    val q6 = for {
      ((c, p), i) <- (categories zip posts).zipWithIndex
    } yield (c.id, p.categoryId, i)
    q6.exec shouldEqual List((1, -1, 0), (3, 1, 1), (2, 2, 2), (4, 3, 3))

    schema.drop.exec
  }

  test("no join condition") {
    class T(tag: Tag) extends Table[Int](tag, "nojoincondition_t") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val ts = TableQuery[T]

    db.exec {
      ts.schema.drop.asTry >>
      ts.schema.create
    }

    // left outer join
    /*
    ┇ select x2.x3, (case when (x4.x5 = 1) then x4.x6 else null end)
    ┇ from (
    ┇   select x7."id" as x3
    ┇   from "nojoincondition_t" x7
    ┇ ) x2
    ┇ left outer join (
    ┇   select 1 as x5, x8."id" as x6
    ┇   from "nojoincondition_t" x8
    ┇ ) x4
     */
    val q1 = ts joinLeft ts

    // right outer join
    /*
    ┇ select (case when (x2.x3 = 1) then x2.x4 else null end), x5.x6
    ┇ from (
    ┇   select 1 as x3, x7."id" as x4
    ┇   from "nojoincondition_t" x7
    ┇ ) x2
    ┇ right outer join (
    ┇   select x8."id" as x6
    ┇   from "nojoincondition_t" x8
    ┇ ) x5
     */
    val q2 = ts joinRight ts

    // inner join
    /*
    ┇ select x2.x3, x4.x5
    ┇ from (
    ┇   select x6."id" as x3
    ┇   from "nojoincondition_t" x6
    ┇ ) x2
    ┇ inner join (
    ┇   select x7."id" as x5
    ┇   from "nojoincondition_t" x7
    ┇ ) x4
     */
    val q3 = ts join ts

    db.exec {
      q1.result >> q2.result >> q3.result
    }

    ts.schema.drop.exec
  }
}
