package debop4s.core

import java.util.UUID
import java.util.concurrent.Callable
import org.slf4j.LoggerFactory
import scala.collection.mutable

/**
 * Thread Context 별로 Local Storage를 제공하는 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 9:36
 */
object Local {

  private lazy val log = LoggerFactory.getLogger(getClass)

  type Context = mutable.LinkedHashMap[Any, Any]

  private[this] lazy val threadLocal = new ThreadLocal[Context]() {
    override def initialValue(): Context = {
      new Context()
    }
  }

  private def getStorage: Context = threadLocal.get()

  def save(): Context = threadLocal.get().clone()

  def restore(saved: Context): Unit = {
    threadLocal.set(saved)
  }

  def get[T](key: Any): Option[T] = {
    getStorage.get(key) match {
      case Some(x) => Some(x.asInstanceOf[T])
      case _ => None
    }
  }

  def put(key: Any, value: Any) {
    log.trace(s"put to Local hashmap. key=$key, value=$value")
    getStorage.update(key, value)
  }

  def put[T](key: Any, optValue: Option[T]) {
    getStorage(key) = optValue
  }

  def clearAll() {
    log.debug(s"clear local storage.")
    getStorage.clear()
  }

  def getOrCreate[T](key: Any, factory: => T): Option[T] = {
    getStorage.getOrElseUpdate(key, factory)
    //    if (!getStorage.contains(key)) {
    //      assert(factory != null)
    //      val result: T = factory
    //      put(key, result)
    //    }
    get[T](key)
  }

  def getOrCreate[T](key: Any, factory: Callable[T]): Option[T] = synchronized {
    getStorage.getOrElseUpdate(key, factory.call())
    //    if (!getStorage.contains(key)) {
    //      assert(factory != null)
    //      put(key, factory.call())
    //    }
    get[T](key)
  }
}

final class Local[T] {
  private[this] val key = UUID.randomUUID()

  def apply(): Option[T] = Local.get[T](key)

  def set(value: T) {
    Local.put(key, value)
  }

  def update(value: T) {
    set(value)
  }

  def clear() {
    Local.getStorage.remove(key)
  }
}
