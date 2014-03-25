package com.github.debop4s.core.compress

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}
import com.github.debop4s.core.utils.With

/**
 * GZip 알고리즘을 이용한 압축기
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:55
 */
class GZipCompressor extends Compressor {

    override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {
        With.using(new ByteArrayOutputStream()) { bos =>
            With.using(new GZIPOutputStream(bos)) { gzip =>
                gzip.write(plainBytes)
            }
            bos.toByteArray
        }
    }

    override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {
        With.using(new ByteArrayOutputStream()) { bos =>
            With.using(new ByteArrayInputStream(compressedBytes)) { bis =>
                With.using(new GZIPInputStream(bis)) { gzip =>
                    val buffer = new Array[Byte](BUFFER_SIZE)
                    var n = 0
                    do {
                        n = gzip.read(buffer, 0, BUFFER_SIZE)
                        if (n > 0) bos.write(buffer, 0, n)
                    } while (n > 0)
                }
            }
            bos.toByteArray
        }
    }
}
