package debop4s.core.compress

import java.io.InputStream

import akka.util.ByteString
import debop4s.core.BinaryStringFormat
import debop4s.core.utils.{Charsets, Streams, Strings}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Compressors
 * @author sunghyouk.bae@gmail.com
 */
object Compressors {

  def compressByteString(compressor: Compressor, plainText: String): ByteString = {
    if (Strings.isEmpty(plainText)) ByteString.empty
    else ByteString(compressor.compress(Strings.getUtf8Bytes(plainText)))
  }

  def decompressByteString(compressor: Compressor, bs: ByteString): String = {
    if (bs == null || bs.isEmpty) Strings.EMPTY_STR
    else new String(compressor.decompress(bs.toArray[Byte]), Charsets.UTF_8)
  }

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
                          stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal) = Future {
    compressString(compressor, plainText, stringFormat)
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
                            stringFormat: BinaryStringFormat = BinaryStringFormat.HexDecimal) = Future {
    decompressString(compressor, compressedText, stringFormat)
  }

  def compressStream(compressor: Compressor, inputStream: InputStream): InputStream = {
    val bytes = compressor.compress(Streams.toByteArray(inputStream))
    Streams.toInputStream(bytes)
  }

  def compressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[InputStream] = Future {
    compressStream(compressor, inputStream)
  }

  def decompressStream(compressor: Compressor, inputStream: InputStream): InputStream = {
    val bytes = compressor.decompress(Streams.toByteArray(inputStream))
    Streams.toInputStream(bytes)
  }

  def decompressStreamAsync(compressor: Compressor, inputStream: InputStream): Future[InputStream] = Future {
    decompressStream(compressor, inputStream)
  }
}
