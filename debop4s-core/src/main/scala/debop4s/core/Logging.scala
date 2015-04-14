package debop4s.core

import java.util.concurrent.atomic.AtomicLong

import org.slf4j.{LoggerFactory, MDC}

/**
 * Slf4j 의 Log를 생성합니다.
 */
object Log {

  def apply(name: String): Log = new Log {
    override lazy val log = LoggerFactory.getLogger(name)
  }

  def apply(clazz: Class[_]): Log =
    apply(clazz.getName.replace("$", "#").stripSuffix("#"))

  def apply(clazz: Class[_], suffix: String): Log =
    apply(clazz.getName.replace("$", "#").stripSuffix("#") + "." + suffix)

  val exceptionIdGenerator = new AtomicLong(System.currentTimeMillis())
  def nextExceptionId = exceptionIdGenerator.incrementAndGet().toHexString
}

/**
 * Slf4j Log Trait
 */
trait Log extends Serializable {

  import Log._

  lazy val log = LoggerFactory.getLogger(getClass.getName.replace("$", "#").stripSuffix("#"))

  @inline
  private def withThrowable(e: Throwable)(block: => Unit) {
    if (e != null) {
      val stackRef: Option[String] =
        if (log.isDebugEnabled) {
          val id = nextExceptionId
          MDC.put("stackref", id.toString)
          Some(id)
        } else {
          None
        }

      block

      stackRef.foreach { id =>
        log.debug("stack trace: " + id, e)
        MDC.remove("stackref")
      }
    } else {
      block
    }
  }

  @inline
  private def format(message: String, args: Seq[Any]) = {
    if (args.isEmpty) message
    else message.format(args.map(_.asInstanceOf[AnyRef]): _*)
  }

  def error(m: => String, args: Any*) {
    if (log.isErrorEnabled) {
      log.error(format(m, args.toSeq))
    }
  }

  def error(e: Throwable, m: => String, args: Any*) {
    if (log.isErrorEnabled) {
      withThrowable(e) {
        log.error(format(m, args.toSeq))
      }
    }
  }

  def error(e: Throwable) {
    if (log.isErrorEnabled) {
      withThrowable(e) {
        log.error(if (e != null) e.getMessage else "")
      }
    }
  }

  def warn(m: => String, args: Any*) {
    if (log.isWarnEnabled) {
      log.warn(format(m, args.toSeq))
    }
  }

  def warn(e: Throwable, m: => String, args: Any*) {
    if (log.isWarnEnabled) {
      withThrowable(e) {
        log.warn(format(m, args.toSeq))
      }
    }
  }

  def warn(e: Throwable) {
    if (log.isWarnEnabled) {
      withThrowable(e) {
        log.warn(if (e != null) e.getMessage else "")
      }
    }
  }

  def info(m: => String, args: Any*) {
    if (log.isInfoEnabled) {
      log.info(format(m, args.toSeq))
    }
  }

  def info(e: Throwable, m: => String, args: Any*) {
    if (log.isInfoEnabled) {
      withThrowable(e) {
        log.info(format(m, args.toSeq))
      }
    }
  }

  def info(e: Throwable) {
    if (log.isInfoEnabled) {
      withThrowable(e) {
        log.info(if (e != null) e.getMessage else "")
      }
    }
  }

  def debug(m: => String, args: Any*) {
    if (log.isDebugEnabled) {
      log.debug(format(m, args.toSeq))
    }
  }

  def debug(e: Throwable, m: => String, args: Any*) {
    if (log.isDebugEnabled) {
      withThrowable(e) {
        log.debug(format(m, args.toSeq))
      }
    }
  }

  def debug(e: Throwable) {
    if (log.isDebugEnabled) {
      withThrowable(e) {
        log.debug(if (e != null) e.getMessage else "")
      }
    }
  }

  def trace(m: => String, args: Any*) {
    if (log.isTraceEnabled) {
      log.trace(format(m, args.toSeq))
    }
  }

  def trace(e: Throwable, m: => String, args: Any*) {
    if (log.isTraceEnabled) {
      withThrowable(e) {
        log.trace(format(m, args.toSeq))
      }
    }
  }

  def trace(e: Throwable) {
    if (log.isTraceEnabled) {
      withThrowable(e) {
        log.trace(if (e != null) e.getMessage else "")
      }
    }
  }
}

/**
 * Logging trait. cake pattern 으로 상속 받으면 됩니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
trait Logging {
  protected val log = Log(getClass)

  protected def error(message: => String): Unit = log.error(message)
  protected def error(message: => String, e: Throwable): Unit = log.error(e, message)
  protected def error(e: Throwable): Unit = log.error(e)

  protected def warn(message: => String): Unit = log.warn(message)
  protected def warn(message: => String, e: Throwable): Unit = log.warn(e, message)
  protected def warn(e: Throwable): Unit = log.warn(e)

  protected def info(message: => String): Unit = log.info(message)
  protected def info(message: => String, e: Throwable): Unit = log.info(e, message)
  protected def info(e: Throwable): Unit = log.info(e)

  protected def debug(message: => String): Unit = log.debug(message)
  protected def debug(message: => String, e: Throwable): Unit = log.debug(e, message)
  protected def debug(e: Throwable): Unit = log.debug(e)

  protected def trace(message: => String): Unit = log.trace(message)
  protected def trace(message: => String, e: Throwable): Unit = log.trace(e, message)
  protected def trace(e: Throwable): Unit = log.trace(e)
}
