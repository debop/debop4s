package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.tools.Converts._
import org.fest.assertions.Assertions._

/**
 * ConvertsFunSuite
 * @author Sunghyouk Bae
 */
class ConvertsFunSuite extends AbstractCoreFunSuite {

  test("getValueInt") {
    val one: Integer = 1
    val empty: Integer = null
    assertThat(getValue(one)).isEqualTo(1)
    assertThat(getValue(empty)).isEqualTo(0)
  }

  test("getValueLong") {
    val one: java.lang.Long = 1L
    val empty: java.lang.Long = null
    assertThat(getValue(one)).isEqualTo(1)
    assertThat(getValue(empty)).isEqualTo(0)
  }

  test("getValueFloat") {
    val one: java.lang.Float = 1f
    val empty: java.lang.Float = null
    assertThat(getValue(one)).isEqualTo(1f)
    assertThat(getValue(empty)).isEqualTo(0)
  }

  test("getValueDouble") {
    val one: java.lang.Double = 1.0d
    val empty: java.lang.Double = null
    assertThat(getValue(one)).isEqualTo(1.0d)
    assertThat(getValue(empty)).isEqualTo(0)
  }

  test("getIntTest") {
    assertThat(getInt(null)).isEqualTo(0)
    assertThat(getInt(1L)).isEqualTo(1)
    assertThat(getInt("1")).isEqualTo(1)
    assertThat(getInt("")).isEqualTo(0)
    assertThat(getInt("a")).isEqualTo(0)
  }

  test("getLongTest") {
    assertThat(getLong(null)).isEqualTo(0)
    assertThat(getLong(1L)).isEqualTo(1)
    assertThat(getLong("1")).isEqualTo(1)
    assertThat(getLong("")).isEqualTo(0)
    assertThat(getLong("a")).isEqualTo(0)
  }
}
