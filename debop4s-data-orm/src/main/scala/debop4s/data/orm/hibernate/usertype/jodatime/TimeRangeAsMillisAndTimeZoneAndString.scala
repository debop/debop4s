package debop4s.data.orm.hibernate.usertype.jodatime

import java.sql.{PreparedStatement, ResultSet}

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.utils.Times
import debop4s.timeperiod.{ITimePeriod, TimeRange, TimeSpec}
import org.hibernate.`type`._
import org.hibernate.engine.spi.SessionImplementor
import org.joda.time.DateTime

/**
 * [[ITimePeriod]] 정보중 시작일자와 완료일자를 Milliseconds, TimezoneId, TimeText (IsoFormatHMS) 값으로 분리 저장하도록 합니다.
 *
 * {{{
 * @Columns( columns = Array(
 * new Column(name = "startTimestamp"),
 * new Column(name="startTimeZoneId"),
 * new Column(name="startTimeText"),
 * new Column(name = "endTimestamp"),
 * new Column(name="endTimeZoneId")
 * new Column(name="endTimeText"),
 * )
 * )
 * @hba.Type( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeRangeAsMillisAndTimeZoneAndString")
 * var range1: ITimePeriod = _
 * }}}
 *
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
class TimeRangeAsMillisAndTimeZoneAndString extends TimeRangeAsMillisAndTimeZone {

  override def getPropertyTypes: Array[Type] =
    Array(LongType.INSTANCE, StringType.INSTANCE, StringType.INSTANCE,
      LongType.INSTANCE, StringType.INSTANCE, StringType.INSTANCE)

  override def getPropertyNames: Array[String] =
    Array("startTimestamp", "startTimeZone", "startTimeText",
      "endTimestamp", "endTimeZone", "endTimeText")

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    val startTimestamp = StandardBasicTypes.LONG.nullSafeGet(rs, names(0), session).asInstanceOf[java.lang.Long]
    val startTimeZoneId = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session).asInstanceOf[String]
    // startTimeText는 읽을 필요 없다.
    val endTimestamp = StandardBasicTypes.LONG.nullSafeGet(rs, names(3), session).asInstanceOf[java.lang.Long]
    val endTimeZoneId = StandardBasicTypes.STRING.nullSafeGet(rs, names(4), session).asInstanceOf[String]
    // endTimeText는 읽을 필요 없다.

    log.trace(s"load timerange. " +
              s"startTimestamp=$startTimestamp, startTimeZoneId=$startTimeZoneId, " +
              s"endTimestamp=$endTimestamp, endTimeZoneId=$endTimeZoneId")

    val start: DateTime =
      if (startTimestamp != null) Times.asDateTime(startTimestamp, startTimeZoneId)
      else TimeSpec.MinPeriodTime

    val end: DateTime =
      if (endTimestamp != null) Times.asDateTime(endTimestamp, endTimeZoneId)
      else TimeSpec.MaxPeriodTime

    log.trace(s"parse range. start=$start, end=$end")
    TimeRange(start, end)
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor): Unit = {
    val period = asTimePeriod(value)
    if (period == null) {
      StandardBasicTypes.LONG.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
      StandardBasicTypes.LONG.nullSafeSet(st, null, index + 2, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 3, session)
    } else {
      val startTimestamp = if (period.hasStart) period.start.getMillis else null
      val startTimeZoneId = if (period.hasStart) period.start.getZone.getID else null
      val startTimeText = if (period.hasStart) period.start.asIsoFormatDateHMS else null
      val endTimestamp = if (period.hasEnd) period.end.getMillis else null
      val endTimeZoneId = if (period.hasEnd) period.end.getZone.getID else null
      val endTimeText = if (period.hasEnd) period.end.asIsoFormatDateHMS else null

      log.trace(s"save timerange. value=$value, " +
                s"startTimestamp=$startTimestamp, startTimeZoneId=$startTimeZoneId, startTimeText=$startTimeText, " +
                s"endTimestamp=$endTimestamp, endTimeZoneId=$endTimeZoneId, endTimeText=$endTimeText")

      StandardBasicTypes.LONG.nullSafeSet(st, startTimestamp, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, startTimeZoneId, index + 1, session)
      StandardBasicTypes.STRING.nullSafeSet(st, startTimeText, index + 2, session)
      StandardBasicTypes.LONG.nullSafeSet(st, endTimestamp, index + 3, session)
      StandardBasicTypes.STRING.nullSafeSet(st, endTimeZoneId, index + 4, session)
      StandardBasicTypes.STRING.nullSafeSet(st, endTimeText, index + 5, session)
    }
  }
}
