package debop4s.core.parallels

import debop4s.core.utils.Threads
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 이름을 가진 Thread를 생성해주는 factory 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 2:01
 */
class NamedThreadFactory(val prefix: Option[String] = None) extends ThreadFactory {

    val threadNumber = new AtomicInteger(1)

    def newThread(r: Runnable): Thread = {
        assert(r != null)
        val threadName = prefix.getOrElse("thread") + "_" + threadNumber.getAndIncrement.toString
        new Thread(r, threadName)
    }
}

object NamedThreadFactory {

    private val threadNumber = new AtomicInteger(1)

    def createThread(r: Runnable, prefix: Option[String] = None): Thread = {
        require(r != null)
        val threadName = prefix.getOrElse("thread") + "_" + threadNumber.getAndIncrement.toString
        new Thread(r, threadName)
    }

    def create(prefix: Option[String] = None)(block: => Unit): Thread = {
        createThread(Threads.makeRunnable(block), prefix)
    }

}
