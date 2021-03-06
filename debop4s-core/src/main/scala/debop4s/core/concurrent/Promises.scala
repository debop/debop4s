package debop4s.core.concurrent

import java.util.concurrent.TimeUnit

import scala.concurrent._
import scala.concurrent.duration._

/**
 * Scala Promise 를 사용합니다. 그냥 future 를 이용해도 됩니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:10
 */
@deprecated("use debop4s.core.concurrent.Asyncs", since = "0.5.0")
object Promises {

  implicit val executor: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def exec[@miniboxed V](block: => V): Future[V] = Future {
    require(block != null)
    block
  }

  def exec[@miniboxed T, V](input: T)(func: T => V): Future[V] = Future {
    require(func != null)
    func(input)
  }

  def exec[@miniboxed T, S, V](input1: T, input2: S)(func: (T, S) => V): Future[V] = Future {
    require(func != null)
    func(input1, input2)
  }

  /**
   * Future 값을 가져옵니다. 최대 5초 동안 기다립니다.
   */
  def await[@miniboxed T](awaitable: Awaitable[T]): T =
    Await.result(awaitable, 15 seconds)

  /**
   * 주어진 timeout 을 가다리다가 작업이 완료되면 값을 반환합니다.
   */
  def await[@miniboxed T](awaitable: Awaitable[T], timeoutMillis: Long): T =
    Await.result(awaitable, timeoutMillis millis)

  /**
   * 주어진 timeout 을 가다리다가 작업이 완료되면 값을 반환합니다.
   */
  def await[@miniboxed T](awaitable: Awaitable[T], atMost: Duration): T =
    Await.result(awaitable, atMost)

  /**
   * 주어진 timeout 을 가다리다가 작업이 완료되면 값을 반환합니다.
   */
  def awaitAll(awaitables: Iterable[Awaitable[_]],
               atMost: Duration = FiniteDuration(15, TimeUnit.MINUTES)): Iterable[Any] = {
    awaitables.par.map(awaitable => Await.result(awaitable, atMost)).seq
  }

  @deprecated("use awaitAll", "0.5.0")
  def resultAll[@miniboxed A](in: Future[A]*): Future[Seq[A]] = {
    Future.sequence(in)
  }
}
