package debop4s.core.utils

import scala.collection.immutable.Stream._

/**
 * Scala Stream 에 대한 Utility Object
 * @author sunghyouk.bae@gmail.com
 */
object ScalaStreams {

  def constant[@miniboxed A](a: A): Stream[A] = unfold(a)(a => Some(a, a)) // Stream.cons(a, constant(a))

  def from(n: Int): Stream[Int] = unfold(n)(n => Some(n, n + 1)) // Stream.cons(n, from(n + 1))

  def from(n: Long): Stream[Long] = unfold(n)(n => Some(n, n + 1)) // Stream.cons(n, from(n + 1L))

  def fibonacci: Stream[Int] = {
    unfold((0, 1)) {
      case (f0, f1) => Some((f0, (f1, f0 + f1)))
    }
  }

  @inline
  def unfold[@miniboxed A, @miniboxed S](z: S)(f: S => Option[(A, S)]): Stream[A] = {
    f(z) match {
      case Some((h, s)) => cons(h, unfold(s)(f))
      case None => empty[A]
    }
  }

}
