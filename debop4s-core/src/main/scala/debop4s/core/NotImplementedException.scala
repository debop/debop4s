package debop4s.core

/**
 * 아직 구현되지 않은 함수를 호출할 때 발생하는 예외입니다.
 * @author Sunghyouk Bae
 */
class NotImplementedException(msg: String, cause: Throwable)
  extends RuntimeException(msg, cause) {

  def this() = this(null, null)
  def this(msg: String) = this(msg, null)
  def this(cause: Throwable) = this(null, cause)
}