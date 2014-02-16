package kr.debop4s.core

import org.slf4j.LoggerFactory

/**
 * JDK 7 의 try 리소스 정리 작업을 수행할 수 있도록 해주는 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:57
 */
class AutoCloseableAction(val actionWhenClosing: Runnable) extends AutoCloseable {

  def this() {
    this(null)
  }

  lazy val log = LoggerFactory.getLogger(getClass)

  private var closed = false

  def isClosed = closed

  def close() {
    if (!closed) {
      try {
        if (actionWhenClosing != null)
          actionWhenClosing.run()
      } catch {
        case e: Throwable => log.warn("AutoClosable의 close 작업 시 예외가 발생했습니다.", e)
      } finally {
        closed = true
      }
    }
  }
}
