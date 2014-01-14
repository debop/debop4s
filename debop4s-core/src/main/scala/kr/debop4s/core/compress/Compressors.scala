package kr.debop4s.core.compress

import java.io.{OutputStream, InputStream}
import kr.debop4s.core.BinaryStringFormat
import kr.debop4s.core.BinaryStringFormat.BinaryStringFormat
import kr.debop4s.core.parallels.Asyncs
import kr.debop4s.core.utils.{Streams, Strings}
import org.slf4j.LoggerFactory
import scala.concurrent.Future

/**
 * 압축 관련 Helper class 입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:57
 */
object Compressors {

    lazy val log = LoggerFactory.getLogger(getClass)

    def compressString(compressor: Compressor,
                       plainText: String,
                       stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): String = {
        if (Strings.isEmpty(plainText))
            Strings.EMPTY_STR

        val bytes = compressor.compress(Strings.getBytesUtf8(plainText))
        Strings.getStringFromBytes(bytes, stringFormat)
    }

    def compressStringAsync(compressor: Compressor,
                            plainText: String,
                            stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): Future[String] = {
        Asyncs.startNew {
            compressString(compressor, plainText, stringFormat)
        }
    }

    def decompressString(compressor: Compressor,
                         compressedText: String,
                         stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): String = {
        if (Strings.isEmpty(compressedText))
            Strings.EMPTY_STR

        val bytes = compressor.decompress(Strings.getBytesFromString(compressedText, stringFormat))
        Strings.getStringUtf8(bytes)
    }

    def decomperssStringAsync(compressor: Compressor,
                              compressedText: String,
                              stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal): Future[String] = {
        Asyncs.startNew {
            decompressString(compressor, compressedText, stringFormat)
        }
    }

    def compressStream(compressor: Compressor, inputStream: InputStream): OutputStream = {
        val bytes = compressor.compress(Streams.toByteArray(inputStream))
        Streams.toOutputStream(bytes)
    }

    def compressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[OutputStream] = {
        Asyncs.startNew {
            compressStream(compressor, inputStream)
        }
    }

    def decompressStream(compressor: Compressor, inputStream: InputStream): OutputStream = {
        val bytes = compressor.decompress(Streams.toByteArray(inputStream))
        Streams.toOutputStream(bytes)
    }

    def decompressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[OutputStream] = {
        Asyncs.startNew {
            decompressStream(compressor, inputStream)
        }
    }
}
