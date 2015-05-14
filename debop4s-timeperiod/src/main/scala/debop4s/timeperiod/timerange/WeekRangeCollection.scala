package debop4s.timeperiod.timerange

import java.util

import com.google.common.collect.Lists
import debop4s.timeperiod.TimeSpec._
import debop4s.timeperiod._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

import scala.collection.SeqView


class WeekRangeCollection(private[this] val year: Int,
                          private[this] val weekOfYear: Int,
                          private[this] val weekCount: Int,
                          private[this] val calendar: ITimeCalendar = DefaultTimeCalendar)
  extends WeekTimeRange(Times.startTimeOfWeek(year, weekOfYear), weekCount, calendar) {

  def this(year: Int, weekOfYear: Int, weekCount: Int) =
    this(year, weekOfYear, weekCount, DefaultTimeCalendar)

  def this(moment: DateTime, weekCount: Int) =
    this(moment.getYear, moment.getWeekOfWeekyear, weekCount, DefaultTimeCalendar)

  def this(moment: DateTime, weekCount: Int, calendar: ITimeCalendar) =
    this(moment.getYear, moment.getWeekOfWeekyear, weekCount, calendar)

  def weeksView: SeqView[WeekRange, Seq[_]] = {
    (0 until weekCount).view.map { w =>
      WeekRange(start.plusWeeks(w), calendar)
    }
  }

  def weeks: util.List[WeekRange] = {
    val weeks = Lists.newArrayListWithCapacity[WeekRange](weekCount)
    var w = 0
    while (w < weekCount) {
      weeks add WeekRange(start.plusWeeks(w), calendar)
      w += 1
    }
    weeks
  }
}

object WeekRangeCollection {

  def apply(year: Int, weekOfYear: Int, weekCount: Int): WeekRangeCollection =
    apply(year, weekOfYear, weekCount, DefaultTimeCalendar)

  def apply(year: Int, weekOfYear: Int, weekCount: Int, calendar: ITimeCalendar): WeekRangeCollection =
    new WeekRangeCollection(year, weekOfYear, weekCount, calendar)

  def apply(moment: DateTime, weekCount: Int): WeekRangeCollection =
    apply(moment, weekCount, DefaultTimeCalendar)

  def apply(moment: DateTime, weekCount: Int, calendar: ITimeCalendar): WeekRangeCollection =
    new WeekRangeCollection(moment.getYear, moment.getWeekOfWeekyear, weekCount, calendar)


}
