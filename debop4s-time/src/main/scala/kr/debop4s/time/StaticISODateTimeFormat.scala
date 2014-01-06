package kr.debop4s.time

import org.joda.time.format.{ISODateTimeFormat => fmt, DateTimeFormatter}

object StaticISODateTimeFormat extends StaticISODateTimeFormat

trait StaticISODateTimeFormat {
  /** @see [[org.joda.time.format.ISODateTimeFormat#date()]]*/
  def date: DateTimeFormatter = fmt.date

  /** @see [[org.joda.time.format.ISODateTimeFormat#time()]]*/
  def time: DateTimeFormatter = fmt.time

  /** @see [[org.joda.time.format.ISODateTimeFormat#timeNoMillis()]]*/
  def timeNoMillis: DateTimeFormatter = fmt.timeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat#tTime()]]*/
  def tTime: DateTimeFormatter = fmt.tTime

  /** @see [[org.joda.time.format.ISODateTimeFormat#tTimeNoMillis()]]*/
  def tTimeNoMillis: DateTimeFormatter = fmt.tTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat#dateTime()]]*/
  def dateTime: DateTimeFormatter = fmt.dateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat#dateTimeNoMillis()]]*/
  def dateTimeNoMillis: DateTimeFormatter = fmt.dateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat#ordinalDate()]]*/
  def ordinalDate: DateTimeFormatter = fmt.ordinalDate

  /** @see [[org.joda.time.format.ISODateTimeFormat#ordinalDateTime()]]*/
  def ordinalDateTime: DateTimeFormatter = fmt.ordinalDateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat#ordinalDateTimeNoMillis()]]*/
  def ordinalDateTimeNoMillis: DateTimeFormatter = fmt.ordinalDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat#weekDate()]]*/
  def weekDate: DateTimeFormatter = fmt.weekDate

  /** @see [[org.joda.time.format.ISODateTimeFormat#weekDateTime()]]*/
  def weekDateTime: DateTimeFormatter = fmt.weekDateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat#weekDateTimeNoMillis()]]*/
  def weekDateTimeNoMillis: DateTimeFormatter = fmt.weekDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat#basicDate()]]*/
  def basicDate: DateTimeFormatter = fmt.basicDate

  /** @see [[org.joda.time.format.ISODateTimeFormat#basicTime()]]*/
  def basicTime: DateTimeFormatter = fmt.basicTime

  /** @see [[org.joda.time.format.ISODateTimeFormat#basicTimeNoMillis()]]*/
  def basicTimeNoMillis: DateTimeFormatter = fmt.basicTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat#basicTTime()]]*/
  def basicTTime: DateTimeFormatter = fmt.basicTTime
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicTTimeNoMillis()]]*/
  def basicTTimeNoMillis: DateTimeFormatter = fmt.basicTTimeNoMillis
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicDateTime()]]*/
  def basicDateTime: DateTimeFormatter = fmt.basicDateTime
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicDateTimeNoMillis()]]*/
  def basicDateTimeNoMillis: DateTimeFormatter = fmt.basicDateTimeNoMillis
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicOrdinalDate()]]*/
  def basicOrdinalDate: DateTimeFormatter = fmt.basicOrdinalDate
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicOrdinalDateTime()]]*/
  def basicOrdinalDateTime: DateTimeFormatter = fmt.basicOrdinalDateTime
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicOrdinalDateTimeNoMillis()]]*/
  def basicOrdinalDateTimeNoMillis: DateTimeFormatter = fmt.basicOrdinalDateTimeNoMillis
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicWeekDate()]]*/
  def basicWeekDate: DateTimeFormatter = fmt.basicWeekDate
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicWeekDateTime()]]*/
  def basicWeekDateTime: DateTimeFormatter = fmt.basicWeekDateTime
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#basicWeekDateTimeNoMillis()]]*/
  def basicWeekDateTimeNoMillis: DateTimeFormatter = fmt.basicWeekDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat#year()]]*/
  def year: DateTimeFormatter = fmt.year

  /** @see [[org.joda.time.format.ISODateTimeFormat#yearMonth()]]*/
  def yearMonth: DateTimeFormatter = fmt.yearMonth

  /** @see [[org.joda.time.format.ISODateTimeFormat#yearMonthDay()]]*/
  def yearMonthDay: DateTimeFormatter = fmt.yearMonthDay
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#weekyear()]]*/
  def weekyear: DateTimeFormatter = fmt.weekyear
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#weekyearWeek()()]]*/
  def weekyearWeek(): DateTimeFormatter = fmt.weekyearWeek
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#weekyearWeekDay()()]]*/
  def weekyearWeekDay(): DateTimeFormatter = fmt.weekyearWeekDay
    
  /** @see [[org.joda.time.format.ISODateTimeFormat#hour()]]*/
  def hour: DateTimeFormatter = fmt.hour
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#hourMinute()]]*/
  def hourMinute: DateTimeFormatter = fmt.hourMinute
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#hourMinuteSecond()]]*/
  def hourMinuteSecond: DateTimeFormatter = fmt.hourMinuteSecond
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#hourMinuteSecondMillis()]]*/
  def hourMinuteSecondMillis: DateTimeFormatter = fmt.hourMinuteSecondMillis
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#hourMinuteSecondFraction()]]*/
  def hourMinuteSecondFraction: DateTimeFormatter = fmt.hourMinuteSecondFraction
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#dateHour()]]*/
  def dateHour: DateTimeFormatter = fmt.dateHour
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#dateHourMinute()]]*/
  def dateHourMinute: DateTimeFormatter = fmt.dateHourMinute
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#dateHourMinuteSecond()]]*/
  def dateHourMinuteSecond: DateTimeFormatter = fmt.dateHourMinuteSecond
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#dateHourMinuteSecondMillis()]]*/
  def dateHourMinuteSecondMillis: DateTimeFormatter = fmt.dateHourMinuteSecondMillis
  
  /** @see [[org.joda.time.format.ISODateTimeFormat#dateHourMinuteSecondFraction()]]*/
  def dateHourMinuteSecondFraction: DateTimeFormatter = fmt.dateHourMinuteSecondFraction
}
