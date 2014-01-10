package kr.debop4s.core.utils

import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.tools.Tasks
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 7:59
 */
object Tasks {

    implicit lazy val log = LoggerFactory.getLogger(getClass)

    /**
     * 지정된 block을 성공할 때까지 수행햅니다.
     */
    def runWithRetry(attempts: Int, waitTime: Long = 1)(block: => Unit) {
        var remains = attempts
        while (remains > 0) {
            try {
                block
                return
            } catch {
                case e: Throwable =>
                    if (remains == 1) throw new RuntimeException(e)
                    Thread.sleep(waitTime)
            }
            remains -= 1
        }
    }

    /**
     * 지정된 func을 성공할 때까지 수행햅니다.
     */
    def callWithRetry[T](attempts: Int, waitTime: Long = 1)(func: () => T): T = {
        var remains = attempts
        while (remains > 0) {
            try {
                return func()
            } catch {
                case e: Throwable =>
                    if (remains == 1) throw new RuntimeException(e)
                    Thread.sleep(waitTime)
            }
            remains -= 1
        }
        null.asInstanceOf[T]
    }

}
