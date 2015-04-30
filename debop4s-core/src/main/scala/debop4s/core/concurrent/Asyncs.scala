package debop4s.core.concurrent

import java.lang.{Iterable => JIterable}
import java.util.concurrent.{Callable, TimeUnit}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

/**
 * Scala 에서 비동기 작업을 수행합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:59
 */
object Asyncs {

  lazy val defaultDuration: Duration = Duration(15, TimeUnit.MINUTES)

  lazy val EMPTY_RUNNABLE = new Runnable {
    def run() { /* nothing to do. */ }
  }

  def run[@miniboxed V](callable: Callable[V]): Future[V] = Future { callable.call() }

  def run[@miniboxed V](block: => V): Future[V] = Future { block }

  def continueTask[T, V](prevTask: Future[T])(block: T => V): Future[V] =
    prevTask.map(v => block(v))

  def runAll[T, R](elements: Iterable[T], function: T => R): Future[Iterable[R]] = {
    Future.sequence(elements.map(x => Future { function(x) }))
  }

  def invokeAll[@miniboxed T](tasks: Iterable[_ <: Callable[T]]): Iterable[T] =
    resultAll(tasks.map { x => Future { x.call() } })

  def invokeAll[@miniboxed T](tasks: Iterable[_ <: Callable[T]], timeout: Long, unit: TimeUnit): Iterable[T] =
    resultAll(tasks.map { x => Future { x.call() } }, timeout, unit)

  def ready[@miniboxed T](awaitable: Awaitable[T]): Unit =
    ready(awaitable, defaultDuration)

  def ready[@miniboxed T](awaitable: Awaitable[T], timeoutMillis: Long): Unit =
    ready(awaitable, timeoutMillis millis)

  def ready[@miniboxed T](awaitable: Awaitable[T], atMost: Duration): Unit = {
    Await.ready[T](awaitable, atMost)
  }

  def readyAll[@miniboxed T](tasks: Iterable[_ <: Future[T]]): Unit = {
    ready(Future.sequence(tasks.toSeq))
  }

  def readyAll[@miniboxed T](tasks: JIterable[_ <: Future[T]]): Unit = {
    readyAll(tasks.asScala)
  }

  def result[@miniboxed T](awaitable: Awaitable[T]): T =
    Await.result(awaitable, defaultDuration)

  def result[@miniboxed T](awaitable: Awaitable[T], timeoutMillis: Long): T =
    Await.result(awaitable, timeoutMillis millis)

  def result[@miniboxed T](awaitable: Awaitable[T], atMost: Duration): T =
    Await.result(awaitable, atMost)

  def resultAll[@miniboxed T](tasks: Iterable[_ <: Future[T]]): Iterable[T] =
    result(Future.sequence(tasks.toSeq), defaultDuration)

  def resultAll[@miniboxed T](tasks: JIterable[_ <: Future[T]]): Iterable[T] =
    resultAll(tasks.asScala)

  def resultAll[@miniboxed T](tasks: Iterable[_ <: Future[T]], timeout: Long, unit: TimeUnit): Iterable[T] =
    result(Future.sequence(tasks.toSeq), Duration(timeout, unit))

  def resultAll[@miniboxed T](tasks: JIterable[_ <: Future[T]], timeout: Long, unit: TimeUnit): Iterable[T] =
    resultAll(tasks.asScala, timeout, unit)
}
