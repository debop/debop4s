package debop4s.core.concurrent

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scala.concurrent._

/**
 * 비동기 FIFO 큐
 * Created by debop on 2014. 4. 9.
 */
object AsyncQueue {
  private sealed trait State[@miniboxed +T]
  private case object Idle extends State[Nothing]
  private case class Offering[@miniboxed T](q: Queue[T]) extends State[T]
  private case class Polling[@miniboxed T](q: Queue[Promise[T]]) extends State[T]
  private case class Excepting[@miniboxed T](exc: Throwable) extends State[T]
}

/**
 * 비동기 방식의 FIFO 큐.
 * 부가적으로 {{offer()}} 와 {{poll()}} 메소드를 제공합니다.
 */
class AsyncQueue[@miniboxed T] {

  import AsyncQueue._

  private[this] val state: AtomicReference[State[T]] = new AtomicReference[State[T]](Idle)

  def size: Int = {
    state.get match {
      case Offering(q) => q.size
      case _ => 0
    }
  }

  /**
   * 큐의 첫번째 요소를 꺼냅니다.
   * 요소에는 실제 값이 있는 것이 아니라, 비동적으로 계산되는 `Promise[T]`를 반환합니다.
   */
  @tailrec
  final def poll(): Promise[T] = state.get match {
    // 큐가 비어 있는 경우 Promise를 반환합니다.
    case s @ Idle =>
      val p = Promise[T]()
      if (state.compareAndSet(s, Polling(Queue(p)))) p else poll()

    // 현재 상태가 polling 상태라면 Promise를 반환합니다.
    case s @ Polling(q) =>
      val p = Promise[T]()
      if (state.compareAndSet(s, Polling(q.enqueue(p)))) p else poll()

    // 큐에 값이 설정되는 상태라면 그 값을 제공합니다.
    case s @ Offering(q) =>
      val (elem, nextq) = q.dequeue
      val nextState = if (nextq.nonEmpty) Offering(nextq) else Idle
      if (state.compareAndSet(s, nextState)) Promise.successful(elem) else poll()

    case Excepting(exc) =>
      Promise.failed(exc)
  }

  /**
   * 큐의 마지막에 지정한 요소를 추가합니다.
   * `poll`로 기다리는 놈들에게 값을 제공합니다.
   */
  @tailrec
  final def offer(elem: T): Unit = state.get match {
    case s @ Idle =>
      if (!state.compareAndSet(s, Offering(Queue(elem))))
        offer(elem)

    // 값을 추가합니다.
    case s @ Offering(q) =>
      if (!state.compareAndSet(s, Offering(q.enqueue(elem))))
        offer(elem)

    // 값을 기다리는 놈들이 있는 경우 값을 제공합니다.
    case s @ Polling(q) =>
      val (waiter, nextq) = q.dequeue
      val nextState = if (nextq.nonEmpty) Polling(nextq) else Idle
      if (state.compareAndSet(s, nextState))
        waiter.success(elem)
      else
        offer(elem)

    case Excepting(_) => // Drop.
  }

  /**
   * 현재와 후속의 모든 poller 들에게 예외가 보냅니다.
   */
  @tailrec
  final def fail(exc: Throwable): Unit = state.get match {
    case s @ Idle =>
      if (!state.compareAndSet(s, Excepting(exc)))
        fail(exc)

    case s @ Polling(q) =>
      if (!state.compareAndSet(s, Excepting(exc))) fail(exc)
      else q.foreach(_.failure(exc))

    case s @ Offering(_) =>
      if (!state.compareAndSet(s, Excepting(exc))) fail(exc)

    case Excepting(_) => // just take the first one.
  }

  override def toString: String = s"AsyncQueue<${ state.get }>"
}
