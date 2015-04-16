package debop4s.core.utils

import debop4s.core.{AbstractCoreFunSuite, CancellableSink}

/**
 * CancellableFunSuite
 * @author Sunghyouk Bae
 */
class CancellableFunSuite extends AbstractCoreFunSuite {

  test("cancel once") {
    var count = 0
    val s = new CancellableSink {count += 1 }
    s.cancel()
    count shouldEqual 1
    s.cancel()
    count shouldEqual 1
  }

}
