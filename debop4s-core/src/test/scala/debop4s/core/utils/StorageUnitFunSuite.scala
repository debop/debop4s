package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.storage._

class StorageUnitFunSuite extends AbstractCoreFunSuite {

  test("저장 크기 단위 변환 테스트") {
    1.byte.inBytes shouldBe 1
    1.kilobyte.inBytes shouldBe 1024
    1.megabyte.inMegaBytes shouldBe 1
    1.gigabyte.inMegaBytes shouldBe 1024
    1.gigabyte.inKiloBytes shouldBe 1024 * 1024
  }

  test("저장 크기를 읽기 편하게 표시하기") {
    900.bytes.toHuman shouldBe "900 B"
    1.kilobyte.toHuman shouldBe "1024 B"
    2.kilobytes.toHuman shouldBe "2.0 KiB"
    Int.MaxValue.bytes.toHuman shouldBe "2.0 GiB"
    Long.MaxValue.bytes.toHuman shouldBe "8.0 EiB"
  }

  test("문자열로 표시된 저장단위 파싱하기") {
    StorageUnit.parse("142.bytes") shouldBe 142.bytes
    StorageUnit.parse("78.kilobytes") shouldBe 78.kilobytes
    StorageUnit.parse("1.megabytes") shouldBe 1.megabytes
    StorageUnit.parse("878.gigabytes") shouldBe 878.gigabytes
    StorageUnit.parse("3.terabytes") shouldBe 3.terabytes
    StorageUnit.parse("-3.terabytes") shouldBe (-3).terabytes
  }

  test("잘못된 저장단위") {
    intercept[NumberFormatException] { StorageUnit.parse("100.bottles") }
    intercept[NumberFormatException] { StorageUnit.parse("100 bytes") }
  }

  test("음수 취급") {
    -123.bytes.inBytes shouldBe -123
    (-2).kilobytes.toHuman shouldBe "-2.0 KiB"
  }

  test("same hashCode") {
    val i = 4.megabytes
    val j = 4.megabytes
    i.hashCode() shouldBe j.hashCode()
  }
}
