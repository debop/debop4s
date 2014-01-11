package kr.debop4s.core.compress

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{InflaterInputStream, DeflaterOutputStream}
import kr.debop4s.core.logging.Logger

/**
 * kr.debop4s.core.compress.DeflateCompressor
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:56
 */
class DeflateCompressor extends Compressor {

    override lazy val log = Logger[DeflateCompressor]

    override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {

        val bos = new ByteArrayOutputStream()
        val deflater = new DeflaterOutputStream(bos)
        try {
            deflater.write(plainBytes)
            deflater.close()

            bos.toByteArray
        } finally {
            bos.close()
        }
    }

    override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {

        val bos = new ByteArrayOutputStream()
        val bis = new ByteArrayInputStream(compressedBytes)
        val inflater = new InflaterInputStream(bis)

        try {
            val buff = new Array[Byte](BUFFER_SIZE)
            var n = 0

            do {
                n = inflater.read(buff, 0, BUFFER_SIZE)
                if (n > 0) bos.write(buff, 0, n)
            } while (n > 0)

            bos.toByteArray
        }
        finally {
            inflater.close()
            bis.close()
            bos.close()
        }
    }
}
