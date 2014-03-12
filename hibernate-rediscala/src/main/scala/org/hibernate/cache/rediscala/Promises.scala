package org.hibernate.cache.rediscala

import java.util.concurrent.TimeUnit
import scala.concurrent._
import scala.concurrent.duration._

/**
 * Created by debop on 2014. 3. 10.
 */
object Promises {

    implicit val executor = ExecutionContext.fromExecutor(scala.concurrent.ExecutionContext.Implicits.global)

    def exec[V](block: => V): Future[V] = future {
        require(block != null)
        block
    }

    def exec[T, V](input: T)(func: T => V): Future[V] = future {
        require(func != null)
        func(input)
    }

    /**
     * Future 값을 가져옵니다. 최대 5초 동안 기다립니다.
     */
    def await[T](awaitable: Awaitable[T]): T =
        Await.result(awaitable, 15 seconds)

    /**
     * 주어진 timeout 을 가다리다가 작업이 완료되면 값을 반환합니다.
     */
    def await[T](awaitable: Awaitable[T], timeoutMillis: Long): T =
        Await.result(awaitable, timeoutMillis millis)

    def await[T](awaitable: Awaitable[T], atMost: Duration): T =
        Await.result(awaitable, atMost)

    def awaitAll(awaitables: Iterable[Awaitable[_]], atMost: Duration = FiniteDuration(15, TimeUnit.MINUTES)): Iterable[Any] = {
        awaitables.map(awaitable => Await.result(awaitable, atMost))
    }
}