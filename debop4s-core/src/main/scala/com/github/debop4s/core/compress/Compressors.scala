package com.github.debop4s.core.compress

import com.github.debop4s.core.BinaryStringFormat
import com.github.debop4s.core.BinaryStringFormat.BinaryStringFormat
import com.github.debop4s.core.parallels.Asyncs
import com.github.debop4s.core.utils.{Streams, Strings}
import java.io.{OutputStream, InputStream}
import scala.concurrent.Future

/**
 * 압축 관련 Helper class 입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:57
 */
object Compressors {

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
                            stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): Future[String] = {
        Asyncs.run {
            compressString(compressor, plainText, stringFormat)
        }
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
                              stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): Future[String] = {
        Asyncs.run {
            decompressString(compressor, compressedText, stringFormat)
        }
    }

    def compressStream(compressor: Compressor, inputStream: InputStream): OutputStream = {
        val bytes = compressor.compress(Streams.toByteArray(inputStream))
        Streams.toOutputStream(bytes)
    }

    def compressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[OutputStream] = {
        Asyncs.run {
            compressStream(compressor, inputStream)
        }
    }

    def decompressStream(compressor: Compressor, inputStream: InputStream): OutputStream = {
        val bytes = compressor.decompress(Streams.toByteArray(inputStream))
        Streams.toOutputStream(bytes)
    }

    def decompressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[OutputStream] = {
        Asyncs.run {
            decompressStream(compressor, inputStream)
        }
    }
}
