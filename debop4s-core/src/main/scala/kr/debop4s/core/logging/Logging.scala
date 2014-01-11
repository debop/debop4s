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
    // @varargs protected def trace(format: => String, arguments: Any*) { log.trace(format, arguments) }
    protected def trace(marker: Marker, msg: => Any) { log.trace(marker, msg) }
    protected def trace(marker: Marker, msg: => Any, t: Throwable) { log.trace(marker, msg, t) }
    // @varargs protected def trace(marker: Marker, format: => String, arguments: Any*) { log.trace(marker, format, arguments) }

    protected def debug(msg: => Any) { log.debug(msg) }
    protected def debug(msg: => Any, t: Throwable) { log.debug(msg, t) }
    // @varargs protected def debug(format: => String, arguments: Any*) { log.debug(format, arguments) }
    protected def debug(marker: Marker, msg: => Any) { log.debug(marker, msg) }
    protected def debug(marker: Marker, msg: => Any, t: Throwable) { log.debug(marker, msg, t) }
    // @varargs protected def debug(marker: Marker, format: => String, arguments: Any*) { log.debug(marker, format, arguments) }

    protected def info(msg: => Any) { log.info(msg) }
    protected def info(msg: => Any, t: Throwable) { log.info(msg, t) }
    // @varargs protected def info(format: => String, arguments: Any*) { log.info(format, arguments) }
    protected def info(marker: Marker, msg: => Any) { log.info(marker, msg) }
    protected def info(marker: Marker, msg: => Any, t: Throwable) { log.info(marker, msg, t) }
    // @varargs protected def info(marker: Marker, format: => String, arguments: Any*) { log.info(marker, format, arguments) }

    protected def warn(msg: => Any) { log.warn(msg) }
    protected def warn(msg: => Any, t: Throwable) { log.warn(msg, t) }
    // @varargs protected def warn(format: => String, arguments: Any*) { log.warn(format, arguments) }
    protected def warn(marker: Marker, msg: => Any) { log.warn(marker, msg) }
    protected def warn(marker: Marker, msg: => Any, t: Throwable) { log.warn(marker, msg, t) }
    // @varargs protected def warn(marker: Marker, format: => String, arguments: Any*) { log.warn(marker, format, arguments) }

    protected def error(msg: => Any) { log.error(msg) }
    protected def error(msg: => Any, t: Throwable) { log.error(msg, t) }
    // @varargs protected def error(format: => String, arguments: Any*) { log.error(format, arguments) }
    protected def error(marker: Marker, msg: => Any) { log.error(marker, msg) }
    protected def error(marker: Marker, msg: => Any, t: Throwable) { log.error(marker, msg, t) }
    // @varargs protected def error(marker: Marker, format: => String, arguments: Any*) { log.error(marker, format, arguments) }
}
