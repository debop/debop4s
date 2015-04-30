package debop4s.core

/**
 * 아직 구현되지 않은 함수를 호출할 때 발생하는 예외입니다.
 * @author Sunghyouk Bae
 */
class NotImplementedException(msg: String = null, cause: Throwable = null)
  extends RuntimeException(msg, cause)