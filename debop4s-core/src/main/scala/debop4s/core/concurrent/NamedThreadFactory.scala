package debop4s.core.concurrent

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 이름을 가진 Thread를 생성해주는 factory 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 2:01
 */
class NamedThreadFactory(val prefix: Option[String] = None) extends ThreadFactory {
  val name: String = prefix.getOrElse("thread")
  val group: ThreadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup, name)
  val threadNumber: AtomicInteger = new AtomicInteger(1)

  def newThread(r: Runnable): Thread = {
    val threadName = name + "-" + threadNumber.getAndIncrement
    val thread = new Thread(group, r, threadName)

    if (thread.getPriority != Thread.NORM_PRIORITY)
      thread.setPriority(Thread.NORM_PRIORITY)

    thread
  }
}

object NamedThreadFactory {

  // private lazy val threadFactory: NamedThreadFactory = new NamedThreadFactory()

  def apply(): NamedThreadFactory = new NamedThreadFactory()

  def apply(prefix: String): NamedThreadFactory = new NamedThreadFactory(Some(prefix))

}
