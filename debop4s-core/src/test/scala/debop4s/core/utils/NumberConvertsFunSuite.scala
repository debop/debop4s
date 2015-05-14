package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

/**
 * ConvertsTest
 * @author Sunghyouk Bae
 */
class NumberConvertsFunSuite extends AbstractCoreFunSuite {

  test("toInt 변환") {
    NumberConverts.toInt(1) shouldBe 1
    NumberConverts.toInt("1") shouldBe 1
    NumberConverts.toInt("12") shouldBe 12
    NumberConverts.toInt("") shouldBe 0
    NumberConverts.toInt("abc") shouldBe 0
    NumberConverts.toInt(null) shouldBe 0
  }

  test("toLong 변환") {
    NumberConverts.toLong(1) shouldBe 1
    NumberConverts.toLong("1") shouldBe 1
    NumberConverts.toLong("12") shouldBe 12
    NumberConverts.toLong("") shouldBe 0
    NumberConverts.toLong("abc") shouldBe 0
    NumberConverts.toLong(null) shouldBe 0
  }

  test("toFloat 변환") {
    NumberConverts.toFloat(1) shouldBe 1
    NumberConverts.toFloat("1") shouldBe 1
    NumberConverts.toFloat("12") shouldBe 12
    NumberConverts.toFloat("") shouldBe 0
    NumberConverts.toFloat("abc") shouldBe 0
    NumberConverts.toFloat(null) shouldBe 0
  }

  test("toDouble 변환") {
    NumberConverts.toDouble(1) shouldBe 1
    NumberConverts.toDouble("1") shouldBe 1
    NumberConverts.toDouble("12") shouldBe 12
    NumberConverts.toDouble("") shouldBe 0
    NumberConverts.toDouble("abc") shouldBe 0
    NumberConverts.toDouble(null) shouldBe 0
  }
}
