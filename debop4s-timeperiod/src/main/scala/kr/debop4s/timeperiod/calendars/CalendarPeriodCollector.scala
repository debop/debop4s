package kr.debop4s.timeperiod.calendars

import kr.debop4s.timeperiod.SeekDirection.SeekDirection
import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.calendars.CollectKind.CollectKind
import kr.debop4s.timeperiod.timerange._

/**
 * kr.debop4s.timeperiod.calendars.CalendarPeriodCollector
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오전 12:54
 */
class CalendarPeriodCollector(private[this] val _filter: CalendarPeriodCollectorFilter,
                              private[this] val _limits: ITimePeriod,
                              private[this] val _seekDir: SeekDirection = SeekDirection.Forward,
                              private[this] val _calendar: ITimeCalendar = DefaultTimeCalendar)
  extends CalendarVisitor[CalendarPeriodCollectorFilter, CalendarPeriodCollectorContext](_filter,
                                                                                          _limits,
                                                                                          _seekDir,
                                                                                          _calendar) {

  val periods: ITimePeriodCollection = TimePeriodCollection()

  def collectYears() {
    log.trace("collect years...")
    collectInternal(CollectKind.Year)
  }

  def collectMonths() {
    log.trace("collect months...")
    collectInternal(CollectKind.Month)
  }

  def collectDays() {
    log.trace("collect days...")
    collectInternal(CollectKind.Day)
  }

  def collectHours() {
    log.trace("collect hours...")
    collectInternal(CollectKind.Hour)
  }

  def collectMinutes() {
    log.trace("collect minutes...")
    collectInternal(CollectKind.Minute)
  }

  private def collectInternal(collectKind: CollectKind) {
    val context = new CalendarPeriodCollectorContext()
    context.scope = collectKind
    startPeriodVisit(context)
  }

  override protected def enterYears(years: YearRangeCollection, context: CalendarPeriodCollectorContext) =
    context.scope > CollectKind.Year

  override protected def enterMonths(year: YearRange, context: CalendarPeriodCollectorContext) =
    context.scope > CollectKind.Month

  override protected def enterDays(month: MonthRange, context: CalendarPeriodCollectorContext) =
    context.scope > CollectKind.Day

  override protected def enterHours(day: DayRange, context: CalendarPeriodCollectorContext) =
    context.scope > CollectKind.Hour

  override protected def enterMinutes(hour: HourRange, context: CalendarPeriodCollectorContext) =
    context.scope > CollectKind.Minute

  override protected def onVisitYears(years: YearRangeCollection, context: CalendarPeriodCollectorContext): Boolean = {
    log.trace(s"visit years... years=[$years]")

    if (context.scope != CollectKind.Year) {
      true
    } else {
      years.getYears
      .filter(y => isMatchingYear(y, context) && checkLimits(y))
      .foreach(y => periods.add(y))
      false
    }
  }

  override protected def onVisitYear(year: YearRange, context: CalendarPeriodCollectorContext): Boolean = {
    log.trace(s"visit year... year=[$year]")

    if (context.scope != CollectKind.Month)
      true

    if (filter.collectingMonths.size == 0) {
      year.getMonths
      .filter(m => isMatchingMonth(m, context) && checkLimits(m))
      .foreach(m => periods.add(m))
    } else {
      filter.getCollectingMonths.foreach(m => {
        if (m.isSingleMonth) {
          val month = new MonthRange(year.year, m.startMonthOfYear, year.calendar)
          if (isMatchingMonth(month, context) && checkLimits(month)) {
            periods.add(month)
          }
        } else {
          val months = new MonthRangeCollection(year.year,
                                                 m.startMonthOfYear,
                                                 m.endMonthOfYear - m.startMonthOfYear,
                                                 year.calendar);
          val isMatching = months.getMonths.forall(m => isMatchingMonth(m, context))
          if (isMatching && checkLimits(months)) {
            periods.addAll(months.getMonths)
          }
        }
      })
    }
    false
  }

  override protected def onVisitMonth(month: MonthRange, context: CalendarPeriodCollectorContext): Boolean = {
    log.trace(s"visit month... month=[$month]")

    if (context.scope != CollectKind.Day)
      true

    if (filter.collectingDays.size == 0) {
      month.getDays
      .filter(d => isMatchingDay(d, context) && checkLimits(d))
      .foreach(d => periods.add(d))
    } else {
      filter.collectingDays.foreach(day => {
        if (day.isSingleDay) {
          val dayRange = new DayRange(month.year, month.monthOfYear, day.startDayOfMonth, month.calendar)
          if (isMatchingDay(dayRange, context) && checkLimits(dayRange)) {
            periods.add(dayRange)
          }
        } else {
          val days = DayRangeCollection(month.year,
                                         month.monthOfYear,
                                         day.startDayOfMonth,
                                         day.endDayOfMonth - day.startDayOfMonth,
                                         month.calendar)
          val isMatching = days.getDays.forall(d => isMatchingDay(d, context))
          if (isMatching && checkLimits(days)) {
            periods.addAll(days.getDays)
          }
        }
      })
    }
    false
  }

  override protected def onVisitDay(day: DayRange, context: CalendarPeriodCollectorContext): Boolean = {
    log.trace(s"visit day... day=[$day]")

    if (context.scope != CollectKind.Hour)
      true

    if (filter.collectingHours.size == 0) {
      day.getHours
      .filter(h => isMatchingHour(h, context) && checkLimits(h))
      .foreach(h => periods.add(h))
    } else if (isMatchingDay(day, context)) {
      filter.collectingHours.foreach(h => {
        val start = h.start.getDateTime(day.start)
        val end = h.end.getDateTime(day.start)
        val hours = CalendarTimeRange(start, end, day.calendar)

        if (checkExcludePeriods(hours) && checkLimits(hours)) {
          periods.add(hours)
        }
      })
    }
    false
  }
}
