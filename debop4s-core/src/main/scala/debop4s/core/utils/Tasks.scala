package debop4s.core.utils

import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.LoggerFactory

import scala.util.Try
import scala.util.control.NonFatal

/**
 * debop4s.core.tools.Tasks
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 7:59
 */
object Tasks {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  /** Thread Id */
  private[this] val sequenceId: AtomicInteger = new AtomicInteger(0)

  /**
   * 지정한 action을 수행할 background thread 를 생성하여 실행합니다.
   *
   * @param action background thread에서 실행할 코드 블럭
   */
  def apply(action: Runnable): Thread =
    spawn("backgraound-" + sequenceId.incrementAndGet, action)

  /**
   * 지정된 action을 새로운 스레드를 만들어 실행하고, 스레드를 반환합니다.
   *
   * @param threadName thread name
   * @param daemon     is background thread?
   * @param action     background thread에서 실행할 코드 블럭
   * @return thread
   */
  def spawn(threadName: String, action: Runnable, daemon: Boolean = false): Thread = {
    require(action != null)
    val thread: Thread = new Thread(action, threadName)
    thread.setDaemon(daemon)
    thread.start()
    thread
  }

  /**
   * 지정된 `block`을 성공할 때까지 `attempts` 만큼 시도합니다.
   * @param attempts 시도 횟수
   * @param waitTime 실패 시 재시도하기 전에 idle time in millis
   */
  @deprecated("제대로 작동하지 않네요. 더 테스트해야 합니다.", "2.0.0")
  def runWithRetry(attempts: Int, waitTime: Long = 1)(block: => Unit) {
    var remains = attempts
    while (remains > 0) {
      try {
        block
        return
      } catch {
        case NonFatal(e) =>
          if (remains <= 1) throw new RuntimeException(e)
          Thread.sleep(waitTime)
      }
      remains -= 1
    }
  }

  /**
   * 지정된 `func` 을 성공할 때까지 `attempts` 회 만큼 시도합니다.
   * 실패 시에는
   * @param attempts 시도 횟수
   * @param waitTime 실패 시 재시도하기 전에 idle time in millis
   */
  @deprecated("제대로 작동하지 않네요. 더 테스트해야 합니다.", "2.0.0")
  def callWithRetry[@miniboxed T](attempts: Int, waitTime: Long = 1)(func: => T): Try[T] = Try {
    var remains = attempts
    var result: T = null.asInstanceOf[T]
    while (remains > 0) {
      log.debug(s"run function... remains=$remains")
      try {
        result = func
      } catch {
        case e: Throwable => log.warn(s"fail to execution method.", e)
      }
      remains -= 1
    }
    result
  }

}
