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

    lazy val log = LoggerFactory.getLogger(getClass)

    private lazy val threadLocal = new ThreadLocal[mutable.LinkedHashMap[Any, Any]]() {
        override def initialValue(): mutable.LinkedHashMap[Any, Any] = {
            new mutable.LinkedHashMap[Any, Any]()
        }
    }

    private def getStorage: mutable.LinkedHashMap[Any, Any] = threadLocal.get()

    def save(): mutable.LinkedHashMap[Any, Any] = threadLocal.get()

    def restore(saved: mutable.LinkedHashMap[Any, Any]): Unit = threadLocal.set(saved)

    def get[T](key: Any): Option[T] = {
        getStorage.get(key) match {
            case Some(x) => Some(x.asInstanceOf[T])
            case _ => None
        }
    }

    def put(key: Any, value: Any) {
        assert(key != null)
        getStorage.update(key, value)
    }

    def put[T](key: Any, optValue: Option[T]) {
        getStorage(key) = optValue
    }

    def clearAll() {
        getStorage.clear()
    }

    def getOrCreate[T](key: Any, factory: => T): Option[T] = {
        if (!getStorage.contains(key)) {
            assert(factory != null)
            val result: T = factory
            put(key, result)
        }
        get[T](key)
    }

    def getOrCreate[T](key: Any, factory: Callable[T]): Option[T] = synchronized {
        if (!getStorage.contains(key)) {
            assert(factory != null)
            put(key, factory.call())
        }
        get[T](key)
    }
}

final class Local[T] {
    private[this] val key = UUID.randomUUID()

    def apply() = Local.get[T](key)

    def set(optValue: Option[T]) {
        Local.put(key, optValue)
    }

    def update(value: T) {
        set(Some(value))
    }

    def clear() {
        set(None)
    }
}
