package debop4s

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Callable

import debop4s.core.utils.Strings
import org.joda.time.{DateTime, Duration => JDuration}

import scala.util.Try
import scala.util.control.NonFatal


/**
 * debop4s.core.package
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:03
 */
package object core {

  // private[this] lazy val log = LoggerFactory.getLogger("debop4s.core")

  val TimeConversions = debop4s.core.conversions.time
  val StorageConversions = debop4s.core.conversions.storage

  val ShouldNotBeNull = "[%s] should not be null."
  val ShouldBeNull = "[%s] should be null."

  val ShouldBeEquals = "%s=[%s] should be equals expected=[%s]"
  val ShouldNotBeEquals = "%s=[%s] should not be equals expected=[%s]"
  val ShouldBeEmptyString = "[%s] should be empty string."
  val ShouldNotBeEmptyString = "[%s] should not be empty string."

  val ShouldBeWhiteSpace = "[%s] should be white space."
  val ShouldNotBeWhiteSpace = "[%s] should not be white space."

  val ShouldBeNumber = "[%s] should be number."

  val ShouldBePositiveNumber = "[%s] should be positive number"
  val ShouldNotBePositiveNumber = "[%s] should not be positive number"

  val ShouldBeNegativeNumber = "[%s] should be negative number"
  val ShouldNotBeNegativeNumber = "[%s] should not be negative number"

  val ShouldBeInRangeInt = "%s[%d]이 범위 [%d, %d) 를 벗어났습니다."
  val ShouldBeInRangeDouble = "%s[%f]이 범위 [%f, %f) 를 벗어났습니다."

  val ElipsisLength = 80: Int

  implicit class StringExtensions(s: String) {

    def words: Array[String] = s.split(" ")
    def isWhitespace: Boolean = Strings.isWhitespace(s)

    def ellipseChar(maxLength: Int = ElipsisLength): String = Strings.ellipsisChar(s, maxLength)
    def ellipseFirst(maxLength: Int = ElipsisLength): String = Strings.ellipsisFirst(s, maxLength)
    def ellipsePath(maxLength: Int = ElipsisLength): String = Strings.ellipsisPath(s, maxLength)
    def toUtf8Bytes: Array[Byte] = Strings.getUtf8Bytes(s)
  }

  implicit class ByteExtensions(bytes: Array[Byte]) {
    def toUtf8String: String = Strings.getUtf8String(bytes)
  }

  /**
   * 지정한 코드 블럭을 `Runnable` 인스턴스로 빌드합니다.
   * {{{
   *     import debop4s.core.utils.Threads._
   *     val runnable = {
   *      // some code...
   *     }.makeRunnable
   *     Thread.start(runnable)
   * }}}
   * @param action 실핼할 코드 블럭
   * @return `Runnable` instance
   */
  implicit def runnable(action: => Unit): Runnable = {
    new Runnable {
      override def run(): Unit = action
    }
  }

  /**
   * 지정한 함수 `func` 을 수행하는 `Callable` 인스턴스를 빌드합니다.
   * @param func   수행할 함수
   * @tparam T 함수가 반환할 값의 수형
   * @return 함수 실행 반환 값
   */
  implicit def callable[@miniboxed T](func: => T): Callable[T] = {
    new Callable[T] {
      override def call(): T = func
    }
  }

  /**
   * 지정한 메소드 `action` 을 수행하는 `Thread` 를 생성합니다.
   * @param block  수행할 코드 블럭
   * @return
   */
  implicit def createThread(block: => Unit): Thread =
    new Thread(runnable { block })

  /**
   * 지정한 메소드 `action` 을 수행하는 `Thread` 를 생성하고, 시작합니다.
   * @param block 스레드로 수행할 블럭
   * @return
   */
  implicit def startThread(block: => Unit, daemon: Boolean = true): Thread = {
    val thread = createThread { block }
    thread.setDaemon(daemon)
    thread.start()
    thread
  }

  /**
   * `close` 메소드를 가진 객체에 대해 메소드 `func` 를 실행한 후 `close` 메소드를 호출합니다.
   */
  implicit def using[@miniboxed A <: {def close() : Unit}, @miniboxed B](closable: A)(func: A => B): B = {
    require(closable != null)
    require(func != null)
    try {
      func(closable)
    } finally {
      Try { closable.close() }
    }
  }

  /**
   * 인스턴스가 null이면 None을 반환하고, 값이 있으면 Some(v)를 반환합니다.
   */
  def toOption[@miniboxed T](v: T): Option[T] =
    v match {
      case null => None
      case None => None
      case x: Option[Any] => x.asInstanceOf[Option[T]]
      case _ => Some(v)
    }

  /**
   * 지정한 객체를 암묵적으로 변환하는 클래스입니다.
   * @param x 변환할 객체
   */
  implicit class NumberExtensions(val x: Any) {

    /** 객체를 `Char` 수형으로 변환합니다. */
    def asChar: Char = x.asByte.toChar

    /** 객체를 `Byte` 수형으로 변환합니다. */
    def asByte: Byte = {
      try {
        x match {
          case Some(a: Any) => a.asByte
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

    /** 객체를 short 수형으로 변환합니다. */
    def asShort: Short = {
      try {
        x match {
          case Some(a: Any) => a.asShort
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

    /** 객체를 `Int` 수형으로 변환합니다. */
    def asInt: Int = {
      try {
        x match {
          case Some(a: Any) => a.asInt
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

    /** 객체를 `Long` 수형으로 변환합니다. */
    def asLong: Long = {
      try {
        x match {
          case Some(a: Any) => a.asLong
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
        case NonFatal(e) => 0L
      }
    }

    /** 객체를 `Float` 수형으로 변환합니다. */
    def asFloat: Float = {
      try {
        x match {
          case Some(a: Any) => a.asFloat
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
          case _ => x.toString.toFloat
        }
      } catch {
        case NonFatal(e) => 0F
      }
    }
    /** 객체를 `Double` 수형으로 변환합니다. */
    def asDouble: Double = {
      try {
        x match {
          case Some(a: Any) => a.asDouble
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
          case _ => x.toString.toDouble
        }
      } catch {
        case NonFatal(e) => 0D
      }
    }

    /** 객체를 `String` 수형으로 변환합니다. */
    def asString: String =
      x match {
        case Some(a: Any) => a.toString
        case None => None.toString
        case null => Strings.NULL_STR
        case _ => x.toString
      }

    /** 객체를 `DateTime` 수형으로 변환합니다. */
    def asDateTime(defaultValue: DateTime = null): DateTime = {
      try {
        x match {
          case Some(a: Any) => a.asDateTime(defaultValue)
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
          case x: DateTime => x
          case x: CharSequence => DateTime.parse(x.toString)
        }
      } catch {
        case NonFatal(e) => defaultValue
      }
    }

    private[this] lazy val sdf = new SimpleDateFormat()

    /** 객체를 `Date` 수형으로 변환합니다. */
    def asDate(defaultValue: Date = null): Date = {
      try {
        x match {
          case Some(a: Any) => a.asDate(defaultValue)
          case null => defaultValue
          case None => defaultValue
          case x: java.lang.Number => new Date(x.longValue())
          case x: Char => new Date(x.toLong)
          case x: Byte => new Date(x.toLong)
          case x: Int => new Date(x.toLong)
          case x: Long => new Date(x)
          case x: Float => new Date(x.toLong)
          case x: Double => new Date(x.toLong)
          case x: Date => x
          case x: DateTime => x.toDate
          case x: CharSequence => sdf.parse(x.toString)
        }
      } catch {
        case NonFatal(e) => defaultValue
      }
    }

    /** 객체를 `Class` 수형으로 변환합니다. */
    def asJavaClass: Class[_] = x match {
      case null => throw new NullPointerException()
      case None => None.getClass
      case scala.Boolean => java.lang.Boolean.TYPE
      case scala.Char => java.lang.Character.TYPE
      case scala.Byte => java.lang.Byte.TYPE
      case scala.Short => java.lang.Short.TYPE
      case scala.Int => java.lang.Integer.TYPE
      case scala.Long => java.lang.Long.TYPE
      case scala.Float => java.lang.Float.TYPE
      case scala.Double => java.lang.Double.TYPE
      case _ => x.getClass
    }
  }
}