package kr.debop4s.core.parallels

import kr.debop4s.core.logging.Logger

/**
 * ExecutableAdapter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 2:07
 */
class ExecutableAdapter(val executable: Executable) extends Runnable {

    assert(executable != null)

    implicit lazy val log = Logger[ExecutableAdapter]

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
