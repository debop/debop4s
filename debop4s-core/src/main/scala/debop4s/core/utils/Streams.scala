package debop4s.core.utils

import java.io._
import java.nio.charset.Charset

import debop4s.core._
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

/**
 * debop4s.core.tools.Streams
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:00
 */
object Streams {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  private[this] val BUFFER_SIZE: Int = 4096

  /**
   * inputStream 정보를 읽어, outputStream에 복사합니다.
   */
  @tailrec
  def copy(inputStream: InputStream, outputStream: OutputStream, bufferSize: Int = BUFFER_SIZE): Unit = {
    val buffer = new Array[Byte](bufferSize)

    inputStream.read(buffer, 0, buffer.length) match {
      case -1 => ()
      case n =>
        outputStream.write(buffer, 0, n)
        copy(inputStream, outputStream, bufferSize)
    }
  }

  def toInputStream(bytes: Array[Byte]): InputStream =
    new ByteArrayInputStream(bytes)

  def toInputStream(str: String, cs: Charset = Charsets.UTF_8): InputStream =
    if (Strings.isEmpty(str)) new ByteArrayInputStream(Array.emptyByteArray)
    else toInputStream(str.getBytes(cs))

  def toOutputStream(is: InputStream): OutputStream = {
    val bos = new ByteArrayOutputStream()
    copy(is, bos)
    bos
  }

  def toOutputStream(bytes: Array[Byte]): OutputStream = {
    if (bytes == null || bytes.length == 0)
      return new ByteArrayOutputStream()

    val os = new ByteArrayOutputStream(bytes.length)
    using(new ByteArrayInputStream(bytes)) { is =>
      copy(is, os)
    }
    os
  }

  def toOutputStream(str: String, cs: Charset = Charsets.UTF_8): OutputStream = {
    if (Strings.isEmpty(str))
      new ByteArrayOutputStream()
    else
      toOutputStream(str.getBytes(cs))
  }

  def toByteArray(is: InputStream): Array[Byte] = {
    if (is == null)
      return Array.emptyByteArray

    using(new ByteArrayOutputStream()) { os =>
      copy(is, os)
      os.toByteArray
    }
  }

  def toString(is: InputStream): String = {
    if (is == null) ""
    else Strings.getUtf8String(toByteArray(is))
  }

  def toString(is: InputStream, cs: Charset): String = {
    if (is == null) ""
    else new String(toByteArray(is), cs)
  }


  // TODO: Scala 고유의 Stream 처리 기능을 제공하자.
}
