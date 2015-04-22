package debop4s.core.conversions

import java.util.concurrent.Callable

/**
 * Implicits for turning a block of code into a Runnable or Callable.
 * Created by debop on 2014. 4. 5.
 */
object thread {

  /**
   * 지정된 `block` 을 실행하는 `Runnable` 을 생성합니다.
   * @param block  실행할 block
   * @return  `Runnable` 인스턴스
   */
  implicit def makeRunnable(block: => Unit): Runnable = new Runnable() {
    def run(): Unit = block
  }

  /**
   * 지정된 `func` 을 실행하는 `Callable` 을 생성합니다.
   * @param func  실행할 func
   * @tparam T  `func`의 반환 값의 수형
   * @return `Callable` 인스턴스
   */
  implicit def makeCallabke[T](func: => T): Callable[T] = new Callable[T]() {
    def call(): T = func
  }

  /**
   * 지정한 `block` 을 실행하는 `Thread` 를 생성합니다.
   * @param block 실행할 코드 블럭
   * @return thread 인스턴스
   */
  implicit def createThread(block: => Unit): Thread =
    new Thread(makeRunnable(block))

  /**
   * 지정한 `block` 을 실행하는 `Thread` 를 생성하고, 시작합니다.
   * @param block 실행할 코드 블럭
   * @param daemon  Daemon 여부
   * @return thread 인스턴스
   */
  implicit def startThread(block: => Unit, daemon: Boolean = true): Thread = {
    val thread = createThread { block }
    thread.setDaemon(daemon)
    thread.start()
    thread
  }
}

