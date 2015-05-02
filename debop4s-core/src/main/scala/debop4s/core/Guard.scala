package debop4s.core

import java.util.Objects

import debop4s.core.utils.Strings

import scala.annotation.varargs

/**
 * Guard 패턴을 이용할 수 있도록 해주는 object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:04
 */
object Guard {

  def firstNotNull[@miniboxed T](first: T, second: T): T = {
    if (!Objects.equals(first, null)) first
    else if (!Objects.equals(second, null)) second
    else throw new IllegalArgumentException("all parameter is null.")
  }

  def toOption[@miniboxed T](v: T): Option[T] =
    if (v == null) None else Some(v)

  def shouldBe(cond: Boolean): Unit = assert(cond)

  def shouldBe(cond: Boolean, msg: String): Unit = assert(cond, msg)

  @varargs
  def shouldBe(cond: Boolean, fmt: String, args: Any*): Unit =
    assert(cond, fmt.format(args: _*))

  def shouldBeEquals(actual: Any, expected: Any, actualName: String): Unit =
    shouldBe(Objects.equals(actual, expected), ShouldBeEquals, actualName, actual, expected)

  def shouldBeNull(arg: AnyRef, argName: String): AnyRef = {
    shouldBe(arg == null, ShouldBeNull, argName)
    arg
  }

  def shouldNotBeNull(arg: AnyRef, argName: String): AnyRef = {
    shouldBe(arg != null, ShouldNotBeNull, argName)
    arg
  }

  def shouldBeEmpty(arg: String, argName: String): String = {
    shouldBe(Strings.isEmpty(arg), ShouldBeEmptyString, argName)
    arg
  }

  def shouldNotBeEmpty(arg: String, argName: String): String = {
    shouldBe(Strings.isNotEmpty(arg), ShouldBeEmptyString, argName)
    arg
  }

  def shouldBeWhitespace(arg: String, argName: String): String = {
    shouldBe(Strings.isWhitespace(arg), ShouldBeWhiteSpace, argName)
    arg
  }

  def shouldNotBeWhitespace(arg: String, argName: String): String = {
    shouldBe(Strings.isNotWhitespace(arg), ShouldNotBeWhiteSpace, argName)
    arg
  }

  def shouldBePositiveNumber(arg: Int, argName: String): Int = {
    shouldBe(arg > 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveNumber(arg: Long, argName: String): Long = {
    shouldBe(arg > 0L, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveNumber(arg: Float, argName: String): Float = {
    shouldBe(arg > 0f, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveNumber(arg: Double, argName: String): Double = {
    shouldBe(arg > 0.0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveNumber(arg: BigDecimal, argName: String): BigDecimal = {
    shouldBe(arg > 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveOrZeroNumber(arg: Int, argName: String): Int = {
    shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveOrZeroNumber(arg: Long, argName: String): Long = {
    shouldBe(arg >= 0L, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveOrZeroNumber(arg: Float, argName: String): Float = {
    shouldBe(arg >= 0f, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveOrZeroNumber(arg: Double, argName: String): Double = {
    shouldBe(arg >= 0.0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBePositiveOrZeroNumber(arg: BigDecimal, argName: String): BigDecimal = {
    shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNotPositiveNumber(arg: Int, argName: String): Int = {
    shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNotPositiveNumber(arg: Long, argName: String): Long = {
    shouldBe(arg <= 0L, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNotPositiveNumber(arg: Float, argName: String): Float = {
    shouldBe(arg <= 0f, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNotPositiveNumber(arg: Double, argName: String): Double = {
    shouldBe(arg <= 0.0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNotPositiveNumber(arg: BigDecimal, argName: String): BigDecimal = {
    shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeNumber(arg: Int, argName: String): Int = {
    shouldBe(arg < 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeNumber(arg: Long, argName: String): Long = {
    shouldBe(arg < 0L, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeNumber(arg: Float, argName: String): Float = {
    shouldBe(arg < 0f, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeNumber(arg: Double, argName: String): Double = {
    shouldBe(arg < 0.0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeNumber(arg: BigDecimal, argName: String): BigDecimal = {
    shouldBe(arg < 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeOrZeroNumber(arg: Int, argName: String): Int = {
    shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeOrZeroNumber(arg: Long, argName: String): Long = {
    shouldBe(arg <= 0L, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeOrZeroNumber(arg: Float, argName: String): Float = {
    shouldBe(arg <= 0f, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeOrZeroNumber(arg: Double, argName: String): Double = {
    shouldBe(arg <= 0.0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeOrZeroNumber(arg: BigDecimal, argName: String): BigDecimal = {
    shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeNegativeOrZeroNumber(arg: BigInt, argName: String): BigInt = {
    shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldNotBeNegativeNumber(arg: Int, argName: String): Int = {
    shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldNotBeNegativeNumber(arg: Long, argName: String): Long = {
    shouldBe(arg >= 0L, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldNotBeNegativeNumber(arg: Float, argName: String): Float = {
    shouldBe(arg >= 0F, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldNotBeNegativeNumber(arg: Double, argName: String): Double = {
    shouldBe(arg >= 0D, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldNotBeNegativeNumber(arg: BigDecimal, argName: String): BigDecimal = {
    shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldNotBeNegativeNumber(arg: BigInt, argName: String) = {
    shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
    arg
  }

  def shouldBeInRange(value: Int, fromInclude: Int, toExclude: Int, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toExclude,
             ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
  }

  def shouldBeInRange(value: Long, fromInclude: Long, toExclude: Long, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toExclude,
             ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
  }

  def shouldBeInRange(value: Float, fromInclude: Float, toExclude: Float, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toExclude,
             ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
  }

  def shouldBeInRange(value: Double, fromInclude: Double, toExclude: Double, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toExclude,
             ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
  }

  def shouldBeInRange(value: Int, range: Range, argName: String): Unit = {
    shouldBe(range.contains(value.toInt),
             ShouldBeInRangeInt, argName, value, range.start, range.end)
  }

  def shouldBeInRange(value: Long, range: Range, argName: String): Unit = {
    shouldBe(range.contains(value.toInt),
             ShouldBeInRangeInt, argName, value, range.start, range.end)
  }

  def shouldBeBetween(value: Int, fromInclude: Int, toInclude: Int, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toInclude,
             ShouldBeInRangeInt, argName, value, fromInclude, toInclude)
  }

  def shouldBeBetween(value: Long, fromInclude: Long, toInclude: Long, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toInclude,
             ShouldBeInRangeInt, argName, value, fromInclude, toInclude)
  }

  def shouldBeBetween(value: Float, fromInclude: Float, toInclude: Float, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toInclude,
             ShouldBeInRangeInt, argName, value, fromInclude, toInclude)
  }

  def shouldBeBetween(value: Double, fromInclude: Double, toInclude: Double, argName: String): Unit = {
    shouldBe(value >= fromInclude && value < toInclude,
             ShouldBeInRangeDouble, argName, value, fromInclude, toInclude)
  }

  def shouldBeBetween(value: Int, range: Range, argName: String): Unit = {
    shouldBe(range.contains(value.toInt),
             ShouldBeInRangeInt, argName, value, range.start, range.end)
  }

  def shouldBeBetween(value: Long, range: Range, argName: String): Unit = {
    shouldBe(range.contains(value.toInt),
             ShouldBeInRangeInt, argName, value, range.start, range.end)
  }
}
