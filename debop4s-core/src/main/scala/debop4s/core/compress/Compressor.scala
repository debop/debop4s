package debop4s.core.compress

import debop4s.core.BinaryStringFormat
import debop4s.core.BinaryStringFormat.BinaryStringFormat
import debop4s.core.utils.{ Streams, Strings }
import java.io.{ OutputStream, InputStream }
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * 데이터를 압축/복원을 수행합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:52
 */
trait Compressor {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val BUFFER_SIZE = 1024

  protected def doCompress(plainBytes: Array[Byte]): Array[Byte]

  protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte]

  /**
   * 데이터를 압축합니다.
   * @param plainBytes 압축할 데이터
   * @return 압축된 데이터
   */
  def compress(plainBytes: Array[Byte]): Array[Byte] = {
    if (plainBytes == null || plainBytes.length == 0)
      return Array.emptyByteArray

    doCompress(plainBytes)
  }

  /**
   * 압축된 데이터를 복원합니다.
   * @param compressedBytes 압축된 데이터
   * @return 복원된 데이터
   */
  def decompress(compressedBytes: Array[Byte]): Array[Byte] = {
    if (compressedBytes == null || compressedBytes.length == 0)
      return Array.emptyByteArray

    doDecompress(compressedBytes)
  }
}

/**
 * 압축 관련 Helper class 입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:57
 */
object Compressor {

  def compressString(compressor: Compressor,
                     plainText: String,
                     stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): String = {
    if (Strings.isEmpty(plainText))
      Strings.EMPTY_STR

    val bytes = compressor.compress(Strings.getUtf8Bytes(plainText))
    Strings.getStringFromBytes(bytes, stringFormat)
  }

  def compressStringAsync(compressor: Compressor,
                          plainText: String,
                          stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal) = Future {
    compressString(compressor, plainText, stringFormat)
  }

  def decompressString(compressor: Compressor,
                       compressedText: String,
                       stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): String = {
    if (Strings.isEmpty(compressedText))
      Strings.EMPTY_STR

    val bytes = compressor.decompress(Strings.getBytesFromString(compressedText, stringFormat))
    Strings.getUtf8String(bytes)
  }

  def decomperssStringAsync(compressor: Compressor,
                            compressedText: String,
                            stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal) = Future {
    decompressString(compressor, compressedText, stringFormat)
  }

  def compressStream(compressor: Compressor, inputStream: InputStream): OutputStream = {
    val bytes = compressor.compress(Streams.toByteArray(inputStream))
    Streams.toOutputStream(bytes)
  }

  def compressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[OutputStream] = Future {
    compressStream(compressor, inputStream)
  }

  def decompressStream(compressor: Compressor, inputStream: InputStream): OutputStream = {
    val bytes = compressor.decompress(Streams.toByteArray(inputStream))
    Streams.toOutputStream(bytes)
  }

  def decompressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[OutputStream] = Future {
    decompressStream(compressor, inputStream)
  }
}
