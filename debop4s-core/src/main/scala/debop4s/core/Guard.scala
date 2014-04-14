package debop4s.core

import debop4s.core.utils.Strings
import java.util.Objects
import scala.annotation.varargs

/**
 * Guard 패턴을 이용할 수 있도록 해주는 object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:04
 */
object Guard {

    def firstNotNull[T <: AnyRef](first: T, second: T): T = {
        if (!Objects.equals(first, null)) first
        else if (!Objects.equals(second, null)) second
        else throw new IllegalArgumentException("all parameter is null.")
    }

    def toOption[T <: AnyRef](v: T): Option[T] = {
        if (v == null) None else Some(v)
    }

    def shouldBe(cond: Boolean) {
        assert(cond)
    }

    def shouldBe(cond: Boolean, msg: String) {
        assert(cond, msg)
    }

    @varargs
    def shouldBe(cond: Boolean, fmt: String, args: Any*) {
        assert(cond, fmt.format(args: _*))
    }

    def shouldBeEquals(actual: Any, expected: Any, actualName: String) {
        shouldBe(Objects.equals(actual, expected), ShouldBeEquals, actualName, actual, expected)
    }

    def shouldBeNull(arg: AnyRef, argName: String) = {
        shouldBe(arg == null, ShouldBeNull, argName)
        arg
    }

    def shouldNotBeNull(arg: AnyRef, argName: String) = {
        shouldBe(arg != null, ShouldNotBeNull, argName)
        arg
    }

    def shouldBeEmpty(arg: String, argName: String) = {
        shouldBe(Strings.isEmpty(arg), ShouldBeEmptyString, argName)
        arg
    }

    def shouldNotBeEmpty(arg: String, argName: String) = {
        shouldBe(Strings.isNotEmpty(arg), ShouldBeEmptyString, argName)
        arg
    }

    def shouldBeWhitespace(arg: String, argName: String) = {
        shouldBe(Strings.isWhitespace(arg), ShouldBeWhiteSpace, argName)
        arg
    }

    def shouldNotBeWhitespace(arg: String, argName: String) = {
        shouldBe(Strings.isNotWhitespace(arg), ShouldNotBeWhiteSpace, argName)
        arg
    }

    def shouldBePositiveNumber(arg: Int, argName: String) = {
        shouldBe(arg > 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveNumber(arg: Long, argName: String) = {
        shouldBe(arg > 0L, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveNumber(arg: Float, argName: String) = {
        shouldBe(arg > 0f, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveNumber(arg: Double, argName: String) = {
        shouldBe(arg > 0.0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveNumber(arg: BigDecimal, argName: String) = {
        shouldBe(arg > 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveOrZeroNumber(arg: Int, argName: String) = {
        shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveOrZeroNumber(arg: Long, argName: String) = {
        shouldBe(arg >= 0L, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveOrZeroNumber(arg: Float, argName: String) = {
        shouldBe(arg >= 0f, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveOrZeroNumber(arg: Double, argName: String) = {
        shouldBe(arg >= 0.0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBePositiveOrZeroNumber(arg: BigDecimal, argName: String) = {
        shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNotPositiveNumber(arg: Int, argName: String) = {
        shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNotPositiveNumber(arg: Long, argName: String) = {
        shouldBe(arg <= 0L, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNotPositiveNumber(arg: Float, argName: String) = {
        shouldBe(arg <= 0f, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNotPositiveNumber(arg: Double, argName: String) = {
        shouldBe(arg <= 0.0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNotPositiveNumber(arg: BigDecimal, argName: String) = {
        shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeNumber(arg: Int, argName: String) = {
        shouldBe(arg < 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeNumber(arg: Long, argName: String) = {
        shouldBe(arg < 0L, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeNumber(arg: Float, argName: String) = {
        shouldBe(arg < 0f, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeNumber(arg: Double, argName: String) = {
        shouldBe(arg < 0.0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeNumber(arg: BigDecimal, argName: String) = {
        shouldBe(arg < 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeOrZeroNumber(arg: Int, argName: String) = {
        shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeOrZeroNumber(arg: Long, argName: String) = {
        shouldBe(arg <= 0L, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeOrZeroNumber(arg: Float, argName: String) = {
        shouldBe(arg <= 0f, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeOrZeroNumber(arg: Double, argName: String) = {
        shouldBe(arg <= 0.0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeNegativeOrZeroNumber(arg: BigDecimal, argName: String) = {
        shouldBe(arg <= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldNotBeNegativeNumber(arg: Int, argName: String) = {
        shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldNotBeNegativeNumber(arg: Long, argName: String) = {
        shouldBe(arg >= 0L, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldNotBeNegativeNumber(arg: Float, argName: String) = {
        shouldBe(arg >= 0f, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldNotBeNegativeNumber(arg: Double, argName: String) = {
        shouldBe(arg >= 0.0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldNotBeNegativeNumber(arg: BigDecimal, argName: String) = {
        shouldBe(arg >= 0, ShouldBePositiveNumber, argName)
        arg
    }

    def shouldBeInRange(value: Int, fromInclude: Int, toExclude: Int, argName: String) {
        shouldBe(value >= fromInclude && value < toExclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
    }

    def shouldBeInRange(value: Long, fromInclude: Long, toExclude: Long, argName: String) {
        shouldBe(value >= fromInclude && value < toExclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
    }

    def shouldBeInRange(value: Float, fromInclude: Float, toExclude: Float, argName: String) {
        shouldBe(value >= fromInclude && value < toExclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
    }

    def shouldBeInRange(value: Double, fromInclude: Double, toExclude: Double, argName: String) {
        shouldBe(value >= fromInclude && value < toExclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toExclude)
    }

    def shouldBeBetween(value: Int, fromInclude: Int, toInclude: Int, argName: String) {
        shouldBe(value >= fromInclude && value < toInclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toInclude)
    }

    def shouldBeBetween(value: Long, fromInclude: Long, toInclude: Long, argName: String) {
        shouldBe(value >= fromInclude && value < toInclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toInclude)
    }

    def shouldBeBetween(value: Float, fromInclude: Float, toInclude: Float, argName: String) {
        shouldBe(value >= fromInclude && value < toInclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toInclude)
    }

    def shouldBeBetween(value: Double, fromInclude: Double, toInclude: Double, argName: String) {
        shouldBe(value >= fromInclude && value < toInclude,
            ShouldBeInRangeInt, argName, value, fromInclude, toInclude)
    }
}
