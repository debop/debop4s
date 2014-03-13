package com.github.debop4s.core.testing

import java.util.concurrent.Callable
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.core.testing.Testing
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 5:32
 */
object Testing {

    lazy val log = LoggerFactory.getLogger(getClass)

    def run(count: Int, runnable: Runnable) {
        log.trace(s"멀티스레드로 지정한 코드를 $count 번 수행합니다.")
        try {
            Range(0, count).par.foreach { x =>
                runnable.run()
            }
        } catch {
            case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
            case e: Exception => log.error("작업 중 예외가 발생했습니다.", e)
        }
        log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
    }

    def run(count: Int)(block: => Unit) {
        log.trace(s"멀티스레드로 지정한 코드를 $count 번 수행합니다.")
        try {
            Range(0, count).par.foreach { x =>
                block
            }
        } catch {
            case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
            case e: Exception => log.error("작업 중 예외가 발생했습니다.", e)
        }
        log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
    }

    def runAction(count: Int)(block: Int => Unit) {
        log.trace(s"멀티스레드로 지정한 코드를 $count 번 수행합니다.")
        try {
            Range(0, count).par.foreach { x =>
                block(x)
            }
        } catch {
            case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
            case e: Exception => log.error("작업 중 예외가 발생했습니다.", e)
        }
        log.debug(s"멀티스레드로 지정한 코드를 $count 번 수행했습니다.")
    }

    def call[T](count: Int)(callable: Callable[T]): List[T] = {
        log.trace(s"멀티스레드로 지정한 코드를 $count 번 수행합니다.")
        try {
            return Range(0, count).par.map(x => callable.call()).toList
        } catch {
            case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
            case e: Exception => log.error("작업 중 예외가 발생했습니다.", e)
        }
        List()
    }

    def runFunc[T](count: Int)(func: Int => T): List[T] = {
        log.trace(s"멀티스레드로 지정한 코드를 $count 번 수행합니다.")
        try {
            return Range(0, count).par.map(x => func(x)).toList
        } catch {
            case e: InterruptedException => log.warn("작업 중 interrupted 되었습니다.")
            case e: Exception => log.error("작업 중 예외가 발생했습니다.", e)
        }
        List()
    }
}
