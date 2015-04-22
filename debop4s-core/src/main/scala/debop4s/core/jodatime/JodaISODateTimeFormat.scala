package debop4s.core.jodatime

import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat => fmt}

object JodaISODateTimeFormat extends JodaISODateTimeFormat

trait JodaISODateTimeFormat {

  def date: DateTimeFormatter = fmt.date
  def time: DateTimeFormatter = fmt.time
  def timeNoMillis: DateTimeFormatter = fmt.timeNoMillis
  def tTime: DateTimeFormatter = fmt.tTime
  def tTimeNoMillis: DateTimeFormatter = fmt.tTimeNoMillis
  def dateTime: DateTimeFormatter = fmt.dateTime
  def dateTimeNoMillis: DateTimeFormatter = fmt.dateTimeNoMillis

  def ordinalDate: DateTimeFormatter = fmt.ordinalDate
  def ordinalDateTime: DateTimeFormatter = fmt.ordinalDateTime
  def ordinalDateTimeNoMillis: DateTimeFormatter = fmt.ordinalDateTimeNoMillis

  def weekDate: DateTimeFormatter = fmt.weekDate
  def weekDateTime: DateTimeFormatter = fmt.weekDateTime
  def weekDateTimeNoMillis: DateTimeFormatter = fmt.weekDateTimeNoMillis

  def basicDate: DateTimeFormatter = fmt.basicDate
  def basicTime: DateTimeFormatter = fmt.basicTime
  def basicTimeNoMillis: DateTimeFormatter = fmt.basicTimeNoMillis
  def basicTTime: DateTimeFormatter = fmt.basicTTime
  def basicTTimeNoMillis: DateTimeFormatter = fmt.basicTTimeNoMillis
  def basicDateTime: DateTimeFormatter = fmt.basicDateTime
  def basicDateTimeNoMillis: DateTimeFormatter = fmt.basicDateTimeNoMillis
  def basicOrdinalDate: DateTimeFormatter = fmt.basicOrdinalDate
  def basicOrdinalDateTime: DateTimeFormatter = fmt.basicOrdinalDateTime
  def basicOrdinalDateTimeNoMillis: DateTimeFormatter = fmt.basicOrdinalDateTimeNoMillis
  def basicWeekDate: DateTimeFormatter = fmt.basicWeekDate
  def basicWeekDateTime: DateTimeFormatter = fmt.basicWeekDateTime
  def basicWeekDateTimeNoMillis: DateTimeFormatter = fmt.basicWeekDateTimeNoMillis

  def year: DateTimeFormatter = fmt.year
  def yearMonth: DateTimeFormatter = fmt.yearMonth
  def yearMonthDay: DateTimeFormatter = fmt.yearMonthDay

  def weekyear: DateTimeFormatter = fmt.weekyear
  def weekyearWeek: DateTimeFormatter = fmt.weekyearWeek
  def weekyearWeekDay: DateTimeFormatter = fmt.weekyearWeekDay

  def hour: DateTimeFormatter = fmt.hour
  def hourMinute: DateTimeFormatter = fmt.hourMinute
  def hourMinuteSecond: DateTimeFormatter = fmt.hourMinuteSecond
  def hourMinuteSecondMillis: DateTimeFormatter = fmt.hourMinuteSecondMillis
  def hourMinuteSecondFraction: DateTimeFormatter = fmt.hourMinuteSecondFraction

  def dateHour: DateTimeFormatter = fmt.dateHour
  def dateHourMinute: DateTimeFormatter = fmt.dateHourMinute
  def dateHourMinuteSecond: DateTimeFormatter = fmt.dateHourMinuteSecond
  def dateHourMinuteSecondMillis: DateTimeFormatter = fmt.dateHourMinuteSecondMillis
  def dateHourMinuteSecondFraction: DateTimeFormatter = fmt.dateHourMinuteSecondFraction
}
