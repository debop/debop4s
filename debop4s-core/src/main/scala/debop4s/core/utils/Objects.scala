package debop4s.core.utils

import java.util.Comparator

import debop4s.core._

import scala.annotation.varargs

/**
 * 모든 Class 에 대한 Helper class
 * @author Sunghyouk Bae
 */
object Objects extends Logging {

  def equals(a: Any, b: Any): Boolean = {
    (a == b) || (a != null && a.equals(b))
  }

  def hashCode(o: Any): Int = o match {
    case null => 0
    case None => 0
    case _ => o.hashCode()
  }

  @varargs
  def hash(values: Any*) = Hashs.compute(values: _*)

  def toString(o: Any) = o.asString

  def toString(o: Any, nullDefault: String) = o.asString

  def compare[T](a: T, b: T, c: Comparator[_ >: T]): Int = {
    if (a == b) 0
    else c.compare(a, b)
  }

  def requireNonNull[T](obj: T): T = obj match {
    case null => throw new NullPointerException
    case _ => obj
  }

  def requireNonNull[T](obj: T, message: String): T = obj match {
    case null => throw new NullPointerException(message)
    case _ => obj
  }
}