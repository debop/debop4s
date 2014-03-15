package com.github.debop4s.core.stests.compressor

import com.github.debop4s.core.compress.{SnappyCompressor, DeflateCompressor, Compressor, GZipCompressor}
import com.github.debop4s.core.utils.Charsets
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import org.slf4j.LoggerFactory


/**
 * com.github.debop4s.core.tests.compressor.CompressTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 10. 오후 9:31
 */
class CompressorTest extends FunSuite with Matchers with BeforeAndAfter {

    private lazy val log = LoggerFactory.getLogger(getClass)

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
        assert(compressor.compress(null) == Array.emptyByteArray)
        assert(compressor.compress(Array.emptyByteArray) == Array.emptyByteArray)

        val text = "동해물과 백두산이 마르고 닳도록 Hello World! " * 100
        val compressedBytes = compressor.compress(text.getBytes(Charsets.UTF_8))
        assert(compressedBytes != null)
        assert(compressedBytes != Array.emptyByteArray)

        val textBytes = compressor.decompress(compressedBytes)
        assert(textBytes != null)
        assert(textBytes != Array.emptyByteArray)

        val text2 = new String(textBytes, Charsets.UTF_8)
        assert(text2 != null)
        assert(text2 == text)
    }
}
