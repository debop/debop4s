package debop4s.core.retry

import scala.annotation.implicitNotFound
import scala.util.Try

/**
 * 성공 여부를 판단합니다.
 * @author sunghyouk.bae@gmail.com
 */
@implicitNotFound("Cannot find an implicit retry.Success for the given type of Future, either require one yourself or import retry.Success._")
class Successful[-T](val predicate: T => Boolean) {

  def or[R <: T](that: Successful[R]): Successful[R] = Successful[R](v => predicate(v) || that.predicate(v))
  def or[R <: T](that: => Boolean): Successful[R] = or(Successful[R](_ => that))

  def and[R <: T](that: Successful[R]): Successful[R] = Successful[R](v => predicate(v) && that.predicate(v))
  def and[R <: T](that: => Boolean): Successful[R] = and(Successful[R](_ => that))
}

object Successful {

  implicit def either[A, B]: Successful[Either[A, B]] =
    Successful(_.isRight)

  implicit def option[A]: Successful[Option[A]] =
    Successful(_.isDefined)

  implicit def tried[A]: Successful[Try[A]] =
    Successful(_.isSuccess)

  val always = Successful(Function.const(true))
  val never = Successful(Function.const(false))


  def apply[T](predicate: T => Boolean) = new Successful[T](predicate)
}
