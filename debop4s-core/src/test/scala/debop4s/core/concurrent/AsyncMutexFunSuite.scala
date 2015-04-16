package debop4s.core.concurrent

import debop4s.core.AbstractCoreFunSuite

/**
 * AsyncMutexFunSuite
 * @author Sunghyouk Bae
 */
class AsyncMutexFunSuite extends AbstractCoreFunSuite {

  test("admit only one operation at a time") {
    val m = new AsyncMutex()

    val a0 = m.acquire()
    val a1 = m.acquire()

    assert(a0.value.isDefined)
    assert(!a1.value.isDefined)

    Asyncs.result(a0).release()
    assert(a1.value.isDefined)

    val a2 = m.acquire()
    assert(!a2.value.isDefined)
    Asyncs.result(a1).release()
    assert(a2.value.isDefined)
  }

}
