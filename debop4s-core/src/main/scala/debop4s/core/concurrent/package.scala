package debop4s.core

import java.util.concurrent.TimeUnit

import debop4s.core.utils.JavaTimer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}

/**
 * Concurrent package object
 * @author sunghyouk.bae@gmail.com
 */
package object concurrent {

  implicit class FutureExtensions[+A](underlying: Future[A]) {

    implicit val timeout = FiniteDuration(5, TimeUnit.MINUTES)

    /** future 객체의 수행이 완료될 때까지 기다렸다가 결과를 반환합니다. */
    def await(timeout: Long): A = {
      Await.result(underlying, Duration(timeout, TimeUnit.MILLISECONDS))
    }

    /** future 객체의 수행이 완료될 때까지 기다렸다가 결과를 반환합니다. */
    def await(implicit timeout: Duration = timeout): A = {
      Await.result(underlying, timeout)
    }

    /** future 객체의 결과를 기다립니다. */
    def hold(timeout: Long): Future[A] = {
      Await.ready(underlying, Duration(timeout, TimeUnit.MILLISECONDS))
    }

    /** future 객체의 결과를 기다립니다. */
    def hold(implicit timeout: Duration = timeout): Future[A] = {
      Await.ready(underlying, timeout)
    }

    /** future 객체가 완료된 후 지정된 시간만큼 지연을 시킨 후 결과를 반환하도록 합니다. */
    def delay(duration: FiniteDuration): Future[A] = {
      val promise = Promise[A]()

      underlying onComplete { result =>
        val timer = JavaTimer()
        timer.doLater(duration) {
          promise.complete(result)
          timer.stop()
        }
      }
      promise.future
    }
  }

  implicit class FutureListExtensions[+A](underlying: Iterable[Future[A]]) {

    implicit val timeout = FiniteDuration(15, TimeUnit.MINUTES)

    def awaitAll(implicit timeout: Duration = timeout): Iterable[A] = {
      Await.result(Future.sequence(underlying), timeout)
    }

    def holdAll(implicit timeout: Duration = timeout): Future[Iterable[A]] = {
      Await.ready(Future.sequence(underlying), timeout)
    }
  }
}
