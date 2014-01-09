package kr.debop4s.core

import java.util.concurrent.Callable
import kr.debop4s.core.logging.Logger
import scala.collection.mutable

/**
 * Thread Context 별로 Local Storage를 제공하는 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 9:36
 */
object Local {

    lazy val log = Logger(getClass)

    private lazy val threadLocal = new ThreadLocal[mutable.LinkedHashMap[Any, Any]]() {
        override def initialValue(): mutable.LinkedHashMap[Any, Any] = {
            new mutable.LinkedHashMap[Any, Any]()
        }
    }

    private def getStorage: mutable.LinkedHashMap[Any, Any] = threadLocal.get()

    def get(key: Any): Any = getStorage.getOrElse(key, null)

    def get[T](key: Any, clazz: Class[T]): T = get(key).asInstanceOf[T]

    def put(key: Any, value: Any) {
        assert(key != null)
        log.trace(s"Local 저장소에 key=[$key], value=[$value]를 저장합니다.")
        getStorage.update(key, value)
    }

    def clear() {
        log.trace("Local 저장소를 clear 합니다.")
        getStorage.clear()
    }

    def getOrCreate[T](key: Any, factory: () => T): T = {
        if (!getStorage.contains(key)) {
            assert(factory != null)
            val result: T = factory()
            put(key, result)
        }
        get(key).asInstanceOf[T]
    }

    def getOrCreate[T](key: Any, factory: Callable[T]): T = synchronized {
                                                                             if (!getStorage.contains(key)) {
                                                                                 assert(factory != null)
                                                                                 put(key, factory.call())
                                                                             }
                                                                             get(key).asInstanceOf[T]
                                                                         }
}
