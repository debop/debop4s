package debop4s.core.tools

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.tools.Converts._

/**
 * ConvertsFunSuite
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
class ConvertsFunSuite extends AbstractCoreFunSuite {

  test("getValueInt") {
    val one: Integer = 1
    val empty: Integer = null

    getValue(one) shouldEqual 1
    getValue(empty) shouldEqual 0
  }

  test("getValueLong") {
    val one: java.lang.Long = 1L
    val empty: java.lang.Long = null

    getValue(one) shouldEqual 1L
    getValue(empty) shouldEqual 0L
  }

  test("getValueFloat") {
    val one: java.lang.Float = 1f
    val empty: java.lang.Float = null
    getValue(one) shouldEqual 1f
    getValue(empty) shouldEqual 0f
  }

  test("getValueDouble") {
    val one: java.lang.Double = 1.0d
    val empty: java.lang.Double = null
    getValue(one) shouldEqual 1.0d
    getValue(empty) shouldEqual 0.0d
  }

  test("getIntTest") {
    getInt(null) shouldEqual 0
    getInt(1L) shouldEqual 1
    getInt("1") shouldEqual 1
    getInt("") shouldEqual 0
    getInt("a") shouldEqual 0
  }

  test("getLongTest") {
    getLong(null) shouldEqual 0L
    getLong(1) shouldEqual 1L
    getLong("1") shouldEqual 1L
    getLong("") shouldEqual 0L
    getLong("a") shouldEqual 0L
  }
}
