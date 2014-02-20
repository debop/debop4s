package com.github.debop4s.core.parallels

import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicLong
import org.slf4j.LoggerFactory

/**
 * 실행 시각 제한이 있는 메소드를 수행하도록 해준다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 2:04
 */
class TimedExecutor(val timeout: Long, val checkMillis: Option[Long] = None) {

    lazy val log = LoggerFactory.getLogger(getClass)

    def this(timeout: Long) {
        this(timeout, None)
    }

    def execute(executable: Executable) {
        if (executable == null)
            return

        val adapter = new ExecutableAdapter(executable)
        val separatedThread = new Thread(adapter)

        separatedThread.start()

        val runningTime = new AtomicLong(0)
        do {
            if (runningTime.get() > timeout) {
                try {
                    executable.timeout()
                } catch {
                    case _: Throwable =>
                }
                throw new TimeoutException()
            }
            try {
                Thread.sleep(checkMillis.getOrElse(100L))
                runningTime.addAndGet(checkMillis.getOrElse(100L))
            } catch {
                case _: Throwable =>
            }
        } while (!adapter.isDone)

        adapter.throwAnyErrors()
        log.debug(s"제한된 시각[$timeout](msecs) 동안 지정된 executable 인스턴스를 실행했습니다.")
    }

}
