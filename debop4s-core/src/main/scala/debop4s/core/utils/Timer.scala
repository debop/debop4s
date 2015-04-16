package debop4s.core.utils

import java.util.concurrent
import java.util.concurrent.atomic.AtomicBoolean

import debop4s.core._
import debop4s.core.concurrent.NamedPoolThreadFactory
import debop4s.core.conversions.time._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try
import scala.util.control.NonFatal

/**
 *
 */
trait TimerTask extends Closable {

  /**
   * Timer 작업을 취소합니다.
   */
  def cancel()

  /**
   * 지정한 작업 완료 시간에 작업을 종료합니다.
   * @param deadline
   * @return
   */
  override def close(deadline: Time): Future[Unit] = Future {
    cancel()
  }
}

/**
 * 주기적으로 작업을 수행할 수 있도록 하는 Timer 입니다.
 */
trait Timer extends Logging {

  /**
   * 특정 시각에 `block` 을 실행시킵니다.
   * @param when  시작 시각
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  def schedule(when: Time)(block: => Unit): TimerTask

  /**
   * 정기적으로 `block` 을 실행시킵니다.
   * @param when  시작 시각
   * @param period 반복 주기
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  def schedule(when: Time, period: Duration)(block: => Unit): TimerTask

  /**
   * 정기적으로 `block` 을 실행시킵니다.
   * @param period 반복 주기
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  def schedule(period: Duration)(block: => Unit): TimerTask =
    schedule(period.fromNow, period)(block)

  /**
   * 지정한 지연 시간 후에 `func` 을 수행합니다.
   */
  def doLater[A](delay: Duration)(func: => A): scala.concurrent.Future[A] = {
    log.trace(s"현재시각으로부터 delay=$delay 이후에 작업을 수행합니다.")
    doAt(Time.now + delay)(func)
  }

  /**
   * 특정 시간에 `func` 을 수행합니다.
   */
  def doAt[A](time: Time)(func: => A): scala.concurrent.Future[A] = {
    log.trace(s"지정된 시각 $time 에 작업을 시작하도록 예약 합니다.")

    val pending = new AtomicBoolean(true)
    val p = Promise[A]()

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

  /**
   * Timer 를 중단합니다.
   */
  def stop()
}

object Timer {
  val Nil: Timer = new NullTimer()
}

object NullTimerTask extends TimerTask {
  def cancel() {}
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

object ThreadStoppingTimer {

  implicit val executor = new concurrent.ForkJoinPool(2)

  def apply(underlying: Timer)(implicit executor: java.util.concurrent.ExecutorService): ThreadStoppingTimer =
    new ThreadStoppingTimer(underlying, executor)
}

class ThreadStoppingTimer(underlying: Timer, executor: concurrent.ExecutorService) extends Timer {

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

object ReferenceCountingTimer {

  def apply(factory: () => Timer): ReferenceCountingTimer =
    new ReferenceCountingTimer(factory)
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

/**
 * companion object for [[JavaTimer]]
 */
object JavaTimer {
  def apply(isDaemon: Boolean = false): JavaTimer = new JavaTimer(isDaemon)
}

/**
 * `java.util.Timer` 를 사용하는 Timer 입니다.
 */
class JavaTimer(isDaemon: Boolean) extends Timer {

  def this() = this(false)

  private[this] val underlying = new java.util.Timer(isDaemon)

  /**
   * 특정 시각에 `block` 을 실행합니다.
   * @param when  시작 시각
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  override def schedule(when: Time)(block: => Unit): TimerTask = {
    val task = toJavaTimerTask(block)
    underlying.schedule(task, when.toDate)
    toTimerTask(task)
  }

  /**
   * 특정 시각부터 `block`을 반복적으로 수행합니다.
   * @param when  시작 시각
   * @param period 반복 주기
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    val task = toJavaTimerTask(block)
    underlying.schedule(task, when.toDate, period.toMillis)
    toTimerTask(task)
  }

  override def stop() = underlying.cancel()

  def logError(t: Throwable) {
    error(s"WARNING: JavaTimer 에서 작업 실행 시 예외가 발생했습니다. $t")
    t.printStackTrace(System.err)
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

/**
 * companion object of [[ScheduledThreadPoolTimer]]
 */
object ScheduledThreadPoolTimer {

  def apply(poolSize: Int = 2,
            name: String = "timer",
            makeDaemons: Boolean = false): ScheduledThreadPoolTimer = {
    new ScheduledThreadPoolTimer(poolSize, name, makeDaemons)
  }
}

/**
 * `ScheduledThreadPool`을 이용하여 Timer 작업을 실행하는 Timer 입니다.
 * @param poolSize  thread pool 의 크기
 * @param threadFactory  thread factory
 * @param rejectedExecutionHandler 작업 거부 시 수행할 handler
 */
class ScheduledThreadPoolTimer(poolSize: Int,
                               threadFactory: java.util.concurrent.ThreadFactory,
                               rejectedExecutionHandler: Option[concurrent.RejectedExecutionHandler]) extends Timer {

  def this(poolSize: Int, threadFactory: concurrent.ThreadFactory) =
    this(poolSize, threadFactory, None)

  def this(poolSize: Int, threadFactory: concurrent.ThreadFactory, handler: concurrent.RejectedExecutionHandler) =
    this(poolSize, threadFactory, Some(handler))

  /** Construct a ScheduledThreadPoolTimer with a NamedPoolThreadFactory. */
  def this(poolSize: Int, name: String = "timer", makeDaemons: Boolean) =
    this(poolSize, NamedPoolThreadFactory(name, makeDaemons), None)

  private[this] val underlying: concurrent.ScheduledThreadPoolExecutor = {
    log.trace(s"ScheduledThreadPoolExecutor 를 생성합니다.")
    rejectedExecutionHandler match {
      case None =>
        new concurrent.ScheduledThreadPoolExecutor(poolSize, threadFactory)
      case Some(handler: concurrent.RejectedExecutionHandler) =>
        new concurrent.ScheduledThreadPoolExecutor(poolSize, threadFactory, handler)
    }
  }

  /**
   * 특정 시각에 `block` 을 실행시킵니다.
   * @param when  시작 시각
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  override def schedule(when: Time)(block: => Unit): TimerTask = {
    val runBlock = runnable { block }
    val javaFuture = underlying.schedule(runBlock,
      when.sinceNow.toMillis,
      concurrent.TimeUnit.MILLISECONDS)

    new TimerTask {
      def cancel() {
        javaFuture.cancel(true)
        underlying.remove(runBlock)
      }
    }
  }
  /**
   * 정기적으로 `block` 을 실행시킵니다.
   * @param when  시작 시각
   * @param period 반복 주기
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  override def schedule(when: Time, period: Duration)(block: => Unit): TimerTask = {
    schedule(when.sinceNow, period)(block)
  }
  /**
   * 정기적으로 `block` 을 실행시킵니다.
   * @param wait  초기 지연 시각
   * @param period 반복 주기
   * @param block  실행할 메소드 블럭
   * @return `TimerTask` instance
   */
  def schedule(wait: Duration, period: Duration)(block: => Unit): TimerTask = {
    val runblock = runnable { block }
    val javaFuture = underlying.scheduleAtFixedRate(runblock,
      wait.toMillis,
      period.toMillis,
      concurrent.TimeUnit.MILLISECONDS)

    new TimerTask {
      def cancel() {
        javaFuture.cancel(true)
        underlying.remove(runblock)
      }
    }
  }
  override def stop(): Unit = underlying.shutdown()
}

object MockTimer {
  def apply(): MockTimer = new MockTimer()
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
    toRun.filter(!_.isCancelled).foreach(_.func())
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
    schedule(when) {
      runAndReschedule()
    }
  }

  override def stop(): Unit = {
    log.trace(s"Timer를 중단합니다.")
    isStopped = true
  }
}