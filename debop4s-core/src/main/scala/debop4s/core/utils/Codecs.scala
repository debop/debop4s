package debop4s.core.utils

import java.nio.charset.Charset

import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory

/**
 * Base64 방식의 인코딩/디코딩을 수행하는 Codec object
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 */
object Codecs {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  def encodeBase64(input: Array[Byte]): Array[Byte] = {
    encodeBase64(input, isChucked = false, urlSafe = true)
  }

  /**
   * 입력 데이터를 base64 방식으로 인코딩합니다.
   */
  def encodeBase64(input: Array[Byte],
                   isChucked: Boolean = false,
                   urlSafe: Boolean = true): Array[Byte] = {
    Base64.encodeBase64(input, isChucked, urlSafe)
  }

  def encodeBase64String(str: String): String =
    encodeBase64String(str, Charsets.UTF_8, isChunked = false, urlSafe = true)

  /**
   * 입력 데이터를 base64 방식으로 인코딩합니다.
   */
  def encodeBase64String(str: String,
                         cs: Charset = Charsets.UTF_8,
                         isChunked: Boolean = false,
                         urlSafe: Boolean = true): String = {
    val input = Strings.getUtf8Bytes(str)
    val bytes = encodeBase64(input, isChunked, urlSafe)
    Strings.getUtf8String(bytes)
  }

  /**
   * base64 방식으로 인코딩된 데이터를 디코딩합니다.
   */
  def decodeBase64(input: Array[Byte]): Array[Byte] =
    Base64.decodeBase64(input)

  def decodeBase64String(str: String): String =
    decodeBase64String(str, Charsets.UTF_8)

  /**
   * base64 방식으로 인코딩된 문자열을 디코딩하여 plain text로 반환합니다.
   */
  def decodeBase64String(str: String, cs: Charset = Charsets.UTF_8): String = {
    val input = Strings.getUtf8Bytes(str)
    val bytes = decodeBase64(input)
    new String(bytes, cs)
  }
}

trait StringEncoder {

  def encode(bytes: Array[Byte]): String = {
    if (bytes != null) new String(bytes)
    else null: String
  }

  def decode(str: String): Array[Byte] = {
    if (str != null) str.getBytes
    else Array.emptyByteArray
  }
}

object StringEncoder extends StringEncoder

trait Base64StringEncoder extends StringEncoder {

  private[this] def codec: Base64 = new Base64()

  override def encode(bytes: Array[Byte]): String =
    codec.encodeAsString(bytes)

  override def decode(str: String): Array[Byte] =
    codec.decode(str)
}

object Base64StringEncoder extends Base64StringEncoder
