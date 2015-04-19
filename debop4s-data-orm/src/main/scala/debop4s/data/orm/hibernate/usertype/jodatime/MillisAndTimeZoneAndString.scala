package debop4s.data.orm.hibernate.usertype.jodatime

import java.sql.PreparedStatement

import debop4s.core.conversions.jodatime._
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.joda.time.DateTime

/**
 * Joda-Time 의 DateTime의 정보를 `Milliseconds`, `DateTimeZone`, DateTime의 ISO Format 문자열의 컬럼으로 분리하여 저장하도록 합니다.
 * 로드 시에는 해당 TimeZone으로 설정된 `DateTime` 을 반환합니다.
 *
 * Milliseconds, TimeZone, ISO Format 로 분리해서 저장하고, 로드 시에는 통합합니다.
 * {{{
 * `@Columns`( columns = { @Column(name = "startTime"), @Column(name = "startTimeZone"), @column(name="startTimeText") })
 * `@Type`( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.MillisAndTimeZoneAndString")
 *  private DateTime startTZ;
 *
 * `@Columns`( columns = { @Column(name = "endTime"), @Column(name = "endTimeZone"), @Column(name="endTimeAsText") })
 * `@Type`( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.MillisAndTimeZoneAndString")
 *  private DateTime endTZ;
 * }}}
 *
 * @author sunghyouk.bae@gmail.com
 */
class MillisAndTimeZoneAndString extends MillisAndTimeZone {

  override def sqlTypes(): Array[Int] = Array(StandardBasicTypes.LONG.sqlType(),
    StandardBasicTypes.STRING.sqlType(),
    StandardBasicTypes.STRING.sqlType())

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor): Unit = {
    val time = value.asInstanceOf[DateTime]
    if (time == null) {
      StandardBasicTypes.LONG.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 2, session)
    } else {
      val millis = time.getMillis
      val timezone = time.getZone.getID
      val timeStr = time.asIsoFormatDateHMS

      log.trace(s"save MillisAndTimeZoneAndString. value=$value, time=$time, " +
                s"millis=$millis, timezone=$timezone, timeStr=$timeStr")

      StandardBasicTypes.LONG.nullSafeSet(st, millis, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, timezone, index + 1, session)
      StandardBasicTypes.STRING.nullSafeSet(st, timeStr, index + 2, session)
    }
  }

}
