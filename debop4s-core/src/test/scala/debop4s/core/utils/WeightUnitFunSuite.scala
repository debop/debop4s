package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.units._

/**
 * WeightUnitFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class WeightUnitFunSuite extends AbstractCoreFunSuite {

  test("무게 단위 변환 테스트") {
    1.milligram.inMilligram shouldEqual 1
    1000.milligram.inGram shouldEqual 1
    1.gram.inGram shouldEqual 1
    1.kilogram.inKilogram shouldEqual 1
    1.kilogram.inGram shouldEqual 1000
  }

  test("무게를 사람이 읽기 쉽도록") {
    900.milligram.toHuman shouldEqual "900.0 mg"
    10.5.kilogram.toHuman shouldEqual "10.5 kg"
    11.56.kilogram.toHuman shouldEqual "11.6 kg"
    Int.MaxValue.gram.toHuman shouldEqual "2147483.6 kg"
  }

  test("무게 표시 문자열 파싱") {
    WeightUnit.parse("142.milligram") shouldEqual 142.milligram
    WeightUnit.parse("123456.7.milligram") shouldEqual 123456.7.milligram
    WeightUnit.parse("123456.7.milligram") shouldEqual 123.4567.gram
    WeightUnit.parse("10000.1.gram") shouldEqual 10000.1.gram
    WeightUnit.parse("78.4.kilogram") shouldEqual 78.4.kilogram

    intercept[NumberFormatException] { WeightUnit.parse("100.bottles") }
    intercept[NumberFormatException] { WeightUnit.parse("100 gram") }
    intercept[NumberFormatException] { WeightUnit.parse("100.0.0.0.meter") }
  }

  test("음수") {
    (-132).gram.inGram shouldEqual -132
    (-2).kilogram.toHuman shouldEqual "-2.0 kg"
  }

  test("same hashCode") {
    val i = 4.kilogram
    val j = 4.kilogram
    val k = 4.0.kilogram
    i.hashCode shouldEqual j.hashCode
    i.hashCode shouldEqual k.hashCode
  }

  test("compare") {
    4.1.kilogram should be > 3.9.kilogram
    (-1.2).kilogram should be > (-5.1).kilogram
  }

  test("무게 사칙연산") {
    (1.0.kilogram + 2.0.kilogram) shouldEqual 3.0.kilogram
    (1.0.kilogram - 2.0.kilogram) shouldEqual (-1.0).kilogram
    (4.0.kilogram * 2.0) shouldEqual 8.0.kilogram
    (4.0.kilogram / 2.0) shouldEqual 2.0.kilogram
  }

}
