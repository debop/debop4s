package kr.debop4s.timeperiod

import java.util.Locale
import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.{Options, ToStringHelper, Hashs}
import kr.debop4s.timeperiod.DayOfWeek.DayOfWeek
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, Duration}

/**
 * kr.debop4s.timeperiod.TimeCalendar
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:45
 */
trait ITimeCalendar extends ITimePeriodMapper {

    /** 문화권 정보 (문화권에 따라 달력에 대한 규칙 및 명칭이 달라집니다.) */
    def getLocale: Locale

    /** 시작 오프셋 (시작일자가 1월 1일이 아닌 경우) */
    def getStartOffset: Duration

    /** 종료 오프셋 */
    def getEndOffset: Duration

    /** 한 주의 시작 요일 (한국, 미국: Sunday, ISO-8601: Monday) */
    def getFirstDayOfWeek: DayOfWeek = DayOfWeek.Monday

    /** 지정된 일자의 년 */
    def getYear(time: DateTime): Int = time.getYear

    /** 지정된 일자의 월 */
    def getMonthOfYear(time: DateTime): Int = time.getMonthOfYear

    /** 지정된 시각의 시간 */
    def getHourOfDay(time: DateTime): Int = time.getHourOfDay

    /** 지정된 시각의 분 */
    def getMinuteOfHour(time: DateTime): Int = time.getMinuteOfHour

    /** 지정된 날짜의 월 몇번째 일인지 */
    def getDayOfMonth(time: DateTime): Int = time.getDayOfMonth

    /** 지정된 날짜의 요일 */
    def getDayOfWeek(time: DateTime): DayOfWeek = DayOfWeek(time.getDayOfWeek)

    /** 지정된 년,월의 날짜수 */
    def getDaysInMonth(year: Int, month: Int): Int = Times.getDaysInMonth(year, month)

    /** 지정된 일자의 주차(Week of Year)를 반환합니다. */
    def getWeekOfYear(time: DateTime): Int = Times.getWeekOfYear(time).weekOfYear

    /** 지정된 년, 주차에 해당하는 주의 첫번째 일자를 반환한다. (예: 2011년 3주차의 첫번째 일자는?) */
    def getStartOfYearWeek(year: Int, weekOfYear: Int): DateTime = Times.getStartOfYearWeek(year, weekOfYear)

    def mapStart(moment: DateTime): DateTime =
        if (moment.compareTo(MinPeriodTime) > 0) moment.plus(getStartOffset)
        else moment

    def mapEnd(moment: DateTime): DateTime =
        if (moment.compareTo(MaxPeriodTime) < 0) moment.plus(getEndOffset)
        else moment

    def unmapStart(moment: DateTime): DateTime =
        if (moment.compareTo(MinPeriodTime) > 0) moment.minus(getStartOffset)
        else moment

    def unmapEnd(moment: DateTime): DateTime =
        if (moment.compareTo(MaxPeriodTime) < 0) moment.minus(getEndOffset)
        else moment
}

@SerialVersionUID(-8731693901249037388L)
class TimeCalendar(val cfg: TimeCalendarConfig) extends ValueObject with ITimeCalendar {

    def this(locale: Locale = Locale.getDefault,
             startOffset: Duration = DefaultStartOffset,
             endOffset: Duration = DefaultEndOffset) {
        this(new TimeCalendarConfig(locale, startOffset, endOffset))
    }

    assert(cfg != null)
    assert(cfg.startOffset != null && cfg.startOffset.getMillis >= 0, "startOffset must be greater than or equal zero.")
    assert(cfg.endOffset != null && cfg.startOffset.getMillis <= 0, "startOffset must be less than or equal zero.")

    val locale: Locale = Options.get(cfg.locale).getOrElse(Locale.getDefault)
    val startOffset: Duration = Options.get(cfg.startOffset).getOrElse(DefaultStartOffset)
    val endOffset: Duration = Options.get(cfg.endOffset).getOrElse(DefaultEndOffset)
    val firstDayOfWeek: DayOfWeek = cfg.firstDayOfWeek

    def getLocale: Locale = locale

    def getStartOffset: Duration = startOffset

    def getEndOffset: Duration = endOffset

    override def hashCode(): Int =
        Hashs.compute(locale, startOffset, endOffset, firstDayOfWeek)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
            .add("locale", locale)
            .add("startOffset", startOffset)
            .add("endOffset", endOffset)
            .add("firstDayOfWeek", firstDayOfWeek)
}

object TimeCalendar {

    def apply(): TimeCalendar = apply(Locale.getDefault)

    def apply(locale: Locale = Locale.getDefault,
              startOffset: Duration = DefaultStartOffset,
              endOffset: Duration = DefaultEndOffset): TimeCalendar = {
        new TimeCalendar(new TimeCalendarConfig(locale, startOffset, endOffset))
    }

    def apply(cfg: TimeCalendarConfig): TimeCalendar = {
        new TimeCalendar(cfg)
    }

    def getEmptyOffset: TimeCalendar = getEmptyOffset(Locale.getDefault)

    def getEmptyOffset(locale: Locale): TimeCalendar = {
        new TimeCalendar(locale, EmptyDuration, EmptyDuration)
    }
}
