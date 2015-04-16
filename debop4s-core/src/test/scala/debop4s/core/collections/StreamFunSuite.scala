package debop4s.core.collections

import debop4s.core.AbstractCoreFunSuite

/**
 * StreamFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class StreamFunSuite extends AbstractCoreFunSuite {

  test("stream lazyness") {

    Stream(1 to 10: _*) map { i =>
      debug(s"Stream -> map $i")
      i + 10
    } filter { i =>
      debug(s"Stream -> filter $i")
      i % 2 == 0
    }
  }
}
