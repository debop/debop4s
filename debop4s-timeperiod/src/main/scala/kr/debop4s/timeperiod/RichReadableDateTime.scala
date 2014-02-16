package kr.debop4s.timeperiod

import org.joda.time.{Chronology, DateTime, MutableDateTime, ReadableDateTime}

/**
 * kr.debop4s.time.RichReadableDateTime
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:58
 */
class RichReadableDateTime(val self: ReadableDateTime) extends AnyVal with Ordered[ReadableDateTime] {

  def millis = self.getMillis

  def second: Int = self.getSecondOfMinute

  def minute: Int = self.getMinuteOfHour

  def hour: Int = self.getHourOfDay

  def day: Int = self.getDayOfMonth

  def month: Int = self.getMonthOfYear

  def year: Int = self.getYear

  def century: Int = self.getCenturyOfEra

  def weekyear: Int = self.getWeekyear

  def week: Int = self.getWeekOfWeekyear

  def chronology: Chronology = self.getChronology

  def dateTime: DateTime = self.toDateTime

  def mutableDateTime: MutableDateTime = self.toMutableDateTime

  def compare(that: ReadableDateTime): Int = self.compareTo(that)
}
