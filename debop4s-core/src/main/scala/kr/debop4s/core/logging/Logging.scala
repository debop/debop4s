package kr.debop4s.core.logging

import org.slf4j.Marker

/**
 * kr.debop4s.core.logging.Logging
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 9:37
 */
trait Logging {

    lazy val log = Logger[Logging]

    def loggerName = log.name

    protected def trace(msg: => Any) { log.trace(msg) }
    protected def trace(msg: => Any, t: Throwable) { log.trace(msg, t) }
    protected def trace(marker: Marker, msg: => Any) { log.trace(marker, msg) }
    protected def trace(marker: Marker, msg: => Any, t: Throwable) { log.trace(marker, msg, t) }

    protected def debug(msg: => Any) { log.debug(msg) }
    protected def debug(msg: => Any, t: Throwable) { log.debug(msg, t) }
    protected def debug(marker: Marker, msg: => Any) { log.debug(marker, msg) }
    protected def debug(marker: Marker, msg: => Any, t: Throwable) { log.debug(marker, msg, t) }

    protected def info(msg: => Any) { log.info(msg) }
    protected def info(msg: => Any, t: Throwable) { log.info(msg, t) }
    protected def info(marker: Marker, msg: => Any) { log.info(marker, msg) }
    protected def info(marker: Marker, msg: => Any, t: Throwable) { log.info(marker, msg, t) }

    protected def warn(msg: => Any) { log.warn(msg) }
    protected def warn(msg: => Any, t: Throwable) { log.warn(msg, t) }
    protected def warn(marker: Marker, msg: => Any) { log.warn(marker, msg) }
    protected def warn(marker: Marker, msg: => Any, t: Throwable) { log.warn(marker, msg, t) }

    protected def error(msg: => Any) { log.error(msg) }
    protected def error(msg: => Any, t: Throwable) { log.error(msg, t) }
    protected def error(marker: Marker, msg: => Any) { log.error(marker, msg) }
    protected def error(marker: Marker, msg: => Any, t: Throwable) { log.error(marker, msg, t) }
}
