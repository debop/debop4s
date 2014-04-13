package debop4s.core.jvm

import java.io.OutputStream
import java.nio.{ByteOrder, ByteBuffer}
import scala.collection.mutable
import scala.concurrent.duration.Duration

/**
 * CpuProfile
 * Created by debop on 2014. 4. 13.
 */
case class CpuProfile(counts: Map[Seq[StackTraceElement], Long], // counts of each observed stack.
                      duration: Duration, // The amout of time over which the sample was taken.
                      count: Int, // The number of samples taken.
                      missed: Int // The number of samples missed.
                       ) {

  /**
   * Write a Google pprof-compatible profile to `out`. The format is
   * documented here:
   *
   *   http://google-perftools.googlecode.com/svn/trunk/doc/cpuprofile-fileformat.html
   */
  def writeGoogleProfile(out: OutputStream) {
    var next = 1
    val uniq = mutable.HashMap[StackTraceElement, Int]()
    val word = ByteBuffer.allocate(8)
    word.order(ByteOrder.LITTLE_ENDIAN)

    def putWord(n: Long) {
      word.clear()
      word.putLong(n)
      out.write(word.array())
    }

    def putString(s: String) {
      out.write(s.getBytes)
    }

    putString(s"---symbol\nbinary=${ Jvm().mainClassName }\n")
  }
}
