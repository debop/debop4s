package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

/**
 * ConvertsTest
 * @author Sunghyouk Bae
 */
class NumberConvertsFunSuite extends AbstractCoreFunSuite {

  test("toInt 변환") {
    assert(NumberConverts.toInt(1) == 1)
    assert(NumberConverts.toInt("1") == 1)
    assert(NumberConverts.toInt("12") == 12)
    assert(NumberConverts.toInt("") == 0)
    assert(NumberConverts.toInt("abc") == 0)
    assert(NumberConverts.toInt(null) == 0)
  }

  test("toLong 변환") {
    assert(NumberConverts.toLong(1) == 1)
    assert(NumberConverts.toLong("1") == 1)
    assert(NumberConverts.toLong("12") == 12)
    assert(NumberConverts.toLong("") == 0)
    assert(NumberConverts.toLong("abc") == 0)
    assert(NumberConverts.toLong(null) == 0)
  }

  test("toFloat 변환") {
    assert(NumberConverts.toFloat(1) == 1)
    assert(NumberConverts.toFloat("1") == 1)
    assert(NumberConverts.toFloat("12") == 12)
    assert(NumberConverts.toFloat("") == 0)
    assert(NumberConverts.toFloat("abc") == 0)
    assert(NumberConverts.toFloat(null) == 0)
  }

  test("toDouble 변환") {
    assert(NumberConverts.toDouble(1) == 1)
    assert(NumberConverts.toDouble("1") == 1)
    assert(NumberConverts.toDouble("12") == 12)
    assert(NumberConverts.toDouble("") == 0)
    assert(NumberConverts.toDouble("abc") == 0)
    assert(NumberConverts.toDouble(null) == 0)
  }
}
