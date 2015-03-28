package debop4s.core.pool

import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.{ Failure, Success }

/**
 * twitter/util 에 있는 pool 을 porting 했습니다.
 * 기존 apache common 과는 달리 `Future` 를 사용합니다.
 */
trait Pool[A] {
  /**
   * 객체를 생성하는 `Future` 를 `Pool`에서 꺼냅니다.
   */
  def reserve(): Future[A]
  /**
   * 사용한 객체를 `Pool`에 보관합니다.
   */
  def release(a: A)
}

class SimplePool[A](items: mutable.Queue[Future[A]]) extends Pool[A] {
  private lazy val log = LoggerFactory.getLogger(getClass)

  def this(items: Seq[A]) = this {
    val queue = new mutable.Queue[Future[A]]
    queue ++= items map { item => Future(item) }
    queue
  }

  private val requests = mutable.Queue[Promise[A]]()

  def reserve(): Future[A] = synchronized {
    if (items.isEmpty) {
      val promise = Promise[A]()
      requests += promise
      promise.future
    } else {
      items.dequeue()
    }
  }

  def release(item: A) {
    items += Future.successful(item)
    synchronized {
      if (!requests.isEmpty && !items.isEmpty)
        Some((requests.dequeue(), items.dequeue()))
      else
        None
    } map { case (request, fa) =>
      fa onComplete {
        case Success(a) => request.success(a)
        case Failure(e) => request.failure(e)
      }
    }
  }
}

abstract class FactoryPool[A](numItems: Int) extends Pool[A] {
  private val healthyQueue = new HealthyQueue[A](makeItem, numItems, isHealthy)
  private val simplePool = new SimplePool[A](healthyQueue)

  def reserve(): Future[A] = simplePool.reserve()
  def release(a: A) {
    simplePool.release(a)
  }
  def dispose(a: A) {
    healthyQueue += makeItem()
  }

  protected def makeItem(): Future[A]
  protected def isHealthy(a: A): Boolean
}

private class HealthyQueue[A](makeItem: () => Future[A],
                              numItems: Int,
                              isHealthy: A => Boolean) extends mutable.QueueProxy[Future[A]] {

  val self = new mutable.SynchronizedQueue[Future[A]]
  synchronized {
    ( 0 until numItems ).foreach(_ => self += makeItem())
  }

  private val log = LoggerFactory.getLogger(getClass)

  override def +=(item: Future[A]) = {
    synchronized { self += item }
    this
  }

  override def dequeue(): Future[A] = synchronized {
    if (isEmpty)
      throw new NoSuchElementException("queue is empty")

    self.dequeue() flatMap { item =>
      if (isHealthy(item)) {
        Future(item)
      } else {
        // LOG.debug(s"not healthy item! $item")
        val newItem = makeItem()
        synchronized {
          this += newItem
          dequeue()
        }
      }
    }
  }

}
