package debop4s.core.utils

import org.slf4j.LoggerFactory


object Stopwatch {
  def apply(msg: String = "", runGc: Boolean = false): Stopwatch =
    new Stopwatch(msg, runGc)
}

/**
 * 코드 실행 시간을 측정하는 Stopwatch 입니다.
 * 내부적으로 nanotime 을 측정하고, milliseconds 단위로 표시합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:40
 */
class Stopwatch(val msg: String = "", val runGC: Boolean = false) {

  def this() = this("", false)
  def this(msg: String) = this(msg, false)

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  private[this] val NANO_TO_MILLISECONDS: Double = 1000000.0

  private[this] var startTime: Long = 0L
  private[this] var endTime: Long = 0L
  private[this] var elapsedTime: Long = 0L

  def getElapsedTime: Long = elapsedTime

  private def cleanUp() {
    System.gc()
  }

  def reset(): Unit = {
    startTime = 0L
    endTime = 0L
    elapsedTime = 0L
  }

  def start(): Unit = {
    if (startTime != 0) reset()
    if (this.runGC) cleanUp()
    startTime = System.nanoTime()
  }

  def stop(): Double = {
    if (startTime == 0)
      throw new IllegalStateException("call start() method at first.")

    if (endTime == 0) {
      endTime = System.nanoTime()
      elapsedTime = endTime - startTime
    }
    log.info(s"$msg elapsed time=[${ nanoToMillis(elapsedTime) }] ms.")
    nanoToMillis(elapsedTime)
  }

  override def toString: String =
    s"$msg elapsed time=[${ nanoToMillis(elapsedTime) }] msecs."

  private def nanoToMillis(nano: Double): Double =
    nano / NANO_TO_MILLISECONDS
}

/**
 * debop4s.core.tools.ClosableStopwatch
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:54
 */
class ClosableStopwatch(msg: String = "", runGC: Boolean = false)
  extends Stopwatch(msg, runGC) with AutoCloseable {

  start()

  def close(): Unit = stop()
}

