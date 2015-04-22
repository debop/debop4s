package debop4s.core.testing

import java.util.concurrent.Callable

import debop4s.core.Action1
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * debop4s.core.testing.Testing
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 5:32
 */
object Testing {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  def run(count: Int, runnable: Runnable): Unit = {
    try {
      Range(0, count).par.foreach(_ => runnable.run())
    } catch {
      case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
      case NonFatal(e) => log.error("작업 중 예외가 발생했습니다.", e)
    }
    log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
  }

  def run(count: Int)(block: => Unit): Unit = {
    try {
      Range(0, count).par.foreach(_ => block)
    } catch {
      case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
      case NonFatal(e) => log.error("작업 중 예외가 발생했습니다.", e)
    }
    log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
  }

  def run(count: Int, action1: Action1[java.lang.Integer]): Unit = {
    try {
      Range(0, count).par.foreach(x => action1.perform(x))
    } catch {
      case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
      case NonFatal(e) => log.error("작업 중 예외가 발생했습니다.", e)
    }
    log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
  }

  def runAction(count: Int)(block: Int => Unit): Unit = {
    try {
      Range(0, count).par.foreach(block)
    } catch {
      case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
      case NonFatal(e) => log.error("작업 중 예외가 발생했습니다.", e)
    }
    log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
  }

  def runAction(count: Int, action1: Action1[java.lang.Integer]): Unit = {
    try {
      Range(0, count).par.foreach(_ => action1.perform(_))
    } catch {
      case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
      case NonFatal(e) => log.error("작업 중 예외가 발생했습니다.", e)
    }
    log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
  }

  def call[T](count: Int)(callable: Callable[T]): Seq[T] = {
    try {
      return Range(0, count).par.map(_ => callable.call()).toList
    } catch {
      case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
      case NonFatal(e) => log.error("작업 중 예외가 발생했습니다.", e)
    }
    Seq.empty[T]
  }

  def runFunc[T](count: Int)(func: Int => T): Seq[T] = {
    try {
      return Range(0, count).par.map(x => func(x)).toList
    } catch {
      case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
      case NonFatal(e) => log.error("작업 중 예외가 발생했습니다.", e)
    }
    Seq.empty[T]
  }
}
