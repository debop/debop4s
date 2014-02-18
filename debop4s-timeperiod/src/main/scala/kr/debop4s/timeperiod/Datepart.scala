package kr.debop4s.timeperiod

import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.Hashs
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

@SerialVersionUID(-2730296141281632596L)
class Datepart(val value: DateTime) extends ValueObject with Ordered[Datepart] {

    def this() {
        this(new DateTime(0))
    }

    def this(year: Int, monthOfYear: Int = 1, dayOfMonth: Int = 1) {
        this(new DateTime(year, monthOfYear, dayOfMonth, 0, 0))
    }

    def year = value.getYear

    def monthOfYear = value.getMonthOfYear

    def dayOfMonth = value.getDayOfMonth

    def getDateTime(time: Timepart): DateTime = {
        if (time != null) value + time.value.getMillis
        else value
    }

    def getDateTime(hourOfDay: Int, minuteOfHour: Int = 0, secondOfMinute: Int = 0, millisOfSecond: Int = 0): DateTime =
        getDateTime(Timepart(hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond))

    def getDateTime(millis: Long): DateTime =
        getDateTime(Timepart(millis.toDateTime))

    def compare(that: Datepart) = value.compareTo(that.value)

    override def hashCode() = Hashs.compute(value)

    override protected def buildStringHelper =
        super.buildStringHelper
            .add("value", value)
}

object Datepart {

    def apply() = new Datepart(Times.today)

    def apply(moment: DateTime) = new Datepart(moment.withTimeAtStartOfDay())

    def today() = apply()
}

