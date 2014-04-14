package debop4s.core

import debop4s.core.TimeConversions._
import org.scalatest.concurrent.Eventually._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * ClosableTest
 * Created by debop on 2014. 4. 13.
 */
class ClosableTest extends AbstractCoreTest {

    test("Closable.close(Duration)") {
        Time.withCurrentTimeFrozen { _ =>
            var time: Option[Time] = None
            val c = Closable.make { t =>
                time = Some(t)
                Future {}
            }
            val dur = 1.minutes
            c.close(dur)
            assert(time === Some(Time.now + dur))
        }
    }

    test("Closable.closeOnCollect") {
        @volatile var closed = false
        Closable.closeOnCollect(
            Closable.make { t =>
                closed = true
                Future()
            },
            new Object {}
        )
        System.gc()
        eventually { assert(closed) }
    }

}
