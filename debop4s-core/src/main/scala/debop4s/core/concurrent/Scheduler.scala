package debop4s.core.concurrent

import java.lang.management.ManagementFactory
import java.util
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionException, ThreadFactory}

import debop4s.core.utils.Threads

import scala.concurrent.CanAwait
import scala.util.Random


/**
 * Scheduler
 * Created by debop on 2014. 4. 16.
 */
trait Scheduler {

  /**
   * Schedule `r` to be run at some time in the future
   */
  def submit(r: Runnable)

  /**
   * Flush the schedule. Return when there is no more work to do.
   */
  def flush()

  /** The amount of User time that's been scheduled as per ThreadMXBean. */
  def usrTime: Long

  /** The amount of CPU time that's been scheduled as per ThreadMXBean. */
  def cpuTime: Long

  /** Number of dispatches performed by this scheduler. */
  def numDispatches: Long

  /** The permit may be removed in the future */
  def blocking[T](f: => T)(implicit perm: CanAwait): T
}

/**
 * A global scheduler.
 */
object Scheduler extends Scheduler {

  @volatile private var self: Scheduler = new LocalScheduler

  def apply(): Scheduler = self

  // Note: This can be unsafe since some schedulers may be active,
  // and flush() can be invoked on the wrong scheduler.
  //
  // This can happen, for example, if a LocalScheduler is used while
  // a future is resolved via Await.
  def setUnsafe(sched: Scheduler) = synchronized {
    self = sched
  }

  def submit(r: Runnable) = self.submit(r)
  def flush() = self.flush()
  def usrTime = self.usrTime
  def cpuTime = self.cpuTime
  def numDispatches = self.numDispatches

  def blocking[T](f: => T)(implicit perm: CanAwait) = self.blocking(f)
}

private class LocalScheduler extends Scheduler {
  private[this] val SampleScale = 1000
  private[this] val bean = ManagementFactory.getThreadMXBean
  private[this] val cpuTimeSupported = bean.isCurrentThreadCpuTimeSupported

  @volatile private[this] var activations = Set[Activation]()

  private[this] val local = new ThreadLocal[Activation] {
    override def initialValue = null
  }

  private class Activation extends Scheduler {
    private[this] var r0, r1, r2: Runnable = null
    private[this] val rs = new util.ArrayDeque[Runnable]
    private[this] var running = false
    private[this] val rnd = new Random(System.currentTimeMillis())

    // This is safe: there's only one updater
    @volatile var usrTime = 0L
    @volatile var cpuTime = 0L
    @volatile var numDispatches = 0L

    def submit(r: Runnable) {
      require(r != null)
      if (r0 == null) r0 = r
      else if (r1 == null) r1 = r
      else if (r2 == null) r2 = r
      else rs.addLast(r)

      if (!running) {
        if (cpuTimeSupported && rnd.nextInt(SampleScale) == 0) {
          numDispatches += SampleScale
          val cpu0 = bean.getCurrentThreadCpuTime
          val usr0 = bean.getCurrentThreadUserTime
          run()
          cpuTime += (bean.getCurrentThreadCpuTime - cpu0) * SampleScale
          usrTime += (bean.getCurrentThreadUserTime - usr0) * SampleScale
        } else {
          run()
        }
      }
    }

    def flush() {
      if (running) run()
    }

    private[this] def run() {
      val save = running
      running = true

      // via moderately silly benchmarking,
      // the queue unrolling gives us a ~50% speedup over pure Queue usage for common situation
      try {
        while (r0 != null) {
          val r = r0
          r0 = r1
          r1 = r2
          r2 = if (rs.isEmpty) null else rs.removeFirst()
          r.run()
        }
      } finally {
        running = save
      }
    }

    def blocking[T](f: => T)(implicit perm: CanAwait): T = f
  }

  private[this] def get(): Activation = {
    val a = local.get()
    if (a != null) return a

    local.set(new Activation)
    synchronized { activations += local.get() }
    local.get()
  }

  // Scheduler implementation:
  def submit(r: Runnable) = get().submit(r)
  def flush() = get().flush()

  def usrTime = (activations.iterator map (_.usrTime)).sum
  def cpuTime = (activations.iterator map (_.cpuTime)).sum
  def numDispatches = (activations.iterator map (_.numDispatches)).sum

  def blocking[T](f: => T)(implicit perm: CanAwait) = f
}

trait ExecutorScheduler {
  self: Scheduler =>
  val name: String
  val executorFactory: ThreadFactory => ExecutorService

  protected[this] val bean = ManagementFactory.getThreadMXBean
  protected val threadGroup: ThreadGroup = new ThreadGroup(name)
  @volatile private[this] var _threads = Set[Thread]()

  protected val threadFactory = new ThreadFactory {
    private val n = new AtomicInteger(1)
    override def newThread(r: Runnable): Thread = {
      val thread = new Thread(threadGroup, r, name + "-" + n.getAndIncrement)
      thread.setDaemon(true)
      thread
    }
  }

  protected def threads() = {
    // We add 2x slop here because it's inherently racy to enumerate threads.
    // Since this is used only for monitoring purposes, we don't try too hard.
    val threadArray = new Array[Thread](threadGroup.activeCount() * 2)
    val n = threadGroup.enumerate(threadArray)
    threadArray take n
  }

  protected[this] val executor = executorFactory(threadFactory)

  def shutdown() { executor.shutdown() }
  def submit(r: Runnable) { executor.execute(r) }
  def flush() = ()
  def usrTime = {
    threads().map(t => bean.getThreadUserTime(t.getId)).filter(_ > 0).sum
  }
  def cpuTime = {
    threads().map(t => bean.getThreadCpuTime(t.getId)).filter(_ > 0).sum
  }

  def numDispatches = -1L // Unsupported

  def getExecutor = executor

  def blocking[T](f: => T)(implicit perm: CanAwait) = f
}

/**
 * A scheduler that dispatches directly to an underlying Java
 * cached threadpool executor.
 */
class ThreadPoolScheduler(val name: String,
                          val executorFactory: ThreadFactory => ExecutorService)
  extends Scheduler with ExecutorScheduler {

  def this(name: String) = this(name, Executors.newCachedThreadPool)
}


/**
 * A scheduler that will bridge tasks from outside into the executor threads,
 * while keeping all local tasks on their local threads.
 * (Note: This scheduler is expecting an executor with unbounded capacity, not
 * expecting any RejectedExecutionException's other than the ones caused by
 * shutting down)
 */
class BridgeThreadPoolScheduler(val name: String,
                                val executorFactory: ThreadFactory => ExecutorService)
  extends Scheduler with ExecutorScheduler {
  private[this] val local = new LocalScheduler()

  def this(name: String) = this(name, Executors.newCachedThreadPool)

  override def submit(r: Runnable) {
    if (Thread.currentThread().getThreadGroup == threadGroup) {
      local.submit(r)
    } else {
      try {
        executor.execute {
          Threads.makeRunnable {
            BridgeThreadPoolScheduler.this.submit(r)
          }
        }
      } catch {
        case _: RejectedExecutionException => local.submit(r)
      }
    }
  }
}
