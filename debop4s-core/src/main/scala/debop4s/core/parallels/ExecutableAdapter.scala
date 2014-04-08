package debop4s.core.parallels

import org.slf4j.LoggerFactory

/**
 * ExecutableAdapter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 2:07
 */
class ExecutableAdapter(val executable: Executable) extends Runnable {

    require(executable != null)

    lazy val log = LoggerFactory.getLogger(classOf[ExecutableAdapter])

    private var error: Throwable = _
    private var done: Boolean = false

    def isDone = done

    def throwAnyErrors() {
        error match {
            case e: RuntimeException => throw e
            case err: scala.Error => throw err
            case _ => throw new ExceptionWrapper(error)
        }
    }

    def run() {
        log.trace("starting to execute Executable instance.")
        error = null
        done = false

        try {
            executable.execute()
            log.trace("finish to execute Executable instance.")
        } catch {
            case t: Throwable => error = t
        } finally {
            done = true
        }
    }

    case class ExceptionWrapper(cause: Throwable) extends RuntimeException {}

}
