package debop4s.timeperiod.calendars

import debop4s.core.Logging
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.calendars.CollectKind.CollectKind
import debop4s.timeperiod.timerange._
import org.slf4j.LoggerFactory

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


object CalendarPeriodCollector {

  def apply(filter: CalendarPeriodCollectorFilter,
            limits: ITimePeriod): CalendarPeriodCollector =
    apply(filter, limits, SeekDirection.Forward)

  def apply(filter: CalendarPeriodCollectorFilter,
            limits: ITimePeriod,
            seekDir: SeekDirection): CalendarPeriodCollector =
    apply(filter, limits, SeekDirection.Forward, DefaultTimeCalendar)

  def apply(filter: CalendarPeriodCollectorFilter,
            limits: ITimePeriod,
            seekDir: SeekDirection,
            calendar: ITimeCalendar): CalendarPeriodCollector =
    new CalendarPeriodCollector(filter, limits, seekDir, calendar)
}

/**
 * debop4s.timeperiod.calendars.CalendarPeriodCollector
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 12:54
 */
class CalendarPeriodCollector(private[this] val _filter: CalendarPeriodCollectorFilter,
                              private[this] val _limits: ITimePeriod,
                              private[this] val _seekDir: SeekDirection = SeekDirection.Forward,
                              private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarVisitor[CalendarPeriodCollectorFilter, CalendarPeriodCollectorContext](_filter, _limits, _seekDir, _calendar) {

  def this(filter: CalendarPeriodCollectorFilter, limits: ITimePeriod) =
    this(filter, limits, SeekDirection.Forward, DefaultTimeCalendar)
  def this(filter: CalendarPeriodCollectorFilter, limits: ITimePeriod, seekDir: SeekDirection) =
    this(filter, limits, seekDir, DefaultTimeCalendar)
  def this(filter: CalendarPeriodCollectorFilter, limits: ITimePeriod, calendar: ITimeCalendar) =
    this(filter, limits, SeekDirection.Forward, calendar)

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  val periods: ITimePeriodCollection = TimePeriodCollection()

  def collectYears() {
    collectInternal(CollectKind.Year)
  }

  def collectMonths() {
    collectInternal(CollectKind.Month)
  }

  def collectDays() {
    collectInternal(CollectKind.Day)
  }

  def collectHours() {
    collectInternal(CollectKind.Hour)
  }

  def collectMinutes() {
    collectInternal(CollectKind.Minute)
  }

  private def collectInternal(scope: CollectKind) {
    val context = new CalendarPeriodCollectorContext(scope)
    // context.scope = scope
    startPeriodVisit(context)
  }

  override protected def enterYears(years: YearRangeCollection, context: CalendarPeriodCollectorContext) =
    context.scope.id > CollectKind.Year.id

  override protected def enterMonths(year: YearRange, context: CalendarPeriodCollectorContext) =
    context.scope.id > CollectKind.Month.id

  override protected def enterDays(month: MonthRange, context: CalendarPeriodCollectorContext) =
    context.scope.id > CollectKind.Day.id

  override protected def enterHours(day: DayRange, context: CalendarPeriodCollectorContext) =
    context.scope.id > CollectKind.Hour.id

  override protected def enterMinutes(hour: HourRange, context: CalendarPeriodCollectorContext) =
    context.scope.id > CollectKind.Minute.id

  @inline
  override protected def onVisitYears(years: YearRangeCollection, context: CalendarPeriodCollectorContext): Boolean = {
    log.trace(s"visit yearsView... yearsView=[$years]")

    if (context.scope != CollectKind.Year) {
      return true
    }

    years.yearsView
    .filter(y => isMatchingYear(y, context) && checkLimits(y))
    .foreach(y => periods.add(y))

    false
  }

  @inline
  override protected def onVisitYear(year: YearRange, context: CalendarPeriodCollectorContext): Boolean = {
    if (context.scope != CollectKind.Month)
      return true

    val monthFilter = (m: MonthRange) => isMatchingMonth(m, context) && checkLimits(m)

    if (filter.collectingMonths.size == 0) {
      year.monthsView
      .filter(monthFilter)
      .foreach(m => periods.add(m))
    } else {
      filter.collectingMonths.foreach { m =>
        if (m.isSingleMonth) {
          val month = MonthRange(year.year, m.startMonthOfYear, year.calendar)
          if (monthFilter(month)) {
            periods.add(month)
          }
        } else {
          val months =
            MonthRangeCollection(year.year,
              m.startMonthOfYear,
              m.endMonthOfYear - m.startMonthOfYear,
              year.calendar)
          val isMatching = months.monthsView.forall(m => isMatchingMonth(m, context))
          if (isMatching && checkLimits(months)) {
            periods.addAll(months.monthsView.asJava)
          }
        }
      }
    }
    false
  }

  @inline
  override protected def onVisitMonth(month: MonthRange, context: CalendarPeriodCollectorContext): Boolean = {
    if (context.scope != CollectKind.Day)
      return true

    if (filter.collectingDays.size == 0) {
      month.daysView
      .filter(d => isMatchingDay(d, context) && checkLimits(d))
      .foreach(d => periods.add(d))
    } else {
      filter.collectingDays.foreach {
        day =>
          if (day.isSingleDay) {
            val dayRange =
              DayRange(month.year,
                        month.monthOfYear,
                        day.startDayOfMonth,
                        month.calendar)

            if (isMatchingDay(dayRange, context) && checkLimits(dayRange)) {
              periods.add(dayRange)
            }
          } else {
            val days =
              DayRangeCollection(month.year,
                                  month.monthOfYear,
                                  day.startDayOfMonth,
                                  day.endDayOfMonth - day.startDayOfMonth,
                                  month.calendar)
            val isMatching = days.daysView.forall(d => isMatchingDay(d, context))
            if (isMatching && checkLimits(days)) {
              periods.addAll(days.daysView.asJava)
            }
          }
      }
    }
    false
  }

  @inline
  override protected def onVisitDay(day: DayRange, context: CalendarPeriodCollectorContext): Boolean = {
    if (context.scope != CollectKind.Hour) {
      return true
    }

    if (filter.collectingHours.size == 0) {
      day.hoursView
      .filter(h => isMatchingHour(h, context) && checkLimits(h))
      .foreach(h => periods.add(h))
    } else if (isMatchingDay(day, context)) {
      filter.collectingHours.foreach {
        h =>
          val start = h.start.getDateTime(day.start)
          val end = h.end.getDateTime(day.start)
          val hours = CalendarTimeRange(start, end, day.calendar)

          if (checkExcludePeriods(hours) && checkLimits(hours)) {
            periods.add(hours)
          }
      }
    }
    false
  }
}
