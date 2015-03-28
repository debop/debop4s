package debop4s.timeperiod

import debop4s.core.ValueObject
import debop4s.core.jodatime._
import debop4s.core.utils.{ Options, ToStringHelper, Hashs }
import debop4s.timeperiod.DayOfWeek.DayOfWeek
import debop4s.timeperiod.utils.Times
import java.util.Locale
import org.joda.time.{ DateTime, Duration }

/**
 * debop4s.timeperiod.TimeCalendar
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 26. 오후 1:45
 */
trait ITimeCalendar extends ITimePeriodMapper {

  /** 문화권 정보 (문화권에 따라 달력에 대한 규칙 및 명칭이 달라집니다.) */
  def getLocale: Locale

  /** 시작 오프셋 (시작일자가 1월 1일이 아닌 경우) */
  def startOffset: Duration

  // /** 시작 오프셋 (시작일자가 1월 1일이 아닌 경우) */
  // def getStartOffset: Duration

  /** 종료 오프셋 */
  def endOffset: Duration

  // /** 종료 오프셋 */
  // def getEndOffset: Duration

  /** 한 주의 시작 요일 (한국, 미국: Sunday, ISO-8601: Monday) */
  val firstDayOfWeek: DayOfWeek = DayOfWeek.Monday

  /** 지정된 일자의 년 */
  def year(time: DateTime): Int = time.getYear

  /** 지정된 일자의 월 */
  def monthOfYear(time: DateTime): Int = time.getMonthOfYear

  /** 지정된 시각의 시간 */
  def hourOfDay(time: DateTime): Int = time.getHourOfDay

  /** 지정된 시각의 분 */
  def minuteOfHour(time: DateTime): Int = time.getMinuteOfHour

  /** 지정된 날짜의 월 몇번째 일인지 */
  def dayOfMonth(time: DateTime): Int = time.getDayOfMonth

  /** 지정된 날짜의 요일 */
  def dayOfWeek(time: DateTime): DayOfWeek = DayOfWeek(time.getDayOfWeek)

  /** 지정된 년,월의 날짜수 */
  def daysInMonth(year: Int, month: Int): Int = Times.daysInMonth(year, month)

  /** 지정된 일자의 주차(Week of Year)를 반환합니다. */
  def weekOfYear(time: DateTime): Int = Times.weekOfYear(time).weekOfWeekyear

  /** 지정된 년, 주차에 해당하는 주의 첫번째 일자를 반환한다. (예: 2011년 3주차의 첫번째 일자는?) */
  def startOfYearWeek(year: Int, weekOfYear: Int): DateTime = Times.startOfYearWeek(year, weekOfYear)

  def mapStart(moment: DateTime): DateTime =
    if (moment > MinPeriodTime) moment.plus(startOffset)
    else moment

  def mapEnd(moment: DateTime): DateTime =
    if (moment < MaxPeriodTime) moment.plus(endOffset)
    else moment

  def unmapStart(moment: DateTime): DateTime =
    if (moment.compareTo(MinPeriodTime) > 0) moment.minus(startOffset)
    else moment

  def unmapEnd(moment: DateTime): DateTime =
    if (moment.compareTo(MaxPeriodTime) < 0) moment.minus(endOffset)
    else moment
}

@SerialVersionUID(-8731693901249037388L)
class TimeCalendar(val cfg: TimeCalendarConfig) extends ValueObject with ITimeCalendar {

  def this(locale: Locale = Locale.getDefault,
           startOffset: Duration = DefaultStartOffset,
           endOffset: Duration = DefaultEndOffset) {
    this(new TimeCalendarConfig(locale, startOffset, endOffset))
  }

  require(cfg != null)
  require(cfg.startOffset != null && cfg.startOffset.millis >= 0, "startOffset must be greater than or equal zero.")
  require(cfg.endOffset != null && cfg.startOffset.millis <= 0, "startOffset must be less than or equal zero.")

  val locale: Locale = Options.toOption(cfg.locale).getOrElse(Locale.getDefault)
  val _startOffset: Duration = Options.toOption(cfg.startOffset).getOrElse(DefaultStartOffset)
  val _endOffset: Duration = Options.toOption(cfg.endOffset).getOrElse(DefaultEndOffset)
  override val firstDayOfWeek: DayOfWeek = cfg.firstDayOfWeek

  def getLocale: Locale = locale

  def startOffset: Duration = _startOffset

  def endOffset: Duration = _endOffset

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

  def getDefault: TimeCalendar = getDefault(Locale.getDefault)

  def getDefault(locale: Locale): TimeCalendar = {
    TimeCalendar(TimeCalendarConfig(locale))
    //        val config = new TimeCalendarConfig(locale)
    //        config.startOffset = DefaultStartOffset
    //        config.endOffset = DefaultEndOffset

    //        TimeCalendar(config)
  }

  def getEmptyOffset: TimeCalendar = getEmptyOffset(Locale.getDefault)

  def getEmptyOffset(locale: Locale): TimeCalendar = {
    new TimeCalendar(locale, EmptyDuration, EmptyDuration)
  }
}
