package debop4s.core.compress

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.utils.Streams

/**
 * CompressorsFunSuite
 * @author sunghyouk.bae@gmail.com 2014. 7. 26.
 */
class CompressorsFunSuite extends AbstractCoreFunSuite {

  var compressors = Seq(new GZipCompressor, new DeflateCompressor, new SnappyCompressor, new LZ4Compressor)
  val text = "동해물과 백두산이 마르고 닳도록 Hello World! " * 100

  test("compress ByteString") {
    compressors.foreach { compressor =>
      val compressed = Compressors.compressByteString(compressor, text)
      val converted = Compressors.decompressByteString(compressor, compressed)
      converted shouldEqual text
    }
  }

  test("compress String") {
    compressors.foreach { compressor =>
      val compressed = Compressors.compressString(compressor, text)
      val converted = Compressors.decompressString(compressor, compressed)
      converted shouldEqual text
    }
  }

  test("compress Stream") {
    compressors.foreach { compressor =>
      val compressed = Compressors.compressStream(compressor, Streams.toInputStream(text))
      val converted = Compressors.decompressStream(compressor, compressed)
      Streams.toString(converted) shouldEqual text
    }
  }

}
