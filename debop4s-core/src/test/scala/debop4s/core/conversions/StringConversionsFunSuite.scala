package debop4s.core.conversions

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.string._

/**
 * StringConversionsFunSuite
 * @author Sunghyouk Bae
 */
class StringConversionsFunSuite extends AbstractCoreFunSuite {

  test("quoteC") {
    "nothing".quoteC shouldEqual "nothing"
    "name\tvalue\t\u20acb\u00fcllet?\u20ac".quoteC shouldEqual "name\\tvalue\\t\\u20acb\\xfcllet?\\u20ac"
    "she said \"hello\"".quoteC shouldEqual "she said \\\"hello\\\""
    "\\backslash".quoteC shouldEqual "\\\\backslash"
  }

  test("unquoteC") {
    "nothing".unquoteC shouldEqual "nothing"
    "name\\tvalue\\t\\u20acb\\xfcllet?\\u20ac".unquoteC shouldEqual "name\tvalue\t\u20acb\u00fcllet?\u20ac"
    "she said \\\"hello\\\"".unquoteC shouldEqual "she said \"hello\""
    "\\\\backslash".unquoteC shouldEqual "\\backslash"
    "real\\$dollar".unquoteC shouldEqual "real\\$dollar"
    "silly\\/quote".unquoteC shouldEqual "silly/quote"
  }

  test("hexlify") {
    "hello".getBytes.slice(1, 4).toHexString shouldEqual "656c6c"
    "hello".getBytes.toHexString shouldEqual "68656c6c6f"
  }

  test("unhexlify") {
    "656c6c".fromHexString.toList shouldEqual "hello".getBytes.slice(1, 4).toList
    "68656c6c6f".fromHexString.toList shouldEqual "hello".getBytes("UTF-8").toList

    "5".fromHexString.toHexString.toInt shouldEqual 5
  }

}
