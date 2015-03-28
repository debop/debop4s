package debop4s.core

/**
 * CancellableTest
 * @author Sunghyouk Bae
 */
class CancellableTest extends AbstractCoreTest {

  test("cancel once") {
    var count = 0
    val s = new CancellableSink {count += 1 }
    s.cancel()
    count shouldEqual 1
    s.cancel()
    count shouldEqual 1
  }

}
