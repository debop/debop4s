package debop4s.core.utils

import debop4s.core._
import org.slf4j.LoggerFactory


/**
 * debop4s.core.tools.With
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:33
 */
object With {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  def stopwatch(action: => Unit): Unit = {
    val sw = new ClosableStopwatch()
    try {
      action
    } finally {
      sw.close()
    }
  }

  def stopwatch(msg: String)(action: => Unit): Unit = {
    using(new ClosableStopwatch("msg")) { sw =>
      action
    }
  }

  def tryAction(action: => Unit)(catchAction: => Unit = ())(finallyAction: => Unit = ()): Unit = {
    try {
      action
    } catch {
      case e: Throwable =>
        log.warn("action 수행에 예외가 발생했습니다.", e)
        catchAction
    } finally {
      finallyAction
    }
  }

  def tryFunction[@miniboxed T](func: () => T)(catchAction: => Unit = ())(finallyAction: => Unit = ()): T = {
    try {
      func()
    } catch {
      case e: Throwable =>
        log.warn("funct 수행에 예외가 발생했습니다.", e)
        catchAction
        null.asInstanceOf[T]
    } finally {
      finallyAction
    }
  }
}
