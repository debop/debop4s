package debop4s

import debop4s.core.utils.Strings
import java.util.concurrent.TimeUnit
import org.joda.time.base.AbstractInstant
import org.joda.time.{ Duration => JDuration }
import scala.concurrent._
import scala.concurrent.duration.{FiniteDuration, Duration}


/**
 * debop4s.core.package
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:03
 */
package object core {

  val TimeConversions = debop4s.core.conversions.time
  val StorageConversions = debop4s.core.conversions.storage

  // implicit val executor = ExecutionContext.fromExecutor(scala.concurrent.ExecutionContext.Implicits.global)

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
    def words: Array[String] = s split " "

    def isWhitespace: Boolean = Strings.isWhitespace(s)

    def ellipseChar(maxLength: Int = ElipsisLength) = Strings.ellipsisChar(s, maxLength)

    def ellipseFirst(maxLength: Int = ElipsisLength) = Strings.ellipsisFirst(s, maxLength)

    def ellipsePath(maxLength: Int = ElipsisLength) = Strings.ellipsisPath(s, maxLength)

    def toUtf8Bytes = Strings.getUtf8Bytes(s)
  }

  implicit class ByteExtensions(bytes: Array[Byte]) {
    def toUtf8String = {
      Strings.getUtf8String(bytes)
    }
  }

  // implicit val defaultDuration: Duration = Duration(60, TimeUnit.MINUTES)

  implicit class AwaitableExtensions[T](task: Awaitable[T]) {

    implicit val defaultDuration: Duration = FiniteDuration(60, TimeUnit.MINUTES)

    def await(implicit atMost: Duration = defaultDuration): Unit = {
      Await.ready(task, atMost)
    }

    def asyncValue(implicit atMost: Duration = defaultDuration): T = {
      Await.result[T](task, atMost)
    }
  }
}