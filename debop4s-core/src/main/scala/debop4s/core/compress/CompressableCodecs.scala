package debop4s.core.compress

import debop4s.core.utils.{Base64StringEncoder, StringEncoder}


trait GZipStringEncoder extends StringEncoder {
  private[this] val gzip = GZipCompressor()

  override def encode(bytes: Array[Byte]): String = {
    Base64StringEncoder.encode(gzip.compress(bytes))
  }

  def encodeString(str: String) = encode(str.getBytes("UTF-8"))

  override def decode(str: String): Array[Byte] = {
    gzip.decompress(Base64StringEncoder.decode(str))
  }

  def decodeString(str: String): String = new String(decode(str), "UTF-8")
}

object GZipStringEncoder extends GZipStringEncoder

trait DeflateStringEncoder extends StringEncoder {
  private[this] val deflater = DeflateCompressor()

  override def encode(bytes: Array[Byte]): String = {
    Base64StringEncoder.encode(deflater.compress(bytes))
  }

  def encodeString(str: String) = encode(str.getBytes("UTF-8"))

  override def decode(str: String): Array[Byte] = {
    deflater.decompress(Base64StringEncoder.decode(str))
  }

  def decodeString(str: String): String = new String(decode(str), "UTF-8")
}

object DeflateStringEncoder extends DeflateStringEncoder

trait SnappyStringEncoder extends StringEncoder {
  private[this] val snappy = SnappyCompressor()

  override def encode(bytes: Array[Byte]): String = {
    Base64StringEncoder.encode(snappy.compress(bytes))
  }

  def encodeString(str: String) = encode(str.getBytes("UTF-8"))

  override def decode(str: String): Array[Byte] = {
    snappy.decompress(Base64StringEncoder.decode(str))
  }

  def decodeString(str: String): String = new String(decode(str), "UTF-8")
}

object SnappyStringEncoder extends SnappyStringEncoder
