package debop4s.core.collections

import java.util
import java.util.concurrent.atomic.AtomicReference

/**
 * `java.util.Queue` 를 이용하여 최대 크기가 1이고, LIFO 순서의 큐를 구현합니다.
 * 큐가 모두 찾을 경우, 요소를 교체합니다.
 *
 * Created by debop on 2014. 4. 13.
 */
class LastWriteWinsQueue[@miniboxed A] extends java.util.Queue[A] {

  val item = new AtomicReference[Option[A]](None)

  def clear() {
    item.set(None)
  }

  def retainAll(p1: util.Collection[_]) = throw new UnsupportedOperationException()
  def removeAll(p1: util.Collection[_]) = throw new UnsupportedOperationException()
  def addAll(p1: util.Collection[_ <: A]) = throw new UnsupportedOperationException()

  def containsAll(p1: util.Collection[_]) =
    p1.size == 1 && item.get == p1.iterator().next()

  def remove(candidate: AnyRef) = {
    val contained = item.get()
    val containsCandidate = contained.contains(candidate)
    if (containsCandidate) {
      item.compareAndSet(contained, None)
    }
    containsCandidate
  }

  def toArray[T](array: Array[T with java.lang.Object]): Array[T with java.lang.Object] = {
    val contained = item.get()
    if (contained.isDefined && array.length > 0) {
      array(0) = contained.get.asInstanceOf[T with java.lang.Object]
      array
    } else if (contained.isDefined) {
      Array[Any](contained.get).asInstanceOf[Array[T with java.lang.Object]]
    } else {
      Array[Any]().asInstanceOf[Array[T with java.lang.Object]]
    }
  }

  def toArray: Array[AnyRef] = toArray(new Array[AnyRef](0))
  def iterator = null
  def contains(p1: AnyRef): Boolean = false
  def isEmpty: Boolean = item.get.isDefined
  def size: Int = if (item.get.isDefined) 1 else 0
  def peek: A = item.get.getOrElse(null.asInstanceOf[A])
  def element: A = item.get.getOrElse(throw new NoSuchElementException)
  override def poll: A = item.getAndSet(None).getOrElse(null.asInstanceOf[A])
  def remove: A = item.getAndSet(None).getOrElse(throw new NoSuchElementException)

  def offer(p1: A): Boolean = {
    item.set(Some(p1))
    true
  }
  def add(p1: A): Boolean = {
    item.set(Some(p1))
    true
  }
}
