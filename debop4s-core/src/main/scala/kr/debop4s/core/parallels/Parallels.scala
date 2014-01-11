package kr.debop4s.core.parallels

import java.util.concurrent.{Callable, ThreadLocalRandom}
import kr.debop4s.core.logging.Logger


/**
 * 작업을 병렬로 처리하게 해주는 Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:18
 */
object Parallels {

    implicit lazy val log = Logger(getClass)

    lazy val random = ThreadLocalRandom.current()
    lazy val processCount = Runtime.getRuntime.availableProcessors()
    lazy val workerCount = processCount * 2

    def mapAsOrdered[T <: Ordered[T], V](items: Iterable[T], mapper: T => V): Iterable[V] = {
        items.par
            .map(x => (x, mapper(x)))
            .toList
            .sortWith(_._1 < _._1)
            .map(_._2)
    }

    def mapAsParallel[T, V](items: Iterable[T], mapper: T => V): Iterable[V] =
        items.par.map(item => mapper(item)).toList


    def run(count: Int)(r: Runnable) {
        assert(r != null)
        run(Range(0, count))(r)
    }

    def run(range: Range)(r: Runnable) {
        assert(range != null)
        assert(r != null)
        range.par.foreach(_ => r.run())
    }

    def runAction(count: Int)(block: => Unit) {
        runAction(Range(0, count))(block)
    }

    def runAction(range: Range)(block: => Unit) {
        assert(range != null)
        range.par.foreach(_ => block)
    }

    def runAction1(count: Int)(block: Int => Unit) {
        runAction1(Range(0, count))(block)
    }

    def runAction1(range: Range)(block: Int => Unit) {
        assert(range != null)
        range.par.foreach(i => block(i))
    }

    def runEach[V](elements: Iterable[V])(block: V => Unit) {
        assert(elements != null)
        elements.par.foreach(block)
    }

    def call[V](count: Int)(callable: Callable[V]) {
        assert(callable != null)
        call[V](Range(0, count))(callable)
    }

    def call[V](range: Range)(callable: Callable[V]) {
        assert(range != null)
        range.par.foreach(_ => callable.call())
    }

    def callFunction[V](count: Int)(func: () => V): List[V] = {
        assert(func != null)
        callFunction(Range(0, count))(func)
    }

    def callFunction[V](range: Range)(func: () => V): List[V] = {
        assert(func != null)
        range.par.map(_ => func()).toList
    }

    def callFunction1[V](count: Int)(func: Int => V): List[V] = {
        assert(func != null)
        callFunction1(Range(0, count))(func)
    }

    def callFunction1[V](range: Range)(func: Int => V): List[V] = {
        assert(func != null)
        range.par.map(i => func(i)).toList
    }

    def callEach[S, T](elements: Iterable[S])(func: S => T): List[T] = {
        assert(func != null)
        elements.par.map(x => func(x)).toList
    }
}
