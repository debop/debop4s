package debop4s.core.utils

import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * `close` 메소드가 있는 객체에 대한 작업을 수행 한 후, `close` 메소드를 호출하도록 해주는 closer 입니다.
 * @author Sunghyouk Bae
 */
object Closer {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  /** `close` 메소드를 가진 객체에 대해 메소드 `func` 를 실행한 후 `close` 메소드를 호출합니다. */
  def using[@miniboxed A <: {def close() : Unit}, @miniboxed B](closable: A)(func: A => B): B = {
    require(closable != null)
    require(func != null)

    try {
      func(closable)
    } finally {
      try {
        closable.close()
      } catch {
        case NonFatal(e) => log.warn("close 메소드에서 예외가 발생했습니다.", e)
      }
    }
  }
}
