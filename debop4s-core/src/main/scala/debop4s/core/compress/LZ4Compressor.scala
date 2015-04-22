package debop4s.core.compress

import net.jpountz.lz4
import net.jpountz.lz4.{LZ4SafeDecompressor, LZ4Factory}

/**
 * companion object for [[LZ4Compressor]]
 */
object LZ4Compressor {
  def apply(): LZ4Compressor = new LZ4Compressor
}

/**
 * LZ4 알고리즘을 이용한 Compressor
 * @author sunghyouk.bae@gmail.com
 */
class LZ4Compressor extends Compressor {

  private lazy val factory: LZ4Factory = LZ4Factory.fastestInstance()
  private lazy val compressor: lz4.LZ4Compressor = factory.fastCompressor()
  private lazy val decompressor: LZ4SafeDecompressor = factory.safeDecompressor()

  /**
   * 데이터를 압축합니다.
   * @param plainBytes 압축할 데이터
   * @return 압축된 데이터
   */
  override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {
    compressor.compress(plainBytes)
  }

  /**
   * 압축된 데이터를 복원합니다.
   * @param compressedBytes 압축된 데이터
   * @return 원본 데이터
   */
  override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {
    decompressor.decompress(compressedBytes, compressedBytes.length * 1000)
  }
}
