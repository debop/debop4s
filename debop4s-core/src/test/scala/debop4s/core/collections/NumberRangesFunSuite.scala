package debop4s.core.collections

import debop4s.core.AbstractCoreFunSuite

/**
 * NumberRangesFunSuite
 * @author sunghyouk.bae@gmail.com 2014. 7. 26.
 */
class NumberRangesFunSuite extends AbstractCoreFunSuite {

  test("Int") {
    NumberRanges.Int(5).foreach { x => debug(x.toString) }
    NumberRanges.Int.group(0, 100, 5, 2).foreach { x => debug(x.toString()) }
  }

}
