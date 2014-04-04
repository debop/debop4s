package com.github.debop4s

import scala.concurrent.ExecutionContext
import com.github.debop4s.core.utils.Strings

/**
 * com.github.debop4s.core.package
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:03
 */
package object core {

    implicit val executor = ExecutionContext.fromExecutor(scala.concurrent.ExecutionContext.Implicits.global)

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


    implicit class StringExtensions(s: String) {
        def words = s split " "

        def isWhitespace: Boolean = Strings.isWhitespace(s)
    }
}