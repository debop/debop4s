package debop4s.core.parallels

import java.util.concurrent.{Callable, ThreadLocalRandom}

import debop4s.core.JAction1

import scala.collection.Seq


/**
 * 작업을 병렬로 처리하게 해주는 Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:18
 */
object Parallels {

  private[this] lazy val random: ThreadLocalRandom = ThreadLocalRandom.current()
  private[this] lazy val processCount: Int = Runtime.getRuntime.availableProcessors()
  private[this] lazy val workerCount: Int = processCount * 2

  def mapAsOrdered[@miniboxed T <: Ordered[T], @miniboxed V](items: Iterable[T], mapper: T => V): Seq[V] = {
    items.par
    .map(x => (x, mapper(x)))
    .toList
    .sortBy(_._1)
    .map(_._2)
  }

  def mapAsParallel[@miniboxed T, @miniboxed V](items: Iterable[T], mapper: T => V): Iterable[V] =
    items.par.map(item => mapper(item)).toList


  def run(count: Int)(r: Runnable): Unit = {
    require(r != null)
    run(Range(0, count))(r)
  }

  def run(start: Int, end: Int, step: Int = 1)(r: Runnable): Unit = {
    require(r != null)
    run(Range(start, end, step))(r)
  }

  def run(range: Seq[Int])(r: Runnable): Unit = {
    require(range != null)
    require(r != null)
    range.par.foreach(_ => r.run())
  }

  def run(count: Int, action1: JAction1[java.lang.Integer]): Unit = {
    runAction1(Range(0, count)) { i =>
      action1.perform(i)
    }
  }

  def run(start: Int, end: Int, action1: JAction1[java.lang.Integer]): Unit = {
    runAction1(Range(start, end)) { i =>
      action1.perform(i)
    }
  }

  def runAction(count: Int)(block: => Unit): Unit = {
    runAction(Range(0, count))(block)
  }

  def runAction(range: Seq[Int])(block: => Unit): Unit = {
    require(range != null)
    range.par.foreach(_ => block)
  }

  def runAction1(count: Int)(block: Int => Unit): Unit = {
    runAction1(Range(0, count))(block)
  }

  def runAction1(count: Int, action1: JAction1[java.lang.Integer]): Unit = {
    runAction1(Range(0, count)) { i =>
      action1.perform(i)
    }
  }

  def runAction1(start: Int, end: Int, action1: JAction1[java.lang.Integer]): Unit = {
    runAction1(Range(start, end)) { i =>
      action1.perform(i)
    }
  }
  def runAction1(start: Int, end: Int, step: Int, action1: JAction1[java.lang.Integer]): Unit = {
    runAction1(Range(start, end, step)) { i =>
      action1.perform(i)
    }
  }

  def runAction1(range: Seq[Int])(block: Int => Unit): Unit = {
    require(range != null)
    range.par.foreach {
      i => block(i)
    }
  }

  def runEach[@miniboxed V](elements: Iterable[V])(block: V => Unit): Unit = {
    require(elements != null)
    elements.par.foreach(block)
  }

  /**
   * 컬렉션을 지정된 갯수로 나누어서 작업합니다.
   */
  def runEach[@miniboxed V](elements: Iterable[V], size: Int = workerCount)(block: V => Unit) {
    require(elements != null)
    elements.grouped(size).foreach(_.foreach(block))
  }

  def call[@miniboxed V](count: Int)(callable: Callable[V]): Seq[V] = {
    require(callable != null)
    call[V](Range(0, count))(callable)
  }
  def call[@miniboxed V](start: Int, end: Int, step: Int)(callable: Callable[V]): Seq[V] = {
    require(callable != null)
    call[V](Range(start, end, step))(callable)
  }

  def call[@miniboxed V](range: Seq[Int])(callable: Callable[V]): Seq[V] = {
    require(range != null)
    range.par.map(_ => callable.call()).seq
  }

  def callFunction[@miniboxed V](count: Int)(func: () => V): Seq[V] = {
    require(func != null)
    callFunction(Range(0, count))(func)
  }

  def callFunction[@miniboxed V](start: Int, end: Int, step: Int)(func: () => V): Seq[V] = {
    require(func != null)
    callFunction(Range(start, end, step))(func)
  }

  def callFunction[@miniboxed V](range: Seq[Int])(func: () => V): Seq[V] = {
    require(func != null)
    range.par.map(_ => func()).seq
  }

  def callFunction1[@miniboxed V](count: Int)(func: Int => V): Seq[V] = {
    require(func != null)
    callFunction1(Range(0, count))(func).toSeq
  }

  def callFunction1[@miniboxed V](start: Int, end: Int, step: Int)(func: Int => V): Seq[V] = {
    require(func != null)
    callFunction1(Range(start, end, step))(func).toSeq
  }

  def callFunction1[@miniboxed V](range: Seq[Int])(func: Int => V): Seq[V] = {
    require(func != null)
    range.par.map(i => func(i)).seq.toSeq
  }

  def callEach[@miniboxed S, @miniboxed T](elements: Iterable[S])(func: S => T): Seq[T] = {
    require(func != null)
    elements.par.map(x => func(x)).seq.toSeq
  }

  def callEach[@miniboxed S, @miniboxed T](elements: Iterable[S], size: Int)(func: S => T): Seq[(S, T)] = {
    elements.grouped(size).map(_.map(s => (s, func(s)))).flatten.toSeq
  }
}
