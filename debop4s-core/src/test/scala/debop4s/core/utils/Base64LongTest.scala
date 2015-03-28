package debop4s.core.utils

import debop4s.core.AbstractCoreTest
import scala.util.Random

/**
 * Base64LongTest
 * @author Sunghyouk Bae
 */
class Base64LongTest extends AbstractCoreTest {

  test("properly convert zero") {
    Base64Long.toBase64(0) shouldEqual "A"
  }

  test("properly convert a large number") {
    Base64Long.toBase64(202128261025763330L) shouldEqual "LOGpUdghAC"
  }

  test("Use the expected number of digits") {
    val expectedLength: Long => Int = {
      case 0 => 1
      case n if n < 0 => 11
      case n => ( math.log(n + 1) / math.log(64) ).ceil.toInt
    }
    val checkExpectedLength = (n: Long) => Base64Long.toBase64(n).length shouldEqual expectedLength(n)
    Seq(0L, 1L, 63L, 64L, 4095L, 4096L, -1L) foreach checkExpectedLength
    ( 1 to 200 ) foreach { _ =>
      checkExpectedLength(Random.nextLong())
    }
  }

}
