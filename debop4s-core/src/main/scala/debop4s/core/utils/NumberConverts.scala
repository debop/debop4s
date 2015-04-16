package debop4s.core.utils

import java.util.Date

import debop4s.core.Logging
import org.joda.time.DateTime

import scala.util.control.NonFatal

/**
 * 수형 변환
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 10:46
 */
object NumberConverts extends Logging {

  def getValue(v: java.lang.Integer): Int = {
    if (v != null) v.intValue() else 0
  }
  def getValue(v: java.lang.Long): Long = {
    if (v != null) v.longValue() else 0L
  }
  def getValue(v: java.lang.Float): Float = {
    if (v != null) v.floatValue() else 0f
  }
  def getValue(v: java.lang.Double): Double = {
    if (v != null) v.doubleValue() else 0D
  }
  def getValue(v: java.lang.Short): Short = {
    if (v != null) v.shortValue() else 0
  }

  def getInt(x: Object) = toInt(x)
  def getLong(x: Object) = toLong(x)
  def getDouble(x: Object) = toDouble(x)
  def getFloat(x: Object) = toFloat(x)
  def getShort(x: Object) = toShort(x)


  @inline
  def toByte(x: Any): Byte = {
    try {
      x match {
        case null => 0
        case None => 0
        case x: java.lang.Number => x.byteValue()
        case x: Char => x.toByte
        case x: Byte => x
        case x: Short => x.toByte
        case x: Int => x.toByte
        case x: Long => x.toByte
        case x: Float => x.toByte
        case x: Double => x.toByte
        case _ => x.toString.toByte
      }
    } catch {
      case NonFatal(e) => 0
    }
  }

  @inline
  def toShort(x: Any): Short = {
    try {
      x match {
        case null => 0
        case None => 0
        case x: java.lang.Number => x.shortValue()
        case x: Char => x.toShort
        case x: Byte => x.toShort
        case x: Short => x
        case x: Int => x.toShort
        case x: Long => x.toShort
        case x: Float => x.toShort
        case x: Double => x.toShort
        case _ => x.toString.toShort
      }
    } catch {
      case NonFatal(e) => 0
    }
  }

  @inline
  def toInt(x: Any): Int = {
    try {
      x match {
        case null => 0
        case None => 0
        case x: java.lang.Number => x.intValue()
        case x: Char => x.toInt
        case x: Byte => x.toInt
        case x: Short => x.toInt
        case x: Int => x
        case x: Long => x.toInt
        case x: Float => x.toInt
        case x: Double => x.toInt
        case _ => x.toString.toInt
      }
    } catch {
      case NonFatal(e) => 0
    }
  }

  @inline
  def toLong(x: Any): Long = {
    try {
      x match {
        case null => 0
        case None => 0
        case x: java.lang.Number => x.longValue()
        case x: Char => x.toLong
        case x: Byte => x.toLong
        case x: Short => x.toLong
        case x: Int => x.toLong
        case x: Long => x
        case x: Float => x.toLong
        case x: Double => x.toLong
        case _ => x.toString.toLong
      }
    } catch {
      case NonFatal(e) => 0
    }
  }

  @inline
  def toFloat(v: Any): Float = {
    try {
      v match {
        case null => 0
        case None => 0
        case x: java.lang.Number => x.floatValue()
        case x: Char => x.toFloat
        case x: Byte => x.toFloat
        case x: Short => x.toFloat
        case x: Int => x.toFloat
        case x: Long => x.toFloat
        case x: Float => x
        case x: Double => x.toFloat
        case _ => v.toString.toFloat
      }
    } catch {
      case NonFatal(e) => 0
    }
  }

  @inline
  def toDouble(v: Any): Double = {
    try {
      v match {
        case null => 0
        case None => 0
        case x: java.lang.Number => x.doubleValue()
        case x: Char => x.toDouble
        case x: Byte => x.toDouble
        case x: Short => x.toDouble
        case x: Int => x.toDouble
        case x: Long => x.toDouble
        case x: Float => x.toDouble
        case x: Double => x
        case _ => v.toString.toDouble
      }
    } catch {
      case NonFatal(e) => 0
    }
  }

  def toString(x: Any): String = if (x == null) "" else x.toString

  def toDateTime(x: Any, defaultValue: DateTime = null): DateTime = {
    try {
      x match {
        case null => defaultValue
        case None => defaultValue
        case x: java.lang.Number => new DateTime(x.longValue())
        case x: Char => new DateTime(x.toLong)
        case x: Byte => new DateTime(x.toLong)
        case x: Int => new DateTime(x.toLong)
        case x: Long => new DateTime(x)
        case x: Float => new DateTime(x.toLong)
        case x: Double => new DateTime(x.toLong)
        case x: Date => new DateTime(x)
        case x: CharSequence => DateTime.parse(x.toString)
      }
    } catch {
      case NonFatal(e) => defaultValue
    }
  }

}

