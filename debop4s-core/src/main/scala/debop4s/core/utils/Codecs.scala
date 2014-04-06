package debop4s.core.utils

import debop4s.core.compress.{DeflateCompressor, SnappyCompressor, GZipCompressor}
import java.nio.charset.Charset
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory

/**
 * Codec helper object
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:27
 */
object Codecs {

  private lazy val log = LoggerFactory.getLogger(getClass)

  /**
   * 입력 데이터를 base64 방식으로 인코딩합니다.
   */
  def encodeBase64(input: Array[Byte],
                   isChucked: Boolean = false,
                   urlSafe: Boolean = true): Array[Byte] =
    Base64.encodeBase64(input, isChucked, urlSafe)

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
  def encode(bytes: Array[Byte]): String = new String(bytes)

  def decode(str: String): Array[Byte] = str.getBytes
}

object StringEncoder extends StringEncoder

trait Base64StringEncoder extends StringEncoder {
  private[this] def codec = new Base64()

  override def encode(bytes: Array[Byte]): String = {
    codec.encodeAsString(bytes)
  }

  override def decode(str: String): Array[Byte] = {
    codec.decode(str)
  }
}

object Base64StringEncoder extends Base64StringEncoder

trait GZipStringEncoder extends StringEncoder {
  private[this] val gzip = GZipCompressor()

  override def encode(bytes: Array[Byte]): String = {
    Base64StringEncoder.encode(gzip.compress(bytes))
  }

  def encodeString(str: String) = encode(str.getBytes("UTF-8"))

  override def decode(str: String): Array[Byte] = {
    gzip.decompress(Base64StringEncoder.decode(str))
  }

  def decodeString(str: String): String = new String(decode(str), "UTF-8")
}

object GZipStringEncoder extends GZipStringEncoder

trait DeflateStringEncoder extends StringEncoder {
  private[this] val deflater = DeflateCompressor()

  override def encode(bytes: Array[Byte]): String = {
    Base64StringEncoder.encode(deflater.compress(bytes))
  }

  def encodeString(str: String) = encode(str.getBytes("UTF-8"))

  override def decode(str: String): Array[Byte] = {
    deflater.decompress(Base64StringEncoder.decode(str))
  }

  def decodeString(str: String): String = new String(decode(str), "UTF-8")
}

object DeflateStringEncoder extends DeflateStringEncoder

trait SnappyStringEncoder extends StringEncoder {
  private[this] val snappy = SnappyCompressor()

  override def encode(bytes: Array[Byte]): String = {
    Base64StringEncoder.encode(snappy.compress(bytes))
  }

  def encodeString(str: String) = encode(str.getBytes("UTF-8"))

  override def decode(str: String): Array[Byte] = {
    snappy.decompress(Base64StringEncoder.decode(str))
  }

  def decodeString(str: String): String = new String(decode(str), "UTF-8")
}

object SnappyStringEncoder extends SnappyStringEncoder