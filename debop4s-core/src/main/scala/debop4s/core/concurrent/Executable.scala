package debop4s.core.concurrent

import java.util.concurrent.atomic.AtomicLong

import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

/**
 * 제한 시간이 있는 실행 객체의 trait
 * @author sunghyouk.bae@gmail.com
 */
trait Executable {

  /** 수행할 코드 블럭 */
  def execute()

  /** 설정한 타임와웃이 되었을 때 호출되는 메소드 */
  def timeout()
}

/**
 * [[Executable]] 를 Wrapping 한 adapter 입니다.
 * @param executable [[Executable]] 인스턴스
 */
class ExecutableAdapter(val executable: Executable) extends Runnable {

  require(executable != null)

  private lazy val log = LoggerFactory.getLogger(getClass)

  private var error: Option[Throwable] = _
  private var done: Boolean = false

  def isDone: Boolean = done

  override def run(): Unit = {
    log.trace(s"start to execute Executable instance. $executable")
    error = None
    done = false

    try {
      executable.execute()
      log.trace("finish to execute Executable instance")
    } catch {
      case t: Throwable => error = Option(t)
    } finally {
      done = true
    }
  }

  def throwIfErrors(): Unit = {
    error match {
      case Some(e: RuntimeException) => throw e
      case Some(e: scala.Error) => throw e
      case Some(e: Throwable) => throw new ExceptionWrapper(e)
      case _ =>
    }
  }

  case class ExceptionWrapper(cause: Throwable) extends RuntimeException
}

class TimedExecutor(val timeout: Long, val checkMillis: Option[Long] = None) {

  private lazy val log = LoggerFactory.getLogger(getClass)

  def execute(executable: Executable): Unit = {
    require(executable != null)

    val adapter = new ExecutableAdapter(executable)
    val separatedThread = new Thread(adapter)

    val runningTime = new AtomicLong(0)

    separatedThread.start()

    while (!adapter.isDone) {
      if (runningTime.get() > timeout) {
        Try {
          executable.timeout()
        } match {
          case Success(x) => log.trace(s"Executable 실행이 timeout 되었습니다.")
          case Failure(e) => log.error(s"Timeout to execute executable instance.", e)
        }
      }
      Try {
        Thread.sleep(checkMillis.getOrElse(100L))
        runningTime.addAndGet(checkMillis.getOrElse(100L))
      } match {
        case Success(x) => log.trace(s"Executable 실행을 완료했습니다.")
        case Failure(e) => log.debug("Executable 실행 중 예외가 발생했습니다.", e)
      }
    }

    adapter.throwIfErrors()
    log.debug(s"제한된 시간[$timeout] (msec) 동안 지정된 executable 인스턴스를 실행했습니다.")
  }
}
