package debop4s.core.utils

import java.io._
import java.nio.charset.Charset

import debop4s.core.{Logging, _}

import scala.annotation.tailrec

/**
 * debop4s.core.tools.Streams
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:00
 */
object Streams extends Logging {

  val BUFFER_SIZE = 4096

  /**
   * inputStream 정보를 읽어, outputStream에 복사합니다.
   */
  @tailrec
  def copy(inputStream: InputStream, outputStream: OutputStream, bufferSize: Int = BUFFER_SIZE) {
    val buffer = new Array[Byte](bufferSize)

    inputStream.read(buffer, 0, buffer.length) match {
      case -1 => ()
      case n =>
        outputStream.write(buffer, 0, n)
        copy(inputStream, outputStream, bufferSize)
    }
  }

  def toInputStream(bytes: Array[Byte]): InputStream = {
    new ByteArrayInputStream(bytes)
  }

  def toInputStream(str: String, cs: Charset = Charsets.UTF_8): InputStream = {
    if (Strings.isEmpty(str)) new ByteArrayInputStream(Array.emptyByteArray)
    else toInputStream(str.getBytes(cs))
  }

  def toOutputStream(is: InputStream): OutputStream = {
    val bos = new ByteArrayOutputStream()
    copy(is, bos)
    bos
  }

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
