package com.github.debop4s.core.compress

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

/**
 * GZip 알고리즘을 이용한 압축기
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:55
 */
class GZipCompressor extends Compressor {

    override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {
        var bos = None: Option[ByteArrayOutputStream]
        var gzip = None: Option[GZIPOutputStream]
        try {
            bos = Some(new ByteArrayOutputStream())
            gzip = Some(new GZIPOutputStream(bos.get))
            gzip.get.write(plainBytes)
            gzip.get.close()

            bos.get.toByteArray
        } finally {
            if (bos.isDefined) bos.get.close()
        }
    }

    override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {
        var bos = None: Option[ByteArrayOutputStream]
        var bis = None: Option[ByteArrayInputStream]
        var gzip = None: Option[GZIPInputStream]
        try {
            bos = Some(new ByteArrayOutputStream())
            bis = Some(new ByteArrayInputStream(compressedBytes))
            gzip = Some(new GZIPInputStream(bis.get))

            val buffer = new Array[Byte](BUFFER_SIZE)
            var n = 0
            do {
                n = gzip.get.read(buffer, 0, BUFFER_SIZE)
                if (n > 0) bos.get.write(buffer, 0, n)
            } while (n > 0)

            bos.get.toByteArray
        } finally {
            if (gzip.isDefined) gzip.get.close()
            if (bos.isDefined) bos.get.close()
        }
    }
}
