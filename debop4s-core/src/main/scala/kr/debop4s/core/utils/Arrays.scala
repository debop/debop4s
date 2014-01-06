package kr.debop4s.core.utils

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import scala.util.Random

/**
 * Array 관련 Utility class
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:10
 */
object Arrays {

    lazy val RANDOM: Random = new Random(System.currentTimeMillis())

    val EMPTY_BYTE_ARRAY = Array[Byte](0)

    def isEmpty[T](array: Array[T]): Boolean =
        (array eq null) || (array.length == 0)

    def isEmpty[T](iterable: Iterable[T]): Boolean =
        (iterable eq null) || (!iterable.iterator.hasNext)

    def contains[T](array: Array[T], target: T): Boolean = {
        if (isEmpty(array)) false
        else array.contains(target)
    }

    def indexOf[T](array: Array[T], target: T): Int = {
        if (isEmpty(array)) -1
        else array.indexOf(target)
    }

    def lastIndexOf[T](array: Array[T], target: T): Int =
        array.lastIndexOf(target)

    def asArray[T: ClassTag](iterable: Iterable[T]): Array[T] =
        iterable.toArray

    def asArray[T: ClassTag](collection: java.util.Collection[T]): Array[T] =
        collection.toBuffer.toArray

    def asString[T](iterable: Iterable[T]): String = iterable.mkString(",")

    def mkArray[T: ClassTag](elems: T*) = Array[T](elems: _*)


    def getRandomBytes(numBytes: Int): Array[Byte] = {
        val bytes = new Array[Byte](numBytes)
        RANDOM.nextBytes(bytes)
        bytes
    }


    def copyOf[T: ClassTag](original: Array[T], newLength: Int): Array[T] = {
        assert(original != null)
        val copy = new Array[T](newLength)
        Array.copy(original, 0, copy, 0, math.min(original.length, newLength))
        copy
    }

    def fill[T](a: Array[T], fromIndex: Int, toIndex: Int, v: T) {
        rangeCheck(a.length, fromIndex, toIndex)
        for (i <- fromIndex until toIndex)
            a(i) = v
    }

    private def rangeCheck(length: Int, fromIndex: Int, toIndex: Int) {
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")")
        if (fromIndex < 0)
            throw new ArrayIndexOutOfBoundsException(fromIndex)
        if (toIndex > length)
            throw new ArrayIndexOutOfBoundsException(toIndex)
    }

    def toSeq[T](iterable: Iterable[_ <: T]): collection.IndexedSeq[T] = {
        val list = new ArrayBuffer[T]()
        iterable.foreach(x => list.add(x.asInstanceOf[T]))
        list
    }

    def toSet[T](iterable: Iterable[_ <: T]): collection.Set[T] = {
        val set = collection.mutable.HashSet[T]()
        iterable.foreach(x => set += x)
        set
    }

}
