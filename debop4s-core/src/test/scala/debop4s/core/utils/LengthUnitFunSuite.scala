package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.units._

/**
 * DistanceUnitFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class LengthUnitFunSuite extends AbstractCoreFunSuite {

  test("길이 단위 변환 테스트") {
    1.millimeter.inMilliMeters shouldEqual 1
    1.centimeter.inCentiMeters shouldEqual 1
    1.meter.inMeters shouldEqual 1
    1.kilometer.inMeters shouldEqual 1000
  }

  test("길이를 사람이 읽기 쉽도록") {
    900.millimeter.toHuman shouldEqual "9.0 cm"
    10.5.kilometer.toHuman shouldEqual "10.5 km"
    Int.MaxValue.meter.toHuman shouldEqual "2147483.6 km"
  }

  test("길이 표시 문자열 파싱") {
    LengthUnit.parse("142.millimeter") shouldEqual 142.millimeter
    LengthUnit.parse("123456.7.centimeter") shouldEqual 123456.7.centimeter
    LengthUnit.parse("0.1.meter") shouldEqual 0.1.meter
    LengthUnit.parse("10000.1.meter") shouldEqual 10000.1.meter
    LengthUnit.parse("78.4.kilometer") shouldEqual 78.4.kilometer

    intercept[NumberFormatException] { StorageUnit.parse("100.bottles") }
    intercept[NumberFormatException] { StorageUnit.parse("100 meter") }
    intercept[NumberFormatException] { StorageUnit.parse("100.0.0.0.meter") }
  }

  test("음수") {
    (-132).meter.inMeters shouldEqual -132
    (-2).kilometer.toHuman shouldEqual "-2.0 km"
  }

  test("same hashCode") {
    val i = 4.kilometer
    val j = 4.kilometer
    val k = 4.0.kilometer
    i.hashCode shouldEqual j.hashCode
    i.hashCode shouldEqual k.hashCode
  }

  test("compare") {
    4.1.kilometer should be > 3.9.kilometer
    (-1.2).kilometer should be > (-5.1).kilometer
  }

  test("길이 사칙연산") {
    (1.0.kilometer + 2.0.kilometer) shouldEqual 3.0.kilometer
    (1.0.kilometer - 2.0.kilometer) shouldEqual (-1.0).kilometer
    (4.0.kilometer * 2.0) shouldEqual 8.0.kilometer
    (4.0.kilometer / 2.0) shouldEqual 2.0.kilometer
  }

  test("단위 변환") {
    1.0.kilometer.inMile shouldEqual (1000 / 1609.3)
    1.0.kilometer.inKiloMeters.mile.inMeters shouldEqual 1609.3
  }
}
