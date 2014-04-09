package debop4s.core.utils

import debop4s.core.AbstractCoreTest
import debop4s.core.conversions.storage._

/**
 * StorageTest
 * @author Sunghyouk Bae
 */
class StorageUnitTest extends AbstractCoreTest {

    test("저장 크기 단위 변환 테스트") {
        assert(1.byte.inBytes === 1)
        assert(1.kilobyte.inBytes === 1024)
        assert(1.megabyte.inMegaBytes === 1)
        assert(1.gigabyte.inMegaBytes === 1024)
        assert(1.gigabyte.inKiloBytes === 1024 * 1024)
    }

    test("저장 크기를 읽기 편하게 표시하기") {
        assert(900.bytes.toHuman === "900 B")
        assert(1.kilobyte.toHuman === "1024 B")
        assert(2.kilobytes.toHuman === "2.0 KiB")
        assert(Int.MaxValue.bytes.toHuman === "2.0 GiB")
        assert(Long.MaxValue.bytes.toHuman === "8.0 EiB")
    }

    test("문자열로 표시된 저장단위 파싱하기") {
        assert(StorageUnit.parse("142.bytes") === 142.bytes)
        assert(StorageUnit.parse("78.kilobytes") === 78.kilobytes)
        assert(StorageUnit.parse("1.megabytes") === 1.megabytes)
        assert(StorageUnit.parse("878.gigabytes") === 878.gigabytes)
        assert(StorageUnit.parse("3.terabytes") === 3.terabytes)
        assert(StorageUnit.parse("-3.terabytes") === -3.terabytes)
    }

    test("잘못된 저장단위") {
        intercept[NumberFormatException] { StorageUnit.parse("100.bottles") }
        intercept[NumberFormatException] { StorageUnit.parse("100 bytes") }
    }

    test("음수 취급") {
        assert(-123.bytes.inBytes === -123)
        assert(-2.kilobytes.toHuman === "-2.0 KiB")
    }

    test("same hashCode") {
        val i = 4.megabytes
        val j = 4.megabytes
        assert(i.hashCode() == j.hashCode())
    }

}
