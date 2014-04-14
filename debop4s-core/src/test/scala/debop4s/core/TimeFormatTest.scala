package debop4s.core

import java.util.Locale

class TimeFormatTest extends AbstractCoreTest {

    test("format correctly with non US locale") {
        val locale = Locale.KOREA
        val format = "EEEE"
        val timeFormat = new TimeFormat(format, Some(locale))
        val day = "목요일"

        println(timeFormat.parse(day))
        timeFormat.parse(day).format(format, locale) shouldEqual day
    }
}
