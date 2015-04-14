package debop4s.core.jodatime

import org.joda.time.{Chronology, DateTime, MutableDateTime, ReadableDateTime}


class JodaRichReadableDateTime(val self: ReadableDateTime) extends AnyVal with Ordered[ReadableDateTime] {

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
