package com.github.debop4s.core.utils

import org.slf4j.LoggerFactory


/**
 * com.github.debop4s.core.tools.With
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:33
 */
object With {

    private lazy val log = LoggerFactory.getLogger(getClass)

    /**
     * C# 의 using 과 유사하게 close 메소드를 가진 인스턴스에 작업 후 close 를 호출 하도록 합니다.
     */
    def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B = {
        try {
            f(resource)
        } finally {
            resource.close()
        }
    }

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
