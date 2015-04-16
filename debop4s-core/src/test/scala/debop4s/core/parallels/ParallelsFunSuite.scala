package debop4s.core.parallels

import debop4s.core.AbstractCoreFunSuite

/**
 * debop4s.core.stests.async.ParallelsFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오후 2:06
 */
class ParallelsFunSuite extends AbstractCoreFunSuite {

  private val LowerBound = 0
  private val UpperBound = 1000

  val runnable: Unit = {
    ( LowerBound until UpperBound ).foreach {
      x => Hero.findRoot(x)
    }
    log.trace(s"FindRoot($UpperBound) = ${ Hero.findRoot(UpperBound) }")
  }

  val runnable1 = (x: Int) => {
    ( LowerBound until UpperBound ).foreach {
      x => Hero.findRoot(x)
    }
    log.trace(s"FindRoot($UpperBound) = ${ Hero.findRoot(UpperBound) }")
  }

  test("parallel runAction") {
    Parallels.runAction(100)(runnable)
  }

  test("parallel runAction1") {
    Parallels.runAction1(100)(x => {
      runnable1(x)
    })
  }

  test("parallel runnable") {
    val runnableAction = new Runnable {
      override def run(): Unit = {
        runnable
      }
    }
    Parallels.run(100)(runnableAction)
  }
}

object Hero {

  private val Tolerance = 1.0e-1

  @inline
  def findRoot(number: Double): Double = {
    var guess = 1.0
    var error = 1.0

    while (error > Tolerance) {
      guess = ( number / guess + guess ) / 2.0
      error = math.abs(guess * guess - number)
    }
    guess
  }

}
