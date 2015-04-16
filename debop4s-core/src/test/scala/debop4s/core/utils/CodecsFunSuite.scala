package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.compress.{SnappyStringEncoder, DeflateStringEncoder, GZipStringEncoder}

/**
 * CodecsFunSuite
 * Created by debop on 2014. 2. 23.
 */
class CodecsFunSuite extends AbstractCoreFunSuite {

  val plainText = "동해물과 백두산이 마르고 닳도록 ^^ https://github.com/debop/debop4s hello world. scala is good language."

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

class TestBase64Encoder extends Base64StringEncoder {}

class StringEncoderFunSuite extends AbstractCoreFunSuite {

  val longString = "동해물과 백두산이 마르고 닳도록 ^^ https://github.com/debop/debop4s hello world. scala is good language."
  val stringEncoder: StringEncoder = new TestBase64Encoder()

  test("string encode/decode") {
    val encodedStr = stringEncoder.encode(longString.getBytes(Charsets.UTF_8))
    val decodedStr = new String(stringEncoder.decode(encodedStr), Charsets.UTF_8)
    assert(decodedStr == longString)
  }
}

class GZipStringEncoderFunSuite extends StringEncoderFunSuite {
  override val stringEncoder = new GZipStringEncoder {}
}

class DeflateStringEncoderFunSuite extends StringEncoderFunSuite {
  override val stringEncoder = new DeflateStringEncoder {}
}

class SnappyStringEncoderFunSuite extends StringEncoderFunSuite {
  override val stringEncoder = new SnappyStringEncoder {}
}
