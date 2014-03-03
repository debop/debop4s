package com.github.debop4s.core.compress

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.core.compress.GZipCompressor
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:55
 */
class GZipCompressor extends Compressor {

  private lazy val log = LoggerFactory.getLogger(getClass)

  override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {
    val bos = new ByteArrayOutputStream()
    val gzip = new GZIPOutputStream(bos)

    try {
      gzip.write(plainBytes)
      gzip.close()

      bos.toByteArray
    } finally {
      bos.close()
    }
  }

  override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {
    val bos = new ByteArrayOutputStream()
    val bis = new ByteArrayInputStream(compressedBytes)
    val gzip = new GZIPInputStream(bis)

    try {
      val buffer = new Array[Byte](BUFFER_SIZE)
      var n = 0
      do {
        n = gzip.read(buffer, 0, BUFFER_SIZE)
        if (n > 0) bos.write(buffer, 0, n)
      } while (n > 0)

      bos.toByteArray
    } finally {
      gzip.close()
      bos.close()
    }
  }
}
