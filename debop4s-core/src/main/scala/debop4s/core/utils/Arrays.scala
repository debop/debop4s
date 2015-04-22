package debop4s.core.utils

import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.util.Random

/**
 * Array 관련 Utility class
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:10
 */
object Arrays {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)
  private[this] lazy val RANDOM: Random = new Random(System.currentTimeMillis())

  val EMPTY_BYTE_ARRAY: Array[Byte] = Array[Byte]()

  def isEmpty[T](array: Array[T]): Boolean =
    (array eq null) || (array.length == 0)

  def isEmpty[T](iterable: Iterable[T]): Boolean =
    (iterable eq null) || (!iterable.iterator.hasNext)

  def contains[T](array: Array[T], target: T): Boolean =
    if (isEmpty(array)) false
    else array.contains(target)

  def indexOf[T](array: Array[T], target: T): Int =
    if (isEmpty(array)) -1
    else array.indexOf(target)

  def lastIndexOf[T](array: Array[T], target: T): Int =
    array.lastIndexOf(target)

  def asArray[T: ClassTag](iterable: Iterable[T]): Array[T] = iterable.toArray

  def asString[T](iterable: Iterable[T]): String = iterable.mkString(",")

  def mkArray[T: ClassTag](elems: T*): Array[T] = Array[T](elems: _*)


  def getRandomBytes(numBytes: Int): Array[Byte] = {
    val bytes = new Array[Byte](numBytes)
    RANDOM.nextBytes(bytes)
    bytes
  }

  def fill[T](a: Array[T], value: T): Unit = {
    for (i <- 0 until a.length) {
      a(i) = value
    }
  }

  def fill[T](a: Array[T], fromInclude: Int, toExclude: Int, value: T): Unit = {
    rangeCheck(a.length, fromInclude, toExclude)
    for (i <- fromInclude until toExclude) {
      a(i) = value
    }
  }

  def copyOf[T: ClassTag](original: Array[T], newLength: Int): Array[T] = {
    require(original != null)

    val copy = new Array[T](newLength)
    Array.copy(original, 0, copy, 0, math.min(original.length, newLength))
    copy
  }

  def copyOfRange[T: ClassTag](src: Array[T], fromInclude: Int, toExclude: Int): Array[T] = {
    val newLength = toExclude - fromInclude
    val copy = new Array[T](newLength)
    System.arraycopy(src, fromInclude, copy, 0, newLength min src.length - fromInclude)
    copy
  }

  private def rangeCheck(length: Int, fromIndex: Int, toIndex: Int): Unit = {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException(s"fromIndex($fromIndex) > toIndex($toIndex)")
    if (fromIndex < 0)
      throw new ArrayIndexOutOfBoundsException(fromIndex)
    if (toIndex > length)
      throw new ArrayIndexOutOfBoundsException(toIndex)
  }

  def toSeq[T](iterable: java.lang.Iterable[_ <: T]): IndexedSeq[T] =
    iterable.asScala.toIndexedSeq

  def toSet[T](iterable: java.lang.Iterable[_ <: T]): Set[T] =
    iterable.asScala.toSet


  def hashCode(a: Array[Any]): Int = Hashs.compute(a: _*)

  @inline
  def equals[T](a: Array[T], b: Array[T]): Boolean = {
    if (a == b)
      return true

    if (a == null || b == null)
      return false
    if (b.length != a.length)
      return false

    // while 구문이 for, foreach보다 훨씬 빠르다.
    val length = a.length
    var i = 0
    while (i < length) {
      val o1 = a(i)
      val o2 = b(i)
      if (!(if (o1 == null) o2 == null else o1.equals(o2)))
        return false
      i += 1
    }
    true
  }
}
