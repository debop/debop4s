package com.github.debop4s.timeperiod.timerange

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.timerange.MinuteRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오후 7:11
 */
@SerialVersionUID(4111802915947727424L)
class MinuteRange(private[this] val _moment: DateTime,
                  private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends MinuteTimeRange(Times.trimToSecond(_moment), 1, _calendar) {


  def year = startYear

  def monthOfYear = startMonthOfYear

  def dayOfMonth = startDayOfMonth

  def hourOfDay = startHourOfDay

  def minuteOfHour = startMinuteOfHour

  def previousMinute: MinuteRange = addMinutes(-1)

  def nextMinute: MinuteRange = addMinutes(1)

  def addMinutes(minutes: Int): MinuteRange = {
    val s = Times.asDate(this.start).withTime(hourOfDay, minuteOfHour, 0, 0)
    new MinuteRange(s + minutes.minute, calendar)
  }
}

object MinuteRange {

  def apply(): MinuteRange = apply(DefaultTimeCalendar)

  def apply(calendar: ITimeCalendar): MinuteRange = apply(Times.now, calendar)

  def apply(moment: DateTime): MinuteRange = apply(moment, DefaultTimeCalendar)

  def apply(moment: DateTime, calendar: ITimeCalendar): MinuteRange = new MinuteRange(moment, calendar)

  def apply(year: Int,
            monthOfYear: Int,
            dayOfMonth: Int,
            hourOfDay: Int,
            minuteOfHour: Int): MinuteRange =
    apply(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, DefaultTimeCalendar)

  def apply(year: Int,
            monthOfYear: Int,
            dayOfMonth: Int,
            hourOfDay: Int,
            minuteOfHour: Int,
            calendar: ITimeCalendar): MinuteRange =
    new MinuteRange(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour), calendar)

}
