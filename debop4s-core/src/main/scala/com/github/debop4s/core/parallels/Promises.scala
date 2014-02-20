package com.github.debop4s.core.parallels

import org.slf4j.LoggerFactory
import scala.concurrent._
import scala.concurrent.duration._

/**
 * com.github.debop4s.core.parallels.Promises
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:10
 */
object Promises {

    lazy val log = LoggerFactory.getLogger(getClass)

    implicit val executor = ExecutionContext.fromExecutor(scala.concurrent.ExecutionContext.Implicits.global)

    def startNew[V](block: => V)(implicit executor: ExecutionContextExecutor): Future[V] = {
        val promise = Promise[V]()
        executor.execute(new Runnable {
            def run() {
                try {
                    val v = block
                    promise.success(v)
                } catch {
                    case t: Throwable => promise.failure(t)
                }
            }
        })
        promise.future
    }

    def startNew[T, V](input: T)(func: T => V)(implicit executor: ExecutionContextExecutor): Future[V] = {
        val promise = Promise[V]()
        executor.execute(new Runnable {
            def run() {
                try {
                    val v = func(input)
                    promise.success(v)
                } catch {
                    case t: Throwable => promise.failure(t)
                }
            }
        })
        promise.future
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

}
