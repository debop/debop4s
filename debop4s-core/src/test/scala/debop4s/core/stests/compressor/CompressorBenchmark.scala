package debop4s.core.stests.compressor

import debop4s.core.compress.{SnappyCompressor, DeflateCompressor, GZipCompressor}
import debop4s.core.utils.Charsets
import org.scalameter.{Gen, PerformanceTest}

/**
 * CompressorBenchmark
 * @author Sunghyouk Bae
 */
object CompressorBenchmark extends PerformanceTest.Quickbenchmark {

  val sizes = Gen.range("size")(3000, 15000, 3000)
  val compressors = Gen.enumeration("compressors")(new GZipCompressor(), new DeflateCompressor(), new SnappyCompressor())
  val inputs = Gen.tupled(sizes, compressors)

  performance of "Compressor" in {
    measure method "compress" in {
      using(inputs) in {
        case (sz, compressor) =>
          val text = "동해물과 백두산이 마르고 닳도록 Hello World! " * sz
          val bytes = text.getBytes(Charsets.UTF_8)
          val c = compressor.compress(bytes)
          val d = compressor.decompress(c)
      }
    }
  }
}
