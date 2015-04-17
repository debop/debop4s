package org.hibernate.cache

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, Future}

/**
 * package
 * @author sunghyouk.bae@gmail.com
 */
package object rediscala {

  /**
   * hiberante-rediscala 에서 사용할 `ActorSystem` 입니다.
   */
  implicit val actorSystem = akka.actor.ActorSystem("hibernate-rediscala")

  /**
   * 비동기 작업 시에 사용할 `ExecutionContextExecutor` 입니다.
   */
  implicit val executor = actorSystem.dispatcher


  /**
   * `Future` 인스턴스에 대한 Extension Method 를 제공합니다.
   * @param underlying `Future` instance
   * @tparam A `Future`의 return 값
   */
  private[rediscala] implicit class FutureExtensions[+A](underlying: Future[A]) {

    val timeout = FiniteDuration(5, TimeUnit.MINUTES)

    def await(implicit timeout: FiniteDuration = timeout): A = {
      Await.result(underlying, timeout)
    }

    def ready(implicit timeout: Duration = timeout): Future[A] = {
      Await.ready(underlying, timeout)
    }
  }

  /**
   * `Future` 컬렉션에 대한 Extension Method 를 제공합니다.
   */
  private[rediscala] implicit class FutureListExtensions[+A](underlying: Iterable[Future[A]]) {
    val timeout = FiniteDuration(15, TimeUnit.MINUTES)

    def awaitAll(implicit timeout: Duration = timeout): Iterable[A] = {
      Await.result(Future.sequence(underlying), timeout)
    }

    def readyAll(implicit timeout: Duration = timeout): Future[Iterable[A]] = {
      Await.ready(Future.sequence(underlying), timeout)
    }
  }
}
