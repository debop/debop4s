package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, SlickContext}

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

    commit {
      DBIO.seq(
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
    }

    // Implicit join
    val q1 = (for {
      c <- categories
      p <- posts if p.categoryId === c.id
    } yield (p.id, c.id, c.name, p.title)).sortBy(_._1)

    readonly { q1.map(r => (r._1, r._2)).result } shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

    // Explicit inner join
    val q2 = (for {
      (c, p) <- categories join posts on (_.id === _.categoryId)
    } yield (p.id, c.id, c.name, p.title)).sortBy(_._1)

    readonly { q2.map(r => (r._1, r._2)).result } shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2))

    // NOTE: Left outer join (null first)
    val q3 = (for {
      (c, p) <- categories joinLeft posts on (_.id === _.categoryId)
    } yield (p.map(_.id), (p.map(_.id).getOrElse(0), c.id, c.name, p.map(_.title).getOrElse("")))).sortBy(_._1.nullsFirst).map(_._2)

    readonly { q3.map(r => (r._1, r._2)).result } shouldEqual Seq((0, 4), (2, 1), (3, 2), (4, 3), (5, 2))

    // NOTE: Read NULL from non-nullable column
    val q3a = (for {
      (c, p) <- categories joinLeft posts on (_.id === _.categoryId)
    } yield (p.map(_.id), c.id, c.name, p.map(_.title))).sortBy(_._1.nullsFirst)

    readonly { q3a.map(r => (r._1, r._2)).result } shouldEqual List((None, 4), (Some(2), 1), (Some(3), 2), (Some(4), 3), (Some(5), 2))

    // NOTE: Left outer join (null last)
    val q3b = (for {
      (c, p) <- categories joinLeft posts on (_.id === _.categoryId)
    } yield (p.map(_.id), (p.map(_.id).getOrElse(0), c.id, c.name, p.map(_.title).getOrElse("")))).sortBy(_._1.nullsLast).map(_._2)

    readonly { q3b.map(r => (r._1, r._2)).result } shouldEqual Seq((2, 1), (3, 2), (4, 3), (5, 2), (0, 4))


    // NOTE: Right outer join
    val q4 = (for {
      (c, p) <- categories joinRight posts on (_.id === _.categoryId)
    } yield (p.id, c.map(_.id).getOrElse(0), c.map(_.name).getOrElse(""), p.title)).sortBy(_._1)

    readonly { q4.map(r => (r._1, r._2)).result } shouldEqual List((1, 0), (2, 1), (3, 2), (4, 3), (5, 2))


    // NOTE: Full outer join
    val q5 = (for {
      (c, p) <- categories joinFull posts on (_.id === _.categoryId)
    } yield (p.map(_.id).getOrElse(0), c.map(_.id).getOrElse(0), c.map(_.name).getOrElse(""), p.map(_.title).getOrElse(""))).sortBy(_._1)

    readonly { q5.map(r => (r._1, r._2)).result } shouldEqual List((0, 4), (1, 0), (2, 1), (3, 2), (4, 3), (5, 2))

    commit { schema.drop }
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

    commit {
      DBIO.seq(
        schema.drop.asTry,
        schema.create,
        xs ++= Seq((1, "a"), (2, "b"), (3, "b"), (4, "c"), (5, "c")),
        ys ++= Seq((1, "a"), (2, "b"), (3, "b"), (4, "d"), (5, "d"))
      )
    }

    // Left outer, lift primitive value
    val q1 = (xs.map(_.b) joinLeft ys.map(_.b) on (_ === _)).to[Set]
    readonly { q1.result } shouldEqual Set(("a", Some("a")), ("b", Some("b")), ("c", None))

    // Nested left outer, lift primitive value
    val q2 = ((xs.map(_.b) joinLeft ys.map(_.b) on (_ === _)) joinLeft ys.map(_.b) on (_._1 === _)).to[Set]
    readonly { q2.result } shouldEqual Set((("a", Some("a")), Some("a")), (("b", Some("b")), Some("b")), (("c", None), None))

    // Left outer, lift non-primitive value
    val q3 = (xs joinLeft ys on (_.b === _.b)).to[Set]
    readonly { q3.result } shouldBe Set(((1, "a"), Some((1, "a"))),
                                        ((2, "b"), Some((2, "b"))),
                                        ((2, "b"), Some((3, "b"))),
                                        ((3, "b"), Some((2, "b"))),
                                        ((3, "b"), Some((3, "b"))),
                                        ((4, "c"), None),
                                        ((5, "c"), None)
    )

    // Left outer, lift non-primitive value, then map to primitive
    val q4 = (xs joinLeft ys on (_.b === _.b)).map { case (x, yo) => (x.a, yo.map(_.a)) }.to[Set]
    readonly { q4.result } shouldBe Set((1, Some(1)),
                                        (2, Some(2)),
                                        (2, Some(3)),
                                        (3, Some(2)),
                                        (3, Some(3)),
                                        (4, None),
                                        (5, None)
    )

    // Nested left outer, lift non-primitive value
    val q5 = ((xs joinLeft ys on (_.b === _.b)) joinLeft ys on (_._1.b === _.b)).to[Set]
    readonly { q5.result } shouldBe Set((((1, "a"), Some((1, "a"))), Some((1, "a"))),
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
    val q6 = (ys.map(_.b) joinRight xs.map(_.b) on (_ === _)).to[Set]
    readonly { q6.result } shouldBe Set((Some("a"), "a"), (Some("b"), "b"), (None, "c"))

    // Nested right outer, lift primitive value
    // (left-associative; non symmetrical to then nested left outer case)
    val q7 = ((ys.map(_.b) joinRight xs.map(_.b) on (_ === _)) joinRight xs.map(_.b) on (_._2 === _)).to[Set]
    readonly { q7.result } shouldBe Set((Some((Some("a"), "a")), "a"),
                                        (Some((Some("b"), "b")), "b"),
                                        (Some((None, "c")), "c")
    )

    // Right outer, lift non-primitive value
    val q8 = (ys joinRight xs on (_.b === _.b)).to[Set]
    readonly { q8.result } shouldBe Set((Some((1, "a")), (1, "a")),
                                        (Some((2, "b")), (2, "b")),
                                        (Some((2, "b")), (3, "b")),
                                        (Some((3, "b")), (2, "b")),
                                        (Some((3, "b")), (3, "b")),
                                        (None, (4, "c")),
                                        (None, (5, "c"))
    )

    // Right outer, lift non-primitive value, then map to primitive
    val q9 = (ys joinRight xs on (_.b === _.b)).map { case (yo, x) => (yo.map(_.a), x.a) }.to[Set]
    readonly { q9.result } shouldBe Set((Some(1), 1),
                                        (Some(2), 2),
                                        (Some(2), 3),
                                        (Some(3), 2),
                                        (Some(3), 3),
                                        (None, 4),
                                        (None, 5)
    )

    // Nested right outer, lift non-primitives value
    // (left-associative, not symmetrical to the nsted left outer case)
    val q10 = ((ys joinRight xs on (_.b === _.b)) joinRight xs on (_._1.map(_.b) === _.b)).to[Set]
    readonly { q10.result } shouldBe Set((Some((Some((1, "a")), (1, "a"))), (1, "a")),
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
    val q11 = (xs.map(_.b) joinFull ys.map(_.b) on (_ === _)).to[Set]
    readonly { q11.result } shouldBe Set((Some("a"), Some("a")),
                                         (Some("b"), Some("b")),
                                         (Some("c"), None),
                                         (None, Some("d"))
    )

    // Full outer, lift non-primitive values
    if (!SlickContext.isMySQL) {
      val q12 = (xs joinFull ys on (_.b === _.b)).to[Set]
      readonly { q12.result } shouldBe Set((Some((1, "a")), Some((1, "a"))),
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

    commit { schema.drop }
  }

  test("computed star projection") {
    class X(tag: Tag) extends Table[(Int, Int)](tag, "star_proj_x") {
      def a = column[Int]("a")
      def b = column[Int]("b", O.Default(2))
      def * = (a, b * 10)
    }
    val xs = TableQuery[X]
    commit {
      xs.schema.drop.asTry >>
      xs.schema.create >>
      (xs.map(_.a) += 1)
    }
    val q1 = xs joinLeft xs
    readonly { q1.result } shouldBe Vector(((1, 20), Some((1, 20))))

    commit { xs.schema.drop }
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

    commit {
      schema.drop.asTry >>
      schema.create >>
      (categories ++= Seq((1, "Scala"), (3, "Windows"), (2, "ScalaQuery"), (4, "Software"))) >>
      (posts.map(p => (p.title, p.categoryId)) ++= Seq(("Test Post", -1),
                                                       ("Formal Language Processing in Scala, Part 5", 1),
                                                       ("Efficient Parameterized Queries in ScalaQuery", 2),
                                                       ("Removing Libraries and HomeGroup icons from the Windows 7 desktop", 3),
                                                       ("A ScalaQuery Update", 2)
      ))
    }

    if (!SlickContext.isMySQL) {
      val q1 = for {
        (c, i) <- categories.sortBy(_.id).zipWithIndex
      } yield (c.id, i)
      readonly { q1.result } shouldEqual Seq((1, 0), (2, 1), (3, 2), (4, 3))
    }

    if (!SlickContext.isMySQL) {
      val q2 = for {
        (c, p) <- categories.sortBy(_.id) zip posts.sortBy(_.categoryId)
      } yield (c.id, p.categoryId)
      readonly { q2.result } shouldEqual Seq((1, -1), (2, 1), (3, 2), (4, 2))
    }

    val q3 = for {
      (c, p) <- categories zip posts
    } yield (c.id, p.categoryId)
    readonly { q3.result } shouldEqual Seq((1, -1), (3, 1), (2, 2), (4, 3))

    val q4 = for {
      res <- categories.zipWith(posts, (c: Categories, p: Posts) => (c.id, p.categoryId))
    } yield res
    readonly { q4.result } shouldEqual Seq((1, -1), (3, 1), (2, 2), (4, 3))

    val q5 = for {
      (c, i) <- categories.zipWithIndex
    } yield (c.id, i)
    readonly { q5.result } shouldEqual Seq((1, 0), (3, 1), (2, 2), (4, 3))

    val q6 = for {
      ((c, p), i) <- (categories zip posts).zipWithIndex
    } yield (c.id, p.categoryId, i)
    readonly { q6.result } shouldEqual Seq((1, -1, 0), (3, 1, 1), (2, 2, 2), (4, 3, 3))

    commit { schema.drop }
  }

  test("no join condition") {
    class T(tag: Tag) extends Table[Int](tag, "nojoincondition_t") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val ts = TableQuery[T]

    commit {
      ts.schema.drop.asTry >>
      ts.schema.create
    }

    // left outer join
    val q1 = ts joinLeft ts

    // right outer join
    val q2 = ts joinRight ts

    // inner join
    val q3 = ts join ts

    db.run(DBIO.seq(q1.result, q2.result, q3.result)).stay

    commit { ts.schema.drop }
  }
}
