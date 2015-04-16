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

  private lazy val log = LoggerFactory.getLogger(getClass)

  def this() = this("", false)
  def this(msg: String) = this(msg, false)

  val NANO_TO_MILLISECONDS = 1000000.0
  var startTime: Long = 0
  var endTime: Long = 0
  var elapsedTime: Long = 0

  def getElapsedTime = elapsedTime

  private def cleanUp() {
    System.gc()
  }

  def reset() {
    startTime = 0
    endTime = 0
    elapsedTime = 0
  }

  def start() {
    if (startTime != 0) reset()
    if (this.runGC) cleanUp()
    startTime = System.nanoTime()
    log.trace(s"start stopwatch at $startTime")
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

  override def toString: String = s"$msg elapsed time=[${ nanoToMillis(elapsedTime) }] msecs."

  private def nanoToMillis(nano: Double): Double = nano / NANO_TO_MILLISECONDS
}

/**
 * debop4s.core.tools.ClosableStopwatch
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:54
 */
class ClosableStopwatch(msg: String = "", runGC: Boolean = false) extends Stopwatch(msg, runGC) with AutoCloseable {

  start()

  def close() {
    stop()
  }
}

