package kr.debop4s.core.stests.compressor

import kr.debop4s.core.compress.{DeflateCompressor, Compressor, GZipCompressor}
import kr.debop4s.core.utils.Charsets
import lombok.extern.slf4j.Slf4j
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit


/**
 * kr.debop4s.core.tests.compressor.CompressTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 10. 오후 9:31
 */
@Slf4j
class CompressorTest extends AssertionsForJUnit {

    @Test
    def gzipTest() {
        val gzip = new GZipCompressor()
        compressorTest(gzip)
    }

    @Test
    def deflateTest() {
        val deflater = new DeflateCompressor()
        compressorTest(deflater)
    }

    private def compressorTest(compressor: Compressor) {
        assert(compressor.compress(null) == Array.emptyByteArray)
        assert(compressor.compress(Array.emptyByteArray) == Array.emptyByteArray)

        val text = "동해물과 백두산이 마르고 닳도록 Hello World!" * 100
        val compressedBytes = compressor.compress(text.getBytes(Charsets.UTF_8))
        assert(compressedBytes != null)
        assert(compressedBytes != Array.emptyByteArray)

        val textBytes = compressor.decompress(compressedBytes)
        assert(textBytes != null)
        assert(textBytes != Array.emptyByteArray)

        val text2 = new String(textBytes, Charsets.UTF_8)
        assert(text2 != null)
        assert(text2 === text)
    }
}
