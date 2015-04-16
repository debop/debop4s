package debop4s.core.support

import javax.annotation.{PostConstruct, PreDestroy}

import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * Background 작업을 관리해주는 Trait 입니다.
 * 상속 받은 클래스는 Worker Thread를 정의해주고, Spring Bean 으로 등록해주면, 자동으로 시작하고, 자동으로 소멸시켜줍니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
// NOTE: Spring Bean으로 등록될 Class에 Logging trait 를 상속 받으면, Bean을 찾지 못하는 경우가 있습니다.
trait BackgroundWorkerSupport {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @volatile protected var running = false
  @volatile protected var stopWorker = false

  private var workerThread: Thread = _

  def isRunning: Boolean = running
  def isStopWorker: Boolean = stopWorker

  /**
   * Worker name
   */
  protected def workerName: String

  /**
   * Background Worker thread 를 생성합니다.
   */
  protected def createWorkerThread(): Thread

  @PostConstruct
  def start() = synchronized {
    if (!running) {
      log.info(s"$workerName 수신 작업을 시작합니다...")
      stopWorker = false

      try {
        workerThread = createWorkerThread()
        workerThread.start()
        running = true

        log.info(s"$workerName 수신 작업을 시작했습니다.")
      } catch {
        case NonFatal(e) =>
          log.error(s"$workerName 수신 작업용 Thread를 생성하는데 실패했습니다.", e)
      }
    }
  }

  @PreDestroy
  def stop() = synchronized {
    if (running) {
      try {
        log.info(s"$workerName 수신 작업을 종료합니다...")
        stopWorker = true
        Thread.sleep(10)
        if (workerThread != null) {
          workerThread.interrupt()
          workerThread.join(500L)
          workerThread = null
        }
      } catch {
        case irr: InterruptedException =>
        case ignored: Throwable =>
          log.debug(s"$workerName 수신 작업용 Thread를 종료하는데 실패했습니다.", ignored)
      } finally {
        running = false
      }
      log.info(s"$workerName 수신 작업을 종료했습니다.")
    }
  }

}
