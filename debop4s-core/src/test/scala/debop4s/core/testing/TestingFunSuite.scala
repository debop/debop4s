package debop4s.core.testing

import debop4s.core.AbstractCoreFunSuite
import org.scalatest.{ BeforeAndAfter, Matchers, FunSuite }
import org.slf4j.LoggerFactory

/**
 * debop4s.core.tests.testing.TestingFunSuite
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 3:33
 */
class TestingFunSuite extends AbstractCoreFunSuite {

  val range = Range(0, 9)

  test("run") {
    Testing.run(100) {
      range.foreach(x => Hero.findRoot(x))
    }
  }

  test("runAction") {
    Testing.runAction(100)(x => {
      range.foreach(_ => Hero.findRoot(x))
    })
  }

  test("runFunc") {
    Testing.runFunc(100)(x => {
      range.map(_ => Hero.findRoot(x))
    })
  }


  object Hero {
    val Tolerance = 1.0e-4

    def findRoot(n: Double): Double = {
      var guess = 1.0
      var error = 100.0
      do {
        guess = ( n / guess + guess ) / 2.0
        error = math.abs(guess * guess - n)
      } while (error > Tolerance)
      guess
    }
  }

}
