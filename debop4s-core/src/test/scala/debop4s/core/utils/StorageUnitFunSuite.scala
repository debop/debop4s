package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.storage._

class StorageUnitFunSuite extends AbstractCoreFunSuite {

  test("저장 크기 단위 변환 테스트") {
    1.byte.inBytes shouldEqual 1
    1.kilobyte.inBytes shouldEqual 1024
    1.megabyte.inMegaBytes shouldEqual 1
    1.gigabyte.inMegaBytes shouldEqual 1024
    1.gigabyte.inKiloBytes shouldEqual 1024 * 1024
  }

  test("저장 크기를 읽기 편하게 표시하기") {
    900.bytes.toHuman shouldEqual "900 B"
    1.kilobyte.toHuman shouldEqual "1024 B"
    2.kilobytes.toHuman shouldEqual "2.0 KiB"
    Int.MaxValue.bytes.toHuman shouldEqual "2.0 GiB"
    Long.MaxValue.bytes.toHuman shouldEqual "8.0 EiB"
  }

  test("문자열로 표시된 저장단위 파싱하기") {
    StorageUnit.parse("142.bytes") shouldEqual 142.bytes
    StorageUnit.parse("78.kilobytes") shouldEqual 78.kilobytes
    StorageUnit.parse("1.megabytes") shouldEqual 1.megabytes
    StorageUnit.parse("878.gigabytes") shouldEqual 878.gigabytes
    StorageUnit.parse("3.terabytes") shouldEqual 3.terabytes
    StorageUnit.parse("-3.terabytes") shouldEqual (-3).terabytes
  }

  test("잘못된 저장단위") {
    intercept[NumberFormatException] { StorageUnit.parse("100.bottles") }
    intercept[NumberFormatException] { StorageUnit.parse("100 bytes") }
  }

  test("음수 취급") {
    -123.bytes.inBytes shouldEqual -123
    (-2).kilobytes.toHuman shouldEqual "-2.0 KiB"
  }

  test("same hashCode") {
    val i = 4.megabytes
    val j = 4.megabytes
    i.hashCode() shouldEqual j.hashCode()
  }
}
