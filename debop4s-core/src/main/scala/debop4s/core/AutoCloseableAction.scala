package debop4s.core

import java.util.concurrent.atomic.AtomicBoolean

import org.slf4j.LoggerFactory

/**
 * JDK 7 의 try 리소스 정리 작업을 수행할 수 있도록 해주는 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:57
 */
class AutoCloseableAction(val closingAction: Runnable) extends AutoCloseable {

  def this() = this(null)

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  private[this] val _closed = new AtomicBoolean(false)

  def isClosed: Boolean = _closed.get()

  def close(): Unit = synchronized {
    if (!_closed.get) {
      try {
        if (closingAction != null)
          closingAction.run()
      } catch {
        case e: Throwable => log.warn("AutoClosable의 close 작업 시 예외가 발생했습니다.", e)
      } finally {
        _closed.compareAndSet(false, true)
      }
    }
  }
}
