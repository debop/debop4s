package debop4s.core.utils

import javax.annotation.Nullable

import debop4s.core.Logging

import scala.util.control.NonFatal

/**
 * `close` 메소드가 있는 객체에 대한 작업을 수행 한 후, `close` 메소드를 호출하도록 해주는 closer 입니다.
 * @author Sunghyouk Bae
 */
object Closer extends Logging {

  /**
   * `close` 메소드를 가진 객체에 대해 메소드 `func` 를 실행한 후 `close` 메소드를 호출합니다.
   */
  def using[A <: {def close() : Unit}, B](@Nullable closable: A)(func: A => B): B = {
    require(closable != null)
    require(func != null)

    try {
      func(closable)
    } finally {
      try {
        trace(s"Closable 인스턴스의 close를 호출합니다.")
        closable close()
      } catch {
        case NonFatal(e) => warn("close 메소드에서 예외가 발생했습니다.", e)
      }
    }
  }
}
