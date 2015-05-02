package debop4s.timeperiod.timerange

import debop4s.timeperiod.utils.Times
import debop4s.timeperiod.{Halfyear, ITimeCalendar, Quarter, TimeCalendar}
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

import scala.collection.immutable.Stream.cons

/**
 * TimeRanges
 * @author sunghyouk.bae@gmail.com
 */
object TimeRanges {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  implicit val calendar: ITimeCalendar = TimeCalendar.getDefault

  def yearStream(start: Int, end: Int)(implicit calendar: ITimeCalendar): Stream[YearRange] = {

    def tails(year: Int): Stream[YearRange] = {
      if (year < end) cons(YearRange(year, calendar), tails(year + 1))
      else Stream.empty[YearRange]
    }

    cons(YearRange(start, calendar), tails(start + 1))
  }

  def halfyearStream(startYear: Int, startHalfyear: Halfyear, halfyearCount: Int)
                    (implicit calendar: ITimeCalendar): Stream[HalfyearRange] = {
    def tails(c: Int): Stream[HalfyearRange] = {
      if (c < halfyearCount) {
        val v = Times.addHalfyear(startYear, startHalfyear, c)
        cons(HalfyearRange(v.year, v.halfyear, calendar), tails(c + 1))
      } else {
        Stream.empty[HalfyearRange]
      }
    }
    cons(HalfyearRange(startYear, startHalfyear, calendar), tails(1))
  }

  def quarterStream(startYear: Int, startQuarter: Quarter, quarterCount: Int)
                   (implicit calendar: ITimeCalendar): Stream[QuarterRange] = {

    def tails(c: Int): Stream[QuarterRange] = {
      if (c < quarterCount) {
        val v = Times.addQuarter(startYear, startQuarter, c)
        cons(QuarterRange(v.year, v.quarter, calendar), tails(c + 1))
      } else {
        Stream.empty[QuarterRange]
      }
    }

    cons(QuarterRange(startYear, startQuarter, calendar), tails(1))
  }

  def monthStream(startMonth: DateTime, monthCount: Int)
                 (implicit calendar: ITimeCalendar): Stream[MonthRange] = {
    def tails(c: Int): Stream[MonthRange] = {
      if (c < monthCount) cons(MonthRange(startMonth.plusMonths(c), calendar), tails(c + 1))
      else Stream.empty[MonthRange]
    }
    cons(MonthRange(startMonth, calendar), tails(1))
  }

  def weekStream(startWeek: DateTime, weekCount: Int)
                (implicit calendar: ITimeCalendar): Stream[WeekRange] = {
    def tails(c: Int): Stream[WeekRange] = {
      if (c < weekCount) cons(WeekRange(startWeek.plusWeeks(c), calendar), tails(c + 1))
      else Stream.empty[WeekRange]
    }
    cons(WeekRange(startWeek, calendar), tails(1))
  }

  def dayStream(start: DateTime, dayCount: Int)
               (implicit calendar: ITimeCalendar): Stream[DayRange] = {

    def tails(day: Int): Stream[DayRange] = {
      if (day < dayCount) cons(DayRange(start.plusDays(day), calendar), tails(day + 1))
      else Stream.empty[DayRange]
    }
    cons(DayRange(start, calendar), tails(1))
  }

  def hourStream(startHour: DateTime, hourCount: Int)
                (implicit calendar: ITimeCalendar): Stream[HourRange] = {
    def tails(hour: Int): Stream[HourRange] = {
      if (hour < hourCount) cons(HourRange(startHour.plusHours(hour), calendar), tails(hour + 1))
      else Stream.empty[HourRange]
    }
    cons(HourRange(startHour, calendar), tails(1))
  }

  def minuteStream(startMinute: DateTime, minuteCount: Int)
                  (implicit calendar: ITimeCalendar): Stream[MinuteRange] = {
    def tails(m: Int): Stream[MinuteRange] = {
      if (m < minuteCount) cons(MinuteRange(startMinute.plusMinutes(m), calendar), tails(m + 1))
      else Stream.empty[MinuteRange]
    }
    cons(MinuteRange(startMinute, calendar), tails(1))
  }
}
