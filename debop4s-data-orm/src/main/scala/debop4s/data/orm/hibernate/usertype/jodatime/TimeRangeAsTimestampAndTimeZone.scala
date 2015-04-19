package debop4s.data.orm.hibernate.usertype.jodatime

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet, Timestamp}
import java.util.Objects

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.utils.Times
import debop4s.timeperiod.{ITimePeriod, TimeRange, TimeSpec}
import org.hibernate.`type`._
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.CompositeUserType
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * [[ITimePeriod]] 정보중 시작일자와 완료일자를 Timestamp 와 TimezoneId 값만 저장하도록 합니다.
 *
 * {{{
 * @Columns( columns = Array(
 * new Column(name = "startTimestamp"),
 * new Column(name="startTimeZoneId"),
 * new Column(name = "endTimestamp"),
 * new Column(name="endTimeZoneId")
 * )
 * )
 * @hba.Type( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeRangeAsTimestampAndTimeZone")
 * var range1: ITimePeriod = _
 * }}}
 *
 * @author sunghyouk.bae@gmail.com
 */
class TimeRangeAsTimestampAndTimeZone extends CompositeUserType {

  protected lazy val log = LoggerFactory.getLogger(getClass)

  protected def asTimePeriod(value: Any): ITimePeriod = value match {
    case x: ITimePeriod => x
    case _ => null
  }

  override def getPropertyTypes: Array[Type] = Array(TimestampType.INSTANCE, StringType.INSTANCE,
    TimestampType.INSTANCE, StringType.INSTANCE)

  override def getPropertyNames: Array[String] = Array("startTimestamp", "startTimeZone",
    "endTimestamp", "endTimeZone")

  override def returnedClass(): Class[_] = classOf[TimeRange]

  override def getPropertyValue(component: Any, property: Int): AnyRef = {
    val period = asTimePeriod(component)
    if (period != null) {
      property match {
        case 0 => period.start
        case 1 => period.end
        case _ => null
      }
    } else null
  }

  override def setPropertyValue(component: Any, property: Int, value: Any): Unit = {
    val period = asTimePeriod(component)

    if (period != null) {
      property match {
        case 0 => period.setup(value.asInstanceOf[DateTime], period.end)
        case 1 => period.setup(period.start, value.asInstanceOf[DateTime])
        case _ =>
      }
    }
  }

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    val startTimestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session).asInstanceOf[Timestamp]
    val startTimeZoneId = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session).asInstanceOf[String]
    val endTimestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(2), session).asInstanceOf[Timestamp]
    val endTimeZoneId = StandardBasicTypes.STRING.nullSafeGet(rs, names(3), session).asInstanceOf[String]

    log.trace(s"load timerange. " +
              s"startTimestamp=$startTimestamp, startTimeZoneId=$startTimeZoneId, " +
              s"endTimestamp=$endTimestamp, endTimeZoneId=$endTimeZoneId")

    val start: DateTime =
      if (startTimestamp != null) Times.asDateTime(startTimestamp.getTime, startTimeZoneId)
      else TimeSpec.MinPeriodTime
    val end: DateTime =
      if (endTimestamp != null) Times.asDateTime(endTimestamp.getTime, endTimeZoneId)
      else TimeSpec.MaxPeriodTime

    log.trace(s"parse time. start=$start, end=$end")

    TimeRange(start, end)
  }
  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor): Unit = {
    val period = asTimePeriod(value)
    if (period == null) {
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index + 2, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 3, session)
    } else {
      val startTimestamp = if (period.hasStart) period.start.asTimestamp else null
      val startTimeZoneId = if (period.hasStart) period.start.getZone.getID else null
      val endTimestamp = if (period.hasEnd) period.end.asTimestamp else null
      val endTimeZoneId = if (period.hasEnd) period.end.getZone.getID else null

      log.trace(s"save timerange. value=$value, period=$period, " +
                s"startTimestamp=$startTimestamp, startTimeZoneId=$startTimeZoneId, " +
                s"endTimestamp=$endTimestamp, endTimeZoneId=$endTimeZoneId")

      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, startTimestamp, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, startTimeZoneId, index + 1, session)
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, endTimestamp, index + 2, session)
      StandardBasicTypes.STRING.nullSafeSet(st, endTimeZoneId, index + 3, session)
    }
  }

  override def equals(x: Any, y: Any): Boolean = Objects.equals(x, y)
  override def hashCode(x: Any): Int = Objects.hashCode(x)

  override def deepCopy(value: Any): AnyRef =
    if (value == null) null
    else TimeRange(asTimePeriod(value))

  override def replace(original: Any, target: Any, session: SessionImplementor, owner: Any): AnyRef =
    deepCopy(original)

  override def assemble(cached: Serializable, session: SessionImplementor, owner: Any): AnyRef =
    deepCopy(cached)

  override def disassemble(value: Any, session: SessionImplementor): Serializable =
    deepCopy(value).asInstanceOf[Serializable]

  override def isMutable: Boolean = true
}
