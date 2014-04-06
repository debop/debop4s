package debop4s.core.utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, OutputStream, InputStream}
import java.nio.charset.Charset
import org.slf4j.LoggerFactory
import scala.annotation.tailrec

/**
 * debop4s.core.tools.Streams
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:00
 */
object Streams {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val BUFFER_SIZE = 1024

  /**
   * inputStream 정보를 읽어, outputStream에 복사합니다.
   */
  @tailrec
  @inline
  def copy(inputStream: InputStream, outputStream: OutputStream, bufferSize: Int = BUFFER_SIZE) {
    val buffer = new Array[Byte](bufferSize)
    inputStream.read(buffer, 0, buffer.size) match {
      case -1 => ()
      case n =>
        outputStream.write(buffer, 0, n)
        copy(inputStream, outputStream, bufferSize)
    }
  }

  @inline
  def toOutputStream(is: InputStream): OutputStream = {
    val bos = new ByteArrayOutputStream()
    copy(is, bos)
    bos
  }

  @inline
  def toOutputStream(bytes: Array[Byte]): OutputStream = {
    if (bytes == null || bytes.length == 0)
      return new ByteArrayOutputStream()

    val is = new ByteArrayInputStream(bytes)
    val os = new ByteArrayOutputStream(bytes.length)
    copy(is, os)
    os
  }

  def toOutputStream(str: String, cs: Charset = Charsets.UTF_8): OutputStream = {
    if (Strings.isEmpty(str))
      return new ByteArrayOutputStream()

    toOutputStream(str.getBytes(cs))
  }

  @inline
  def toByteArray(is: InputStream): Array[Byte] = {
    if (is == null)
      Array.emptyByteArray

    val os = new ByteArrayOutputStream()
    try {
      copy(is, os)
      os.toByteArray
    } finally {
      os.close()
    }
  }

  def toString(is: InputStream): String =
    Strings.getUtf8String(toByteArray(is))

  def toString(is: InputStream, cs: Charset): String =
    new String(toByteArray(is), cs)


  // TODO: Scala 고유의 Stream 처리 기능을 제공하자.
}
