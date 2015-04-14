package debop4s.core.compress

/** for Java compatibility */
abstract class AbstractCompressor extends Compressor {}

/**
 * 데이터를 압축/복원을 수행합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:52
 */
trait Compressor {

  val BUFFER_SIZE = 1024

  /**
   * 데이터를 압축합니다.
   * @param plainBytes 압축할 데이터
   * @return 압축된 데이터
   */
  protected def doCompress(plainBytes: Array[Byte]): Array[Byte]

  /**
   * 압축된 데이터를 복원합니다.
   * @param compressedBytes 압축된 데이터
   * @return 원본 데이터
   */
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
