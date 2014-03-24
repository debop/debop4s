package com.github.debop4s.core

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

    def get[T](key: Any): Option[T] = getStorage.get(key) match {
        case Some(x) => Some(x.asInstanceOf[T])
        case _ => None
    }

    def put(key: Any, value: Any) {
        assert(key != null)
        log.trace(s"put: Local 저장소에 key=[$key], value=[$value]를 저장합니다.")
        getStorage.update(key, value)
    }

    def clear() {
        log.trace("clear: Local 저장소를 clear 합니다.")
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
