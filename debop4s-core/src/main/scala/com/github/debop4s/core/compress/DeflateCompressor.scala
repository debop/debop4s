package com.github.debop4s.core.compress

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{InflaterInputStream, DeflaterOutputStream}
import com.github.debop4s.core.utils.With

/**
 * DeflateCompressor
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:56
 */
class DeflateCompressor extends Compressor {

    override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {
        With.using(new ByteArrayOutputStream()) { bos =>
            With.using(new DeflaterOutputStream(bos)) { deflater =>
                deflater.write(plainBytes)
            }
            bos.toByteArray
        }
    }

    override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {

        With.using(new ByteArrayOutputStream()) { bos =>
            With.using(new ByteArrayInputStream(compressedBytes)) { bis =>
                With.using(new InflaterInputStream(bis)) { inflater =>
                    val buff = new Array[Byte](BUFFER_SIZE)
                    var n = 0

                    do {
                        n = inflater.read(buff, 0, BUFFER_SIZE)
                        if (n > 0) bos.write(buff, 0, n)
                    } while (n > 0)

                    bos.toByteArray
                }
            }
        }
    }
}
