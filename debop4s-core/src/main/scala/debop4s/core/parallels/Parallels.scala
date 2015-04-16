package debop4s.core.parallels

import java.util.concurrent.{Callable, ThreadLocalRandom}

import debop4s.core.JAction1
import org.slf4j.LoggerFactory

import scala.collection.Seq


/**
 * 작업을 병렬로 처리하게 해주는 Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:18
 */
object Parallels {

  private lazy val LOG = LoggerFactory.getLogger(getClass)

  lazy val random = ThreadLocalRandom.current()
  lazy val processCount = Runtime.getRuntime.availableProcessors()
  lazy val workerCount = processCount * 2

  def mapAsOrdered[T <: Ordered[T], V](items: Iterable[T], mapper: T => V): Seq[V] = {
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

  def run(start: Int, end: Int, step: Int = 1)(r: Runnable) {
    assert(r != null)
    run(Range(start, end, step))(r)
  }

  def run(range: Seq[Int])(r: Runnable) {
    assert(range != null)
    assert(r != null)
    range.par.foreach(_ => r.run())
  }

  def run(count: Int, action1: JAction1[java.lang.Integer]) {
    runAction1(Range(0, count)) { i =>
      action1.perform(i)
    }
  }

  def run(start: Int, end: Int, action1: JAction1[java.lang.Integer]) {
    runAction1(Range(start, end)) { i =>
      action1.perform(i)
    }
  }

  def runAction(count: Int)(block: => Unit) {
    runAction(Range(0, count))(block)
  }

  def runAction(range: Seq[Int])(block: => Unit) {
    assert(range != null)
    range.par.foreach(_ => block)
  }

  def runAction1(count: Int)(block: Int => Unit) {
    runAction1(Range(0, count))(block)
  }

  def runAction1(count: Int, action1: JAction1[java.lang.Integer]) {
    runAction1(Range(0, count)) { i =>
      action1.perform(i)
    }
  }

  def runAction1(start: Int, end: Int, action1: JAction1[java.lang.Integer]) {
    runAction1(Range(start, end)) { i =>
      action1.perform(i)
    }
  }
  def runAction1(start: Int, end: Int, step: Int, action1: JAction1[java.lang.Integer]) {
    runAction1(Range(start, end, step)) { i =>
      action1.perform(i)
    }
  }

  def runAction1(range: Seq[Int])(block: Int => Unit) {
    assert(range != null)
    range.par.foreach {
      i => block(i)
    }
  }

  def runEach[V](elements: Iterable[V])(block: V => Unit) {
    assert(elements != null)
    elements.par.foreach(block)
  }

  /**
   * 컬렉션을 지정된 갯수로 나누어서 작업합니다.
   */
  def runEach[V](elements: Iterable[V], size: Int = workerCount)(block: V => Unit) {
    assert(elements != null)
    elements.grouped(size).foreach(_.foreach(block))
  }

  def call[V](count: Int)(callable: Callable[V]): Seq[V] = {
    assert(callable != null)
    call[V](Range(0, count))(callable)
  }
  def call[V](start: Int, end: Int, step: Int)(callable: Callable[V]): Seq[V] = {
    assert(callable != null)
    call[V](Range(start, end, step))(callable)
  }

  def call[V](range: Seq[Int])(callable: Callable[V]): Seq[V] = {
    assert(range != null)
    range.par.map(_ => callable.call()).seq
  }

  def callFunction[V](count: Int)(func: () => V): Seq[V] = {
    assert(func != null)
    callFunction(Range(0, count))(func)
  }

  def callFunction[V](start: Int, end: Int, step: Int)(func: () => V): Seq[V] = {
    assert(func != null)
    callFunction(Range(start, end, step))(func)
  }

  def callFunction[V](range: Seq[Int])(func: () => V): Seq[V] = {
    assert(func != null)
    range.par.map(_ => func()).seq
  }

  def callFunction1[V](count: Int)(func: Int => V): Seq[V] = {
    assert(func != null)
    callFunction1(Range(0, count))(func).toSeq
  }

  def callFunction1[V](start: Int, end: Int, step: Int)(func: Int => V): Seq[V] = {
    assert(func != null)
    callFunction1(Range(start, end, step))(func).toSeq
  }

  def callFunction1[V](range: Seq[Int])(func: Int => V): Seq[V] = {
    assert(func != null)
    range.par.map(i => func(i)).seq.toSeq
  }

  def callEach[S, T](elements: Iterable[S])(func: S => T): Seq[T] = {
    assert(func != null)
    elements.par.map(x => func(x)).seq.toSeq
  }

  def callEach[S, T](elements: Iterable[S], size: Int)(func: S => T): Seq[(S, T)] = {
    elements.grouped(size).map(_.map(s => (s, func(s)))).flatten.toSeq
  }
}
