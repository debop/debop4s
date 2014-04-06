package debop4s.core.stests.utils

import debop4s.core.stests.AbstractCoreTest
import debop4s.core.utils.{Strings, Codecs}

/**
 * CodecsTest
 * Created by debop on 2014. 2. 23.
 */
class CodecsTest extends AbstractCoreTest {

  val plainText = "동해물과 백두산이 마르고 닳도록 ^^ https://github.com/debop/debop4s"

  test("base64 bytes") {
    val base64Bytes = Codecs.encodeBase64(Strings.getUtf8Bytes(plainText))
    val convertedBytes = Codecs.decodeBase64(base64Bytes)

    val convertedText = Strings.getUtf8String(convertedBytes)
    convertedText should equal(plainText)
  }

  test("base64 string") {
    val base64String = Codecs.encodeBase64String(plainText)
    val converted = Codecs.decodeBase64String(base64String)

    converted should equal(plainText)
  }

}
