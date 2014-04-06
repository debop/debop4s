package com.github.debop4s.core.compress

import org.xerial.snappy.Snappy

/**
 * Google Snappy 압축 라이브러리를 사용합니다.
 * Created by debop on 2014. 3. 14.
 */
class SnappyCompressor extends Compressor {

  override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {
    Snappy.compress(plainBytes)
  }

  override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {
    Snappy.uncompress(compressedBytes)
  }
}

object SnappyCompressor {
  def apply(): SnappyCompressor = new SnappyCompressor()
}
