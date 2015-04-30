package debop4s.core.retry

import debop4s.core.utils.Timer
import org.slf4j.LoggerFactory

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

case class PromiseWrapper[T](promise: () => Future[T])

object PromiseWrapper {
  implicit def fromFuture[@miniboxed T](promise: () => Future[T]): PromiseWrapper[T] = PromiseWrapper(promise)
  implicit def toFuture[@miniboxed T](pw: PromiseWrapper[T]): () => Future[T] = pw.promise
}

object Directly {

  /**
   * 성공할 때까지 재시도 합니다.
   */
  def forever: Policy =
    new Policy {
      override def apply[T](promise: PromiseWrapper[T])
                           (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {
        retry(promise, promise)
      }
    }

  /**
   * 성공할 때까지 재시도 횟수 (기본 3회) 만큼 재시도 합니다.
   */
  def apply(max: Int = 3): Policy =
    new CountingPolicy {
      override def apply[T](promise: PromiseWrapper[T])
                           (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {
        def run(max: Int): Future[T] = {
          log.trace(s"작업 실패 시 바로 재실행. 최대 시도 횟수=$max")
          countdown(max, promise, run)
        }
        run(max)
      }
    }

}

object Pause {

  /**
   * 실패시에 delay 시간 후에 영원히 재시도 합니다.
   */
  def forever(delay: FiniteDuration = DEFAULT_DELAY)(implicit timer: Timer): Policy =
    new Policy {
      self =>
      override def apply[@miniboxed T](promise: PromiseWrapper[T])
                           (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {
        val orElse = () => timer.doLater(delay) { self(promise) }.flatMap(identity)
        retry(promise, orElse)
      }
    }

  /**
   * 실패 시에 일정 시간 지연 후 최대 재시도 횟수(기본 4회) 내에서 재시도합니다.
   */
  def apply(max: Int = 4, delay: FiniteDuration = DEFAULT_DELAY)(implicit timer: Timer): Policy =
    new CountingPolicy {
      override def apply[@miniboxed T](promise: PromiseWrapper[T])
                           (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {

        def run(max: Int): Future[T] = {
          log.trace(s"지연시간을 두고 재실행합니다. 최대 시도 횟수=$max, 지연시간=$delay")
          countdown(max, promise, c => timer.doLater(delay)(run(c)).flatMap(identity))
        }
        run(max)
      }
    }

}

object Backoff {

  /**
   * 성공할 때까지 영원히 재시도하는데, 재시도 시 마다 지연 시간이 지수(exponential) 형태로 증가합니다.
   */
  def forever(delay: FiniteDuration = DEFAULT_DELAY, base: Int = 2)(implicit timer: Timer): Policy =
    new Policy {
      override def apply[@miniboxed T](promise: PromiseWrapper[T])
                           (implicit success: Successful[T], executor: ExecutionContext): Future[T] = {
        def run(delay: FiniteDuration): Future[T] = {
          val orElse = () => timer.doLater(delay)(run(Duration(delay.length * base, delay.unit))).flatMap(identity)
          retry(promise, orElse)
        }
        run(delay)
      }
    }

  /**
   * 성공할 때까지 최대 재시도 횟수 (기본: 0회) 만큼 재시도하는데, 재시도 시 마다 지연 시간이 지수(exponential) 형태로 증가합니다.
   */
  def apply(max: Int = 8,
            delay: FiniteDuration = DEFAULT_DELAY,
            base: Int = 2)
           (implicit timer: Timer): Policy =
    new CountingPolicy {
      override def apply[@miniboxed T](promise: PromiseWrapper[T])
                           (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {
        def run(max: Int, delay: FiniteDuration): Future[T] = {
          countdown(
            max,
            promise,
            count => timer.doLater(delay) {
              run(count, Duration(delay.length * base, delay.unit))
            }.flatMap(identity))
        }
        run(max, delay)
      }
    }

}

/**
 * 실패 시의 재시도 정책을 Partial function 을 이용하여 정의할 수 있다.
 *
 * {{{
 *   val policy = When {
 *      case RetryAfter(retryAt) => Pause(delay = retryAt)
 *   }
 *   val future = policy(issueRequest)
 * }}}
 *
 * 결과 값이 partial function case에 해당하지 않는 경우에는 재시도하지 않습니다.
 */
object When {
  type Depends = PartialFunction[Any, Policy]

  def apply(depends: Depends): Policy =
    new Policy {
      override def apply[@miniboxed T](promise: PromiseWrapper[T])
                           (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {
        val future = promise()
        future.flatMap { result =>
          if (successful.predicate(result) || !depends.isDefinedAt(result)) future
          else depends(result)(promise)
        }.recoverWith {
          case NonFatal(e) =>
            log.warn(s"예외가 발생하여, 재시도 정책에 따라 작업을 수행하도록 합니다.", e)
            if (depends.isDefinedAt(e)) {
              log.debug("재시도 정책이 존재하므로, 정책에 따라 후속 작업을 수행합니다.")
              depends(e)(promise)
            } else {
              log.debug("재시도 정책이 없으므로, 후속 작업을 수행하지 않습니다.")
              future
            }
        }
      }
    }
}


/**
 * 특정 작업을 성공할 때까지 재시도 하는 방식에 대한 기본 trait 입니다.
 * @author sunghyouk.bae@gmail.com
 */
trait Policy {

  protected val log = LoggerFactory.getLogger(getClass)

  def apply[@miniboxed T](pw: PromiseWrapper[T])
              (implicit successful: Successful[T], executionContext: ExecutionContext): Future[T]

  def apply[@miniboxed T](promise: => Future[T])
              (implicit successful: Successful[T], executor: ExecutionContext): Future[T] =
    apply { () => promise }

  /**
   * `promise` 를 작업하고, 실패 시에는 `orElse` 를 수행하고, 예외가 발생한 경우는 `recovery` 로 복원합니다.
   */
  protected def retry[@miniboxed T](promise: () => Future[T],
                         orElse: () => Future[T],
                         recovery: Future[T] => Future[T] = identity(_: Future[T]))
                        (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {

    val future = promise()

    future.flatMap { result =>
      if (successful.predicate(result)) future
      else orElse()
    }.recoverWith {
      case NonFatal(e) =>
        log.warn(s"작업 중 예외가 발생했습니다. recovery를 수행합니다.", e)
        recovery(future)
    }
  }
}

trait CountingPolicy extends Policy {

  protected def countdown[T](max: Int,
                             promise: () => Future[T],
                             orElse: Int => Future[T])
                            (implicit successful: Successful[T], executor: ExecutionContext): Future[T] = {

    log.debug(s"작업을 시도합니다. 남은 재시도 횟수=$max")

    // 성공했거나, 재시도 횟수가 0이라면 재시도를 끝내도록 한다. (실패했거나 아직 재시도 횟수가 남았다면 다시 시도하도록한다)
    val countedSuccess: Successful[T] = successful.or(max < 1)
    val recovery = (f: Future[T]) => if (max < 1) f else orElse(max - 1)

    retry(promise, () => orElse(max - 1), recovery)(countedSuccess, executor)
  }
}