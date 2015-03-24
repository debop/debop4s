package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.slick.util.iter._
import scala.util.Try

/**
 * IterateeFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class IterateeFunSuite extends AbstractSlickFunSuite {

  class A(tag: Tag) extends Table[(String, Int)](tag, "iteratee_a") {
    def s = column[String]("s", O.PrimaryKey)
    def i = column[Int]("i")
    def * = (s, i)
  }
  lazy val as = TableQuery[A]

  test("iteratee test") {
    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create
      as.insertAll(("a", 1), ("b", 2), ("c", 3), ("d", 4))

      val q1 = as.sortBy(_.s)

      // Sum i values until > 5 with foldLeft()
      // There is no way to stop early when the limit has reached
      var seen1 = ""
      val r1 = q1.foldLeft(0) {
        case (z, (s, i)) =>
          seen1 += s
          if (z > 5) z else z + i
      }
      r1 shouldEqual (1 + 2 + 3)
      seen1 shouldEqual "abcd"

      // Do the same with enumerate() and terminate when down
      var seen2 = ""
      def step(z: Int): Input[(String, Int)] => IterV[(String, Int), Int] = {
        case El((s, i)) =>
          seen2 += s
          if (z + i > 5) Done(z + i, Empty) else Cont(step(z + i))
        case Empty =>
          seen2 += "_"
          Cont(step(z))
        case EOF =>
          seen2 += "."
          Done(z, EOF)
      }

      val r2 = q1.enumerate(Cont(step(0))).run
      r2 shouldEqual 6
      seen2 shouldEqual "abc"


      def step2(z: Int): Cont.K[(String, Int), Int] =
        _.fold(
        { case (s, i) =>
          if (z + i > 5) Done(z + i) else Cont(step2(z + i))
        },
        Cont(step2(z)),
        Done(z, EOF)
              )

      val r3 = q1.enumerate(Cont(step2(0))).run
      r3 shouldEqual 6
    }
  }
}
