package debop4s.core.utils

import debop4s.core._
import debop4s.core.concurrent.NamedPoolThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import scala.Some
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try
import scala.util.control.NonFatal


trait TimerTask extends Closable {
  def cancel()
  override def close(deadline: Time) = Future(cancel())
}

trait Timer {
  def schedule(when: Time)(block: => Unit): TimerTask
  def schedule(when: Time, period: Duration)(block: => Unit): TimerTask

  def schedule(period: Duration)(block: => Unit): TimerTask = {
    schedule(period.fromNow, period)(block)
  }

  /**
  *  지정한 지연 시간 후에 작업을 수행합니다.
  */
  def doLater[A](delay: Duration)(func: => A): scala.concurrent.Future[A] = {
    doAt(Time.now + delay)(func)
  }

  /**
  *  특정 시간에 주어진 작업을 수행합니다.
  */
  def doAt[A](time: Time)(func: => A): scala.concurrent.Future[A] = {
    val pending = new AtomicBoolean(true)
    val p = promise[A]()

    val task = schedule(time) {
      if (pending.compareAndSet(true, false)) {
        p complete Try(func)
      }
    }
    p.future onFailure {
      case cause =>
        if (pending.compareAndSet(true, false))
          task.cancel()
    }
    p.future
  }

  def stop()
}

object Timer {
  val Nil: Timer = new NullTimer()
}

/**
* NullTimer 는 모든 Task 에 대해 즉시 호출하고 끝냅니다.
*/
class NullTimer extends Timer {
  override def schedule(when: Time)(block: => Unit): TimerTask = {
    block
    NullTimerTask
  }
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    block
    NullTimerTask
  }
  override def stop() = {}
}

object NullTimerTask extends TimerTask {
  def cancel() {}
}

class ThreadStoppingTimer(underlying: Timer, executor: java.util.concurrent.ExecutorService) extends Timer {
  override def schedule(when: Time)(block: => Unit): TimerTask = {
    underlying.schedule(when)(block)
  }
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    underlying.schedule(when, period)(block)
  }
  override def stop(): Unit = {

    executor.submit(Threads.makeRunnable { underlying.stop() })
  }
}

trait ReferenceCountedTimer extends Timer {
  def acquire()
}

class ReferenceCountingTimer(factory: () => Timer) extends ReferenceCountedTimer {
  private[this] var refcount = 0
  private[this] var underlying = null: Timer

  override def acquire() = synchronized {
    refcount += 1
    if (refcount == 1) {
      require(underlying == null)
      underlying = factory()
    }
  }
  override def stop() = synchronized {
    refcount -= 1
    if (refcount == 0) {
      underlying.stop()
      underlying = null
    }
  }
  override def schedule(when: Time)(block: => Unit): TimerTask = {
    require(underlying != null)
    underlying.schedule(when)(block)
  }
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    require(underlying != null)
    underlying.schedule(when, period)(block)
  }
}

class JavaTimer(isDaemon: Boolean) extends Timer {

  def this() = this(false)

  private[this] val underlying = new java.util.Timer(isDaemon)

  override def schedule(when: Time)(block: => Unit): TimerTask = {
    val task = toJavaTimerTask(block)
    underlying.schedule(task, when.toDate)
    toTimerTask(task)
  }
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    val task = toJavaTimerTask(block)
    underlying.schedule(task, when.toDate, period.toMillis)
    toTimerTask(task)
  }

  override def stop() = underlying.cancel()


  def logError(t: Throwable) {
    System.err.println(s"WARNING: JavaTimer 에서 작업 실행 시 예외가 발생했습니다. $t")
  }
  private def toJavaTimerTask(block: => Unit) = new java.util.TimerTask {
    def run() {
      try {
        block
      } catch {
        case NonFatal(t) => logError(t)
        case fatal: Throwable =>
          logError(fatal)
          throw fatal
      }
    }
  }
  private[this] def toTimerTask(task: java.util.TimerTask) = new TimerTask {
    def cancel() { task.cancel() }
  }
}

class ScheduledThreadPoolTimer(poolSize: Int,
                               threadFactory: java.util.concurrent.ThreadFactory,
                               rejectedExecutionHandler: Option[java.util.concurrent.RejectedExecutionHandler]) extends Timer {

  def this(poolSize: Int, threadFactory: java.util.concurrent.ThreadFactory) =
    this(poolSize, threadFactory, None)
  def this(poolSize: Int, threadFactory: java.util.concurrent.ThreadFactory, handler: java.util.concurrent.RejectedExecutionHandler) =
    this(poolSize, threadFactory, Some(handler))

  /** Construct a ScheduledThreadPoolTimer with a NamedPoolThreadFactory. */
  def this(poolSize: Int = 2, name: String = "timer", makeDaemons: Boolean = false) =
    this(poolSize, NamedPoolThreadFactory(name, makeDaemons), None)

  private[this] val underlying: java.util.concurrent.ScheduledThreadPoolExecutor = {
    rejectedExecutionHandler match {
      case None =>
        new java.util.concurrent.ScheduledThreadPoolExecutor(poolSize, threadFactory)
      case Some(handler: java.util.concurrent.RejectedExecutionHandler) =>
        new java.util.concurrent.ScheduledThreadPoolExecutor(poolSize, threadFactory, handler)
    }
  }

  override def schedule(when: Time)(block: => Unit): TimerTask = {
    val runnable = Threads.makeRunnable { block }
    val javaFuture = underlying.schedule(runnable, when.sinceNow.toMillis, TimeUnit.MILLISECONDS)
    new TimerTask {
      def cancel() {
        javaFuture.cancel(true)
        underlying.remove(runnable)
      }
    }
  }
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    schedule(when.sinceNow, period)(block)
  }
  def schedule(wait: Duration, period: Duration)(block: => Unit): TimerTask = {
    val runnable = Threads.makeRunnable { block }
    val javaFuture = underlying.scheduleAtFixedRate(runnable, wait.toMillis, period.toMillis, TimeUnit.MILLISECONDS)
    new TimerTask {
      def cancel() {
        javaFuture.cancel(true)
        underlying.remove(runnable)
      }
    }
  }
  override def stop(): Unit = underlying.shutdown()
}

/**
* 테스트용 Timer
*/
class MockTimer extends Timer {

  // These are weird semantics admittedly, but there may
  // be a bunch of tests that rely on them already.
  case class Task(var when: Time, func: () => Unit) extends TimerTask {
    var isCancelled = false
    def cancel() {
      isCancelled = true
      nCancelled += 1
      when = Time.now
      tick()
    }
  }

  var isStopped = false
  var tasks = ArrayBuffer[Task]()
  var nCancelled = 0

  def tick() {
    if (isStopped)
      throw new IllegalStateException("timer is stopped already.")

    val now = Time.now
    val (toRun, toQueue) = tasks.partition(task => task.when <= now)
    tasks = toQueue
    toRun filter { !_.isCancelled } foreach { _.func() }
  }


  override def schedule(when: Time)(block: => Unit): TimerTask = {
    val task = Task(when, () => block)
    tasks += task
    task
  }
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    def runAndReschedule() {
      schedule(Time.now + period) { runAndReschedule() }
      block
    }
    schedule(when) { runAndReschedule() }
  }
  override def stop(): Unit = { isStopped = true }
}