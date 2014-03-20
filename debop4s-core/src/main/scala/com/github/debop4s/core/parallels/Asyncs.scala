package com.github.debop4s.core.parallels

import com.github.debop4s.core._
import java.util.concurrent.{TimeUnit, Callable}
import scala.concurrent._
import scala.concurrent.duration.{FiniteDuration, Duration}

/**
 * Scala 에서 비동기 작업을 수행합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:59
 */
object Asyncs {

    lazy val EMPTY_RUNNABLE = new Runnable {
        def run() {
            /* nothing to do. */
        }
    }

    lazy val defaultDuration: Duration = new FiniteDuration(15, TimeUnit.MINUTES)

    def run[V](callable: Callable[V]): concurrent.Future[V] = future {
        callable.call()
    }

    def run[V](block: => V): concurrent.Future[V] = future {
        block
    }

    def continueTask[T, V](prevTask: Future[T])(block: T => V): Future[V] = {
        prevTask.map(v => block(v))
    }

    def runAll[T, R](elements: Iterable[T], function: T => R): Iterable[Future[R]] = {
        elements.map(x => future {function(x)})
    }

    def invokeAll[T](tasks: Iterable[_ <: Callable[T]]): Iterable[T] = {
        resultAll(tasks.map(x => future {x.call()}))
    }

    def invokeAll[T](tasks: Iterable[_ <: Callable[T]], timeout: Long, unit: TimeUnit): Iterable[T] = {
        resultAll(tasks.map(x => future {x.call()}), timeout, unit)
    }

    def ready[T](awaitable: Awaitable[T]): Awaitable[T] = {
        Await.ready(awaitable, defaultDuration)
    }

    def readyAll[T](tasks: Iterable[_ <: Future[T]]) {
        while (!tasks.forall { task =>
            Await.ready(task, defaultDuration)
            task.isCompleted
        }) {
            Thread.sleep(1)
        }
    }

    def result[T](awaitable: Awaitable[T]): T = {
        Await.result(awaitable, defaultDuration)
    }

    def resultAll[T](tasks: Iterable[_ <: Future[T]]): Iterable[T] = {
        tasks.map(task => Await.result(task, defaultDuration))
    }

    def resultAll[T](tasks: Iterable[_ <: Future[T]], timeout: Long, unit: TimeUnit): Iterable[T] = {
        tasks.map(task => Await.result(task, Duration(timeout, unit)))
    }


}
