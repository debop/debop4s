package com.github.debop4s.core.parallels

import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import scala.concurrent._
import scala.concurrent.duration._

/**
 * Scala Promise 를 사용합니다. 그냥 future 를 이용해도 됩니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:10
 */
object Promises {

    private lazy val log = LoggerFactory.getLogger(getClass)

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
