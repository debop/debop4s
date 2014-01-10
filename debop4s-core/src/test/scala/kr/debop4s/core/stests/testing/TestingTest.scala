package kr.debop4s.core.stests.testing

import kr.debop4s.core.testing.Testing
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.tests.testing.TestingTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 3:33
 */
class TestingTest extends AssertionsForJUnit {

    implicit lazy val log = LoggerFactory.getLogger(getClass)

    val range = Range(0, 9)

    @Test
    def run() {
        Testing.run(100) {
            range.foreach(x => Hero.findRoot(x))
        }
    }

    @Test
    def runAction() {
        Testing.runAction(100)(x => {
            range.foreach(_ => Hero.findRoot(x))
        })
    }

    @Test
    def runFunc() {
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
                guess = (n / guess + guess) / 2.0
                error = math.abs(guess * guess - n)
            } while (error > Tolerance)
            guess
        }
    }

}
