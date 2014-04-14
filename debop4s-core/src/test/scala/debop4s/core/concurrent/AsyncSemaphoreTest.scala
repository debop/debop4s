package debop4s.core.concurrent

import debop4s.core.AbstractCoreTest
import debop4s.core.parallels.Asyncs
import java.util.concurrent.ConcurrentLinkedQueue
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * AsyncSemaphoreTest
 * @author Sunghyouk Bae
 */
@RunWith(classOf[JUnitRunner])
class AsyncSemaphoreTest extends AbstractCoreTest {

    class AsyncSemaphoreHelper(val sem: AsyncSemaphore,
                               var count: Int,
                               val permits: ConcurrentLinkedQueue[Permit]) {
        def copy(sem: AsyncSemaphore = this.sem,
                 count: Int = this.count,
                 permits: ConcurrentLinkedQueue[Permit] = this.permits) =
            new AsyncSemaphoreHelper(sem, count, permits)
    }

    var sem: AsyncSemaphore = _
    var helper: AsyncSemaphoreHelper = _

    before {
        sem = new AsyncSemaphore(2)
        helper = new AsyncSemaphoreHelper(sem, 0, new ConcurrentLinkedQueue[Permit])
    }

    private def aquire(helper: AsyncSemaphoreHelper) = {
        val fPermit = helper.sem.aquire()
        fPermit onSuccess { case permit =>
            helper.count += 1
            helper.permits add permit
        }
        fPermit
    }

    test("should execute immediately while permits are available") {
        assert(helper.sem.numPermitsAvailable === 2)
        Asyncs.ready(aquire(helper))
        assert(helper.count === 1)
        assert(helper.sem.numPermitsAvailable === 1)

        Asyncs.ready(aquire(helper))
        assert(helper.count === 2)
        assert(helper.sem.numPermitsAvailable === 0)

        Asyncs.ready(aquire(helper))
        assert(helper.count === 2)
        assert(helper.sem.numPermitsAvailable === 0)
    }

}
