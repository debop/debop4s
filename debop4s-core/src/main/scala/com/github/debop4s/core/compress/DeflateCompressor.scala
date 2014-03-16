package com.github.debop4s.core.compress

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{InflaterInputStream, DeflaterOutputStream}

/**
 * DeflateCompressor
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:56
 */
class DeflateCompressor extends Compressor {

    override protected def doCompress(plainBytes: Array[Byte]): Array[Byte] = {
        val bos = new ByteArrayOutputStream()
        try {
            val deflater = new DeflaterOutputStream(bos)
            deflater.write(plainBytes)
            deflater.close()

            bos.toByteArray
        } finally {
            bos.close()
        }
    }

    override protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte] = {

        val bos = new ByteArrayOutputStream()
        var bis = None: Option[ByteArrayInputStream]
        var inflater = None: Option[InflaterInputStream]

        try {
            bis = Some(new ByteArrayInputStream(compressedBytes))
            inflater = Some(new InflaterInputStream(bis.get))

            val buff = new Array[Byte](BUFFER_SIZE)
            var n = 0

            do {
                n = inflater.get.read(buff, 0, BUFFER_SIZE)
                if (n > 0) bos.write(buff, 0, n)
            } while (n > 0)

            bos.toByteArray
        }
        finally {
            if (inflater.isDefined) inflater.get.close()
            if (bis.isDefined) bis.get.close()
            bos.close()
        }
    }
}
