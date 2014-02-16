package kr.debop4s.timeperiod

import org.joda.time.format.{ISODateTimeFormat => fmt, DateTimeFormatter}

object StaticISODateTimeFormat extends StaticISODateTimeFormat

trait StaticISODateTimeFormat {
  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e ( )]]*/
  def date: DateTimeFormatter = fmt.date

  /** @see [[org.joda.time.format.ISODateTimeFormat# t i m e ( )]]*/
  def time: DateTimeFormatter = fmt.time

  /** @see [[org.joda.time.format.ISODateTimeFormat# t i m e N o M i l l i s ( )]]*/
  def timeNoMillis: DateTimeFormatter = fmt.timeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# t T i m e ( )]]*/
  def tTime: DateTimeFormatter = fmt.tTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# t T i m e N o M i l l i s ( )]]*/
  def tTimeNoMillis: DateTimeFormatter = fmt.tTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e T i m e ( )]]*/
  def dateTime: DateTimeFormatter = fmt.dateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e T i m e N o M i l l i s ( )]]*/
  def dateTimeNoMillis: DateTimeFormatter = fmt.dateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# o r d i n a l D a t e ( )]]*/
  def ordinalDate: DateTimeFormatter = fmt.ordinalDate

  /** @see [[org.joda.time.format.ISODateTimeFormat# o r d i n a l D a t e T i m e ( )]]*/
  def ordinalDateTime: DateTimeFormatter = fmt.ordinalDateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# o r d i n a l D a t e T i m e N o M i l l i s ( )]]*/
  def ordinalDateTimeNoMillis: DateTimeFormatter = fmt.ordinalDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# w e e k D a t e ( )]]*/
  def weekDate: DateTimeFormatter = fmt.weekDate

  /** @see [[org.joda.time.format.ISODateTimeFormat# w e e k D a t e T i m e ( )]]*/
  def weekDateTime: DateTimeFormatter = fmt.weekDateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# w e e k D a t e T i m e N o M i l l i s ( )]]*/
  def weekDateTimeNoMillis: DateTimeFormatter = fmt.weekDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c D a t e ( )]]*/
  def basicDate: DateTimeFormatter = fmt.basicDate

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c T i m e ( )]]*/
  def basicTime: DateTimeFormatter = fmt.basicTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c T i m e N o M i l l i s ( )]]*/
  def basicTimeNoMillis: DateTimeFormatter = fmt.basicTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c T T i m e ( )]]*/
  def basicTTime: DateTimeFormatter = fmt.basicTTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c T T i m e N o M i l l i s ( )]]*/
  def basicTTimeNoMillis: DateTimeFormatter = fmt.basicTTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c D a t e T i m e ( )]]*/
  def basicDateTime: DateTimeFormatter = fmt.basicDateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c D a t e T i m e N o M i l l i s ( )]]*/
  def basicDateTimeNoMillis: DateTimeFormatter = fmt.basicDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c O r d i n a l D a t e ( )]]*/
  def basicOrdinalDate: DateTimeFormatter = fmt.basicOrdinalDate

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c O r d i n a l D a t e T i m e ( )]]*/
  def basicOrdinalDateTime: DateTimeFormatter = fmt.basicOrdinalDateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c O r d i n a l D a t e T i m e N o M i l l i s ( )]]*/
  def basicOrdinalDateTimeNoMillis: DateTimeFormatter = fmt.basicOrdinalDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c W e e k D a t e ( )]]*/
  def basicWeekDate: DateTimeFormatter = fmt.basicWeekDate

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c W e e k D a t e T i m e ( )]]*/
  def basicWeekDateTime: DateTimeFormatter = fmt.basicWeekDateTime

  /** @see [[org.joda.time.format.ISODateTimeFormat# b a s i c W e e k D a t e T i m e N o M i l l i s ( )]]*/
  def basicWeekDateTimeNoMillis: DateTimeFormatter = fmt.basicWeekDateTimeNoMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# y e a r ( )]]*/
  def year: DateTimeFormatter = fmt.year

  /** @see [[org.joda.time.format.ISODateTimeFormat# y e a r M o n t h ( )]]*/
  def yearMonth: DateTimeFormatter = fmt.yearMonth

  /** @see [[org.joda.time.format.ISODateTimeFormat# y e a r M o n t h D a y ( )]]*/
  def yearMonthDay: DateTimeFormatter = fmt.yearMonthDay

  /** @see [[org.joda.time.format.ISODateTimeFormat# w e e k y e a r ( )]]*/
  def weekyear: DateTimeFormatter = fmt.weekyear

  /** @see [[org.joda.time.format.ISODateTimeFormat# w e e k y e a r W e e k ( ) ( )]]*/
  def weekyearWeek(): DateTimeFormatter = fmt.weekyearWeek

  /** @see [[org.joda.time.format.ISODateTimeFormat# w e e k y e a r W e e k D a y ( ) ( )]]*/
  def weekyearWeekDay(): DateTimeFormatter = fmt.weekyearWeekDay

  /** @see [[org.joda.time.format.ISODateTimeFormat# h o u r ( )]]*/
  def hour: DateTimeFormatter = fmt.hour

  /** @see [[org.joda.time.format.ISODateTimeFormat# h o u r M i n u t e ( )]]*/
  def hourMinute: DateTimeFormatter = fmt.hourMinute

  /** @see [[org.joda.time.format.ISODateTimeFormat# h o u r M i n u t e S e c o n d ( )]]*/
  def hourMinuteSecond: DateTimeFormatter = fmt.hourMinuteSecond

  /** @see [[org.joda.time.format.ISODateTimeFormat# h o u r M i n u t e S e c o n d M i l l i s ( )]]*/
  def hourMinuteSecondMillis: DateTimeFormatter = fmt.hourMinuteSecondMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# h o u r M i n u t e S e c o n d F r a c t i o n ( )]]*/
  def hourMinuteSecondFraction: DateTimeFormatter = fmt.hourMinuteSecondFraction

  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e H o u r ( )]]*/
  def dateHour: DateTimeFormatter = fmt.dateHour

  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e H o u r M i n u t e ( )]]*/
  def dateHourMinute: DateTimeFormatter = fmt.dateHourMinute

  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e H o u r M i n u t e S e c o n d ( )]]*/
  def dateHourMinuteSecond: DateTimeFormatter = fmt.dateHourMinuteSecond

  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e H o u r M i n u t e S e c o n d M i l l i s ( )]]*/
  def dateHourMinuteSecondMillis: DateTimeFormatter = fmt.dateHourMinuteSecondMillis

  /** @see [[org.joda.time.format.ISODateTimeFormat# d a t e H o u r M i n u t e S e c o n d F r a c t i o n ( )]]*/
  def dateHourMinuteSecondFraction: DateTimeFormatter = fmt.dateHourMinuteSecondFraction
}
