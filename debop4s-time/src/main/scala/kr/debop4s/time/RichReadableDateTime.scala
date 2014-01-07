package kr.debop4s.time

import org.joda.time.{Chronology, DateTime, MutableDateTime, ReadableDateTime}

/**
 * kr.debop4s.time.RichReadableDateTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:58
 */
class RichReadableDateTime(val self: ReadableDateTime) extends AnyVal with Ordered[ReadableDateTime] {

    def millis = self.getMillis
    def millisOfSecond = self.getMillisOfSecond
    def millisOfDay = self.getMillisOfDay

    def secondOfMinute: Int = self.getSecondOfMinute
    def minuteOfHour: Int = self.getMinuteOfHour
    def hourOfDay: Int = self.getHourOfDay
    def dayOfMonth: Int = self.getDayOfMonth
    def monthOfYear: Int = self.getMonthOfYear
    def year: Int = self.getYear
    def century: Int = self.getCenturyOfEra

    def weekyear: Int = self.getWeekyear
    def weekOfWeekyear: Int = self.getWeekOfWeekyear

    def chronology: Chronology = self.getChronology

    def dateTime: DateTime = self.toDateTime
    def mutableDateTime: MutableDateTime = self.toMutableDateTime

    def compare(that: ReadableDateTime): Int = self.compareTo(that)
}
