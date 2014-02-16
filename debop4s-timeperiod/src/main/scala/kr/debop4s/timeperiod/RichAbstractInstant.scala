package kr.debop4s.timeperiod

import java.util.Date
import org.joda.time._
import org.joda.time.base.AbstractInstant

/**
 * kr.debop4s.time.RichAbstractInstant
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:35
 */
class RichAbstractInstant(val self: AbstractInstant) extends AnyVal with Ordered[AbstractInstant] {

  def date: Date = self.toDate

  def dateTime: DateTime = self.toDateTime

  def dateTime(chronology: Chronology): DateTime = self.toDateTime(chronology)

  def dateTime(zone: DateTimeZone): DateTime = self.toDateTime(zone)

  def dateTimeISO: DateTime = self.toDateTimeISO

  def dateTimeUTC = dateTime(DateTimeZone.UTC)

  def instance: Instant = self.toInstant

  def mutableDateTime: MutableDateTime = self.toMutableDateTime

  def mutableDateTime(chronology: Chronology): MutableDateTime = self.toMutableDateTime(chronology)

  def mutableDateTime(zone: DateTimeZone): MutableDateTime = self.toMutableDateTime(zone)

  def mutableDateTimeISO: MutableDateTime = self.toMutableDateTimeISO

  def mutableDateTimeUTC: MutableDateTime = mutableDateTime(DateTimeZone.UTC)

  def compare(that: AbstractInstant): Int = self.compareTo(that)
}
