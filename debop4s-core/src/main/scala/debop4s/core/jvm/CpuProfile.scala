package debop4s.core.jvm

import debop4s.core.Time
import debop4s.core.conversions.time._
import debop4s.core.utils.Stopwatch
import java.io.OutputStream
import java.lang.management.ManagementFactory
import java.nio.{ByteOrder, ByteBuffer}
import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Promise, Future}

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

        for ((stack, _) <- counts; frame <- stack if !uniq.contains(frame)) {
            putString(s"$next%016x ${ frame.toString }")
            uniq(frame) = next
            next += 1
        }
        putString("---\n---prifile\n")
        Seq(0, 3, 0, 1, 0).foreach(w => putWord(w))
        for ((stack, n) <- counts if !stack.isEmpty) {
            putWord(n)
            putWord(stack.size)
            stack.foreach(frame => putWord(uniq(frame)))
        }
        putWord(0)
        putWord(1)
        putWord(0)
        out.flush()
    }
}

object CpuProfile {
    /**
    * Profile CPU usage of threads in `state` for `howlong`, sampling
    * stacks at `frequency` Hz.
    *
    * As an example, using Nyquist's sampling theorem, we see that
    * sampling at 100Hz will accurately represent components 50Hz or
    * less; ie. any stack that contributes 2% or more to the total CPU
    * time.
    *
    * Note that the maximum sampling frequency is set to 1000Hz.
    * Anything greater than this is likely to consume considerable
    * amounts of CPU while sampling.
    *
    * The profiler will discount its own stacks.
    *
    * TODO:
    *
    *   - Should we synthesize GC frames? GC has significant runtime
    *   impact, so it seems nonfaithful to exlude them.
    *   - Limit stack depth?
    */
    def record(howlong: Duration, frequency: Int, state: Thread.State): CpuProfile = {
        require(frequency < 10000)

        // TODO: it may make sense to write a custom hash function here
        // that needn't traverse the all stack trace elems. Usually, the
        // top handful of frames are distinguishing.
        val counts = mutable.HashMap[Seq[StackTraceElement], Long]()
        val bean = ManagementFactory.getThreadMXBean
        val stopwatch = Stopwatch()
        val end = howlong.fromNow
        val period = (1000000 / frequency).toMicros
        val myId = Thread.currentThread().getId
        var next = Time.now

        var n = 0
        var nmissed = 0

        stopwatch.start()

        while (Time.now < end) {
            for (thread <- bean.dumpAllThreads(false, false)
                 if thread.getThreadState == state &&
                    thread.getThreadId != myId) {
                val s = thread.getStackTrace.toSeq
                if (!s.isEmpty)
                    counts(s) = counts.getOrElse(s, 0L) + 1L
            }

            n += 1
            next += period

            while (next < Time.now && next < end) {
                nmissed += 1
                next += period
            }
            val sleep = (next - Time.now).toMillis max 0
            Thread.sleep(sleep)
        }

        stopwatch.stop()
        CpuProfile(counts.toMap, stopwatch.getElapsedTime.toMillis, n, nmissed)
    }

    /**
     * Call `record` in a thread with the given parameters, returning a
     * `Future` representing the completion of the profile.
     */
    def recordInThread(howlong: Duration, frequency: Int, state: Thread.State): Future[CpuProfile] = {
        val p = Promise[CpuProfile]()
        val thread = new Thread("CpuProfile") {
            override def run() {
                p success record(howlong, frequency, state)
            }
        }
        thread.start()
        p.future
    }

    def recordInThread(howlong: Duration, frequency: Int): Future[CpuProfile] =
        recordInThread(howlong, frequency, Thread.State.RUNNABLE)
}
