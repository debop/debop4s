package kr.debop4s.core.logging

import language.implicitConversions
import org.slf4j.{Marker, Logger => Slf4jLogger}
import scala.language.implicitConversions

/**
 * Scala를 위한 Logger 입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 9:37
 */
final class Logger(val log: Slf4jLogger) {

    lazy val name = log.getName

    @inline
    private implicit def _any2String(msg: Any): String = {
        msg match {
            case null => "<null>"
            case _ => msg.toString
        }
    }

    lazy val isTraceEnabled = log.isTraceEnabled
    @inline def isTraceEnabled(marker: Marker) = log.isTraceEnabled(marker)

    @inline
    def trace(msg: => Any) {
        if (isTraceEnabled) log.trace(msg.toString)
    }
    @inline
    def trace(msg: => Any, t: Throwable) {
        if (isTraceEnabled) log.trace(msg.toString, t)
    }
    @inline
    def trace(marker: Marker, msg: => Any) {
        if (isTraceEnabled(marker))
            log.trace(marker, msg.toString)
    }
    @inline
    def trace(marker: Marker, msg: => Any, t: Throwable) {
        if (isTraceEnabled(marker))
            log.trace(marker, msg.toString, t)
    }

    lazy val isDebugEnabled = log.isDebugEnabled
    @inline def isDebugEnabled(marker: Marker) = log.isDebugEnabled(marker)

    @inline
    def debug(msg: => Any) {
        if (isDebugEnabled) log.debug(msg.toString)
    }
    @inline
    def debug(msg: => Any, t: Throwable) {
        if (isDebugEnabled) log.debug(msg.toString, t)
    }
    @inline
    def debug(marker: Marker, msg: => Any) {
        if (isDebugEnabled(marker))
            log.debug(marker, msg.toString)
    }
    @inline
    def debug(marker: Marker, msg: => Any, t: Throwable) {
        if (isDebugEnabled(marker))
            log.debug(marker, msg.toString, t)
    }

    lazy val isInfoEnabled = log.isInfoEnabled
    @inline def isInfoEnabled(marker: Marker) = log.isInfoEnabled(marker)

    @inline
    def info(msg: => Any) {
        if (isInfoEnabled) log.info(msg.toString)
    }
    @inline
    def info(msg: => Any, t: Throwable) {
        if (isInfoEnabled) log.info(msg.toString, t)
    }
    @inline
    def info(marker: Marker, msg: => Any) {
        if (isInfoEnabled(marker))
            log.info(marker, msg.toString)
    }
    @inline
    def info(marker: Marker, msg: => Any, t: Throwable) {
        if (isInfoEnabled(marker))
            log.info(marker, msg.toString, t)
    }

    lazy val isWarnEnabled = log.isWarnEnabled
    @inline def isWarnEnabled(marker: Marker) = log.isWarnEnabled(marker)

    @inline
    def warn(msg: => Any) {
        if (isWarnEnabled)
            log.warn(msg.toString)
    }
    @inline
    def warn(msg: => Any, t: Throwable) {
        if (isWarnEnabled)
            log.warn(msg.toString, t)
    }
    @inline
    def warn(marker: Marker, msg: => Any) {
        if (isWarnEnabled(marker))
            log.warn(marker, msg.toString)
    }
    @inline
    def warn(marker: Marker, msg: => Any, t: Throwable) {
        if (isWarnEnabled(marker))
            log.warn(marker, msg.toString, t)
    }

    lazy val isErrorEnabled = log.isErrorEnabled
    @inline def isErrorEnabled(marker: Marker) = log.isErrorEnabled(marker)

    @inline
    def error(msg: => Any) {
        if (isErrorEnabled)
            log.error(msg.toString)
    }
    @inline
    def error(msg: => Any, t: Throwable) {
        if (isErrorEnabled)
            log.error(msg.toString, t)
    }
    @inline
    def error(marker: Marker, msg: => Any) {
        if (isErrorEnabled(marker))
            log.error(marker, msg.toString)
    }
    @inline
    def error(marker: Marker, msg: => Any, t: Throwable) {
        if (isErrorEnabled(marker))
            log.error(marker, msg.toString, t)
    }
}

/**
 * Logger
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  13. 6. 2. 오후 4:43
 */
object Logger {

    import reflect.{classTag, ClassTag}

    lazy val RootLoggerName = Slf4jLogger.ROOT_LOGGER_NAME

    /**
     * 지정된 이름을 Logger 이름으로 사용합니다. 예: Logger("LoggerName")
     */
    def apply(name: String): Logger = new Logger(org.slf4j.LoggerFactory.getLogger(name))

    /**
     * 지정한 클래스 수형에 맞는 Logger를 반환합니다.
     * {{{
     *     val log = Logger(classOf[MyClass])
     * }}}
     */
    def apply(cls: Class[_]): Logger = apply(cls.getName)

    /**
     * 특정 클래스에 맞는 Logger 를 반환합니다.
     * {{{
     *  val log = Logger[classOf[MyClass]]
     * }}}
     */
    def apply[C: ClassTag]: Logger = apply(classTag[C].runtimeClass.getName)

    /**
     * 특정 클래스에 맞는 Logger 를 반환합니다.
     * {{{
     *   val log = Logger[MyClass]
     * }}}
     */
    def get[T](implicit tag: reflect.ClassTag[T]): Logger = apply(tag.runtimeClass.getName)

    /**
     * Root Logger
     */
    def rootLogger = apply(RootLoggerName)
}
