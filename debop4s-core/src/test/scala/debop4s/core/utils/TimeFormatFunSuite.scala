package debop4s.core.utils

import java.util.Locale

import debop4s.core.AbstractCoreFunSuite

class TimeFormatFunSuite extends AbstractCoreFunSuite {

  test("format correctly with non US locale") {
    val locale = Locale.KOREA
    val format = "EEEE"
    val timeFormat = new TimeFormat(format, Some(locale))
    val day = "목요일"

    println(timeFormat.parse(day))
    timeFormat.parse(day).format(format, locale) shouldEqual day
  }
}
