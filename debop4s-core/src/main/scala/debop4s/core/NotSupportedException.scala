package debop4s.core

/**
 * debop4s.core.NotSupportedException
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오전 11:53
 */
class NotSupportedException(msg: String = null, cause: Throwable = null)
  extends RuntimeException(msg, cause)