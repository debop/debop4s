package debop4s.core.compress

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.compress._
import debop4s.core.utils.Charsets
import org.scalatest.{ BeforeAndAfter, Matchers, FunSuite }
import org.slf4j.LoggerFactory


/**
 * debop4s.core.tests.compress.CompressTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 10. 오후 9:31
 */
class CompressorFunSuite extends AbstractCoreFunSuite {

  test("gzip test") {
    val gzip = new GZipCompressor()
    compressorTest(gzip)
  }

  test("deflate test") {
    val deflater = new DeflateCompressor()
    compressorTest(deflater)
  }

  test("snappy test") {
    val snappy = new SnappyCompressor()
    compressorTest(snappy)
  }

  private def compressorTest(compressor: Compressor) {
    log.debug(s"Compressor test: compress=${ compressor.getClass.getSimpleName }")

    assert(compressor.compress(null) == Array.emptyByteArray)
    assert(compressor.compress(Array.emptyByteArray) == Array.emptyByteArray)

    val text = "동해물과 백두산이 마르고 닳도록 Hello World! " * 100
    val compressedBytes = compressor.compress(text.getBytes("UTF-8"))
    assert(compressedBytes != null)
    assert(compressedBytes != Array.emptyByteArray)

    val textBytes = compressor.decompress(compressedBytes)
    assert(textBytes != null)
    assert(textBytes != Array.emptyByteArray)

    val text2 = new String(textBytes, "UTF-8")
    assert(text2 != null)
    assert(text2 == text)


  }
}

