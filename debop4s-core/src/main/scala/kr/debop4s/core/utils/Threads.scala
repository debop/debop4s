package kr.debop4s.core.utils

import java.util.concurrent.Callable
import kr.debop4s.core.logging.Logger

/**
 * kr.debop4s.core.tools.Threads
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오후 5:24
 */
object Threads {

    lazy val log = Logger(this.getClass)

    implicit def makeRunnable(action: => Unit): Runnable = {
        new Runnable {
            def run() {
                action
            }
        }
    }

    implicit def makeCallable[T](function: => T): Callable[T] = {
        new Callable[T] {
            def call() = function
        }
    }

}
