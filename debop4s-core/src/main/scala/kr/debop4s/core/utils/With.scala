package kr.debop4s.core.utils

import kr.debop4s.core.logging.Logging

/**
 * kr.debop4s.core.tools.With
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:33
 */
object With extends Logging {

    def stopwatch(action: => Unit) {
        val sw = new ClosableStopwatch()
        try {
            action
        } finally {
            sw.close()
        }
    }

    def stopwatch(msg: String)(action: => Unit) {
        val sw = new ClosableStopwatch("msg")
        try {
            action
        } finally {
            sw.close()
        }
    }

    def tryAction(action: => Unit)(catchAction: => Unit = ())(finallyAction: => Unit = ()) {
        try {
            action
        } catch {
            case e: Throwable =>
                log.warn("action 수행에 예외가 발생했습니다.", e)
                catchAction
        } finally {
            finallyAction
        }
    }

    def tryFunction[T](func: () => T)(catchAction: => Unit = ())(finallyAction: => Unit = ()): T = {
        try {
            func()
        } catch {
            case e: Throwable =>
                log.warn("funct 수행에 예외가 발생했습니다.", e)
                catchAction
                null.asInstanceOf[T]
        } finally {
            finallyAction
        }
    }
}
