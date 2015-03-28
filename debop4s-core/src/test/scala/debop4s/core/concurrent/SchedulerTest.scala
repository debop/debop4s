package debop4s.core.concurrent

import debop4s.core.AbstractCoreTest
import debop4s.core.utils.Threads
import org.scalatest.concurrent.Eventually
import scala.concurrent.Promise

class LocalSchedulerTest extends AbstractCoreTest {
  private val scheduler = new LocalScheduler

  def submit(f: => Unit) = scheduler.submit(Threads.makeRunnable { f })

  val N = 100

  test("run the first submitter immediately") {
    var ok = false
    submit {
      ok = true
    }
    assert(ok)
  }
  test("run subsequence submits serially") {
    var n = 0
    submit {
      n shouldEqual 0
      submit {
        n shouldEqual 1
        submit {
          n shouldEqual 2
          n += 1
        }
        n += 1
      }
      n += 1
    }
    n shouldEqual 3
  }

  test("handle many submits") {
    var ran = Nil: List[Int]
    submit {
      for (which <- 0 until N)
        submit {
          ran match {
            case Nil if which == 0 => //ok
            case hd :: _ => hd shouldEqual which - 1
            case _ => fail("ran wrong")
          }
          ran ::= which
        }
    }
    ran shouldEqual ( 0 until N ).reverse
  }
}

class ThreadPoolSchedulerTest extends AbstractCoreTest with Eventually {

  test("works") {
    val p = Promise[Unit]()
    val scheduler = new ThreadPoolScheduler("test")
    scheduler.submit(Threads.makeRunnable { p success() })

    eventually { p.isCompleted }

    scheduler.shutdown()
  }

}