package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * NestingFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class NestingFunSuite extends AbstractSlickFunSuite {

  test("nested tupled") {
    class T(tag: Tag) extends Table[(Int, String, String)](tag, "nested_tupled") {
      def a = column[Int]("A")
      def b = column[String]("B")
      def c = column[String]("C")
      def * = (a, b, c)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq((1, "1", "a"), (2, "2", "b"), (3, "3", "c"))

      val res1 = Seq(
                      (1, "1", "a", 5), (2, "2", "a", 5), (3, "3", "a", 5),
                      (1, "1", "b", 5), (2, "2", "b", 5), (3, "3", "b", 5),
                      (1, "1", "c", 5), (2, "2", "c", 5), (3, "3", "c", 5)
                    )
      val res1b = res1.map { case (a, b, c, d) => ((a, b), (c, d)) }

      // H2:
      // select x2."A", x2."B", x3."C", 5
      //   from "nested_tupled" x2, "nested_tupled" x3
      //  order by x3."C", x2."A"
      val q1a = ( for {
        (a, b) <- ts.map(t => (t.a, t.b))
        c <- ts.map(t => t.c)
      } yield (a, b, c, 5) )
                .sortBy(t => (t._3, t._1))

      q1a.run shouldEqual res1

      // H2:
      // select x2."A", x2."B", x3."C", 5
      //   from "nested_tupled" x2, "nested_tupled" x3
      //  order by x3."C", x2."A"
      val q1c = ( for {
        (a, b) <- ts.map(t => (t.a, t.b))
        c <- ts.map(t => t.c)
      } yield (a, b, c, LiteralColumn(5)) )
                .sortBy(t => (t._3, t._1))

      q1c.run shouldEqual res1

      val q1d = ( for {
        (a, b) <- ts.map(t => (t.a, t.b))
        c <- ts.map(t => t.c)
      } yield ((a, b), (c, 5)) )
                .sortBy(t => (t._2._1, t._1._1))


      val res2 = Set((1, "1", 8), (2, "2", 10))

      // H2
      // select x2.x3, x2.x4, x2.x5 * 2
      //   from (select x6."A" as x3, x6."B" as x4, 4 as x5
      //           from "nested_tupled" x6
      //           where x6."A" = ?
      //
      //         union all
      //
      //         select x7."A" as x3, x7."B" as x4, 5 as x5
      //           from "nested_tupled" x7
      //          where x7."A" = ?) x2
      val q2a = for {
        (a, b, c) <- ts.filter(_.a === 1.bind).map(t => (t.a, t.b, 4)) unionAll ts.filter(_.a === 2.bind).map(t => (t.a, t.b, 5))
      } yield (a, b, c * 2)
      q2a.run.toSet shouldEqual res2

      val q2b = for {
        (a, b, c) <-
        ts.filter(_.a === 1.bind).map(t => (t.a, t.b, LiteralColumn(4))) unionAll
        ts.filter(_.a === 2).map(t => (t.a, t.b, LiteralColumn(5)))
      } yield (a, b, c * 2)

      q2b.run.toSet shouldEqual res2


      // H2:
      // select x2.x3, x2.x4, x2.x5 * 2
      //   from (select x6."A" as x3, x6."B" as x4, 4 as x5 from "nested_tupled" x6 where x6."A" = 1
      //         union all
      //         select x7."A" as x3, x7."B" as x4, 5 as x5 from "nested_tupled" x7 where x7."A" = 2) x2
      val q2c = for {
        (a, b, c) <- ts.filter(_.a === 1).map(t => (t.a, t.b, 4)) unionAll ts.filter(_.a === 2).map(t => (t.a, t.b, 5))
      } yield (a, b, c * 2)

      q2c.run.toSet shouldEqual res2
    }
  }

}
