package com.github.debop4s.core.parallels

import java.util.concurrent.TimeUnit
import scala.concurrent._
import scala.concurrent.duration._

/**
 * Scala Promise 를 사용합니다. 그냥 future 를 이용해도 됩니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:10
 */
object Promises {

  implicit val executor = ExecutionContext.fromExecutor(scala.concurrent.ExecutionContext.Implicits.global)

  def exec[@specialized(Int, Long) V](block: => V): Future[V] = future {
    require(block != null)
    block
  }

  def exec[@specialized(Int, Long) T, V](input: T)(func: T => V): Future[V] = future {
    require(func != null)
    func(input)
  }

  /**
   * Future 값을 가져옵니다. 최대 5초 동안 기다립니다.
   */
  def await[@specialized(Int, Long) T](awaitable: Awaitable[T]): T =
    Await.result(awaitable, 15 seconds)

  /**
   * 주어진 timeout 을 가다리다가 작업이 완료되면 값을 반환합니다.
   */
  def await[@specialized(Int, Long) T](awaitable: Awaitable[T], timeoutMillis: Long): T =
    Await.result(awaitable, timeoutMillis millis)

  def await[@specialized(Int, Long) T](awaitable: Awaitable[T], atMost: Duration): T =
    Await.result(awaitable, atMost)

  def awaitAll(awaitables: Iterable[Awaitable[_]],
               atMost: Duration = FiniteDuration(15, TimeUnit.MINUTES)): Iterable[Any] = {
    awaitables.map(awaitable => Await.result(awaitable, atMost))
  }

  def resultAll[A](in: Future[A]*): Future[Seq[A]] = {

    val p = Promise[Seq[A]]()
    in.foreach(_.onFailure { case e => p tryFailure e})
    Future.sequence(in).foreach(p.trySuccess)
    p.future
  }
}
