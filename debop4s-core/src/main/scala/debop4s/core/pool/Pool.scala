package debop4s.core.pool

import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.{Failure, Success}

/**
 * twitter/util 에 있는 pool 을 porting 했습니다.
 * 기존 apache common 과는 달리 `Future` 를 사용합니다.
 */
trait Pool[@miniboxed A] {
  /**
   * 객체를 생성하는 `Future` 를 `Pool`에서 꺼냅니다.
   */
  def reserve(): Future[A]
  /**
   * 사용한 객체를 `Pool`에 보관합니다.
   */
  def release(a: A): Unit
}

class SimplePool[@miniboxed A](items: mutable.Queue[Future[A]]) extends Pool[A] {

  def this(items: Seq[A]) = this {
    val queue = new mutable.Queue[Future[A]]
    queue ++= items map { item => Future(item) }
    queue
  }

  private[this] val requests = mutable.Queue[Promise[A]]()

  def reserve(): Future[A] = synchronized {
    if (items.isEmpty) {
      val promise = Promise[A]()
      requests += promise
      promise.future
    } else {
      items.dequeue()
    }
  }

  def release(item: A): Unit = {
    items += Future.successful(item)
    synchronized {
      if (requests.nonEmpty && items.nonEmpty)
        Some((requests.dequeue(), items.dequeue()))
      else
        None
    } foreach { case (request, fa) =>
      fa onComplete {
        case Success(a) => request.success(a)
        case Failure(e) => request.failure(e)
      }
    }
  }
}

abstract class FactoryPool[@miniboxed A](numItems: Int) extends Pool[A] {

  private[this] val healthyQueue = new HealthyQueue[A](makeItem, numItems, isHealthy)
  private[this] val simplePool = new SimplePool[A](healthyQueue)

  def reserve(): Future[A] = simplePool.reserve()
  def release(a: A): Unit = simplePool.release(a)

  def dispose(a: A): Unit = healthyQueue += makeItem()

  protected def makeItem(): Future[A]
  protected def isHealthy(a: A): Boolean
}

private class HealthyQueue[@miniboxed A](makeItem: () => Future[A],
                              numItems: Int,
                              isHealthy: A => Boolean) extends mutable.QueueProxy[Future[A]] {

  override val self = new mutable.Queue[Future[A]]

  synchronized {
    (0 until numItems).foreach(_ => self += makeItem())
  }

  override def +=(item: Future[A]): this.type = {
    synchronized { self += item }
    this
  }

  override def enqueue(elems: Future[A]*): Unit = synchronized {
    this ++= elems
  }

  override def dequeue(): Future[A] = synchronized {
    if (self.isEmpty)
      throw new NoSuchElementException("queue is empty")

    self.dequeue() flatMap { item =>
      if (isHealthy(item)) {
        Future(item)
      } else {
        // log.debug(s"not healthy item! $item")
        val newItem = makeItem()
        synchronized {
          this += newItem
          dequeue()
        }
      }
    }
  }

}
