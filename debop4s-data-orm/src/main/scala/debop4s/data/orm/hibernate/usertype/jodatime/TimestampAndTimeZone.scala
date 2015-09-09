package debop4s.data.orm.hibernate.usertype.jodatime

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet, Timestamp}
import java.util.Objects

import debop4s.core.utils.Strings
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * Joda-Time 의 DateTime의 정보를 `Timestamp`과 `DateTimeZone` 정보로 분리하여 저장하도록 합니다.
 * 로드 시에는 해당 TimeZone으로 설정된 `DateTime` 을 반환합니다.
 *
 * Timestamp 와 TimeZone 으로 분리해서 저장하고, 로드 시에는 통합합니다.
 * {{{
 * `@Columns`( columns = { @Column(name = "startTime"), @Column(name = "startTimeZone") })
 * `@Type`( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZone")
 *  private DateTime startTZ;
 *
 * `@Columns`( columns = { @Column(name = "endTime"), @Column(name = "endTimeZone") })
 * `@Type`( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZone")
 *  private DateTime endTZ;
 * }}}
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 9. 오전 12:43
 */
class TimestampAndTimeZone extends UserType {

  protected lazy val log = LoggerFactory.getLogger(getClass)

  def sqlTypes() = Array(StandardBasicTypes.TIMESTAMP.sqlType(), StandardBasicTypes.STRING.sqlType())

  def returnedClass() = classOf[DateTime]

  def equals(x: Any, y: Any) = Objects.equals(x, y)

  def hashCode(x: Any) = Objects.hashCode(x)

  def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any) = {
    val timestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session, owner).asInstanceOf[Timestamp]
    val timezone = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session, owner).asInstanceOf[String]

    log.trace(s"load timestamp and timezone. timestamp=$timestamp, timezone=$timezone")

    if (timestamp == null)
      return null.asInstanceOf[DateTime]

    try {
      if (Strings.isEmpty(timezone))
        new DateTime(timestamp)
      else
        new DateTime(timestamp, DateTimeZone.forID(timezone))
    } catch {
      case NonFatal(e) =>
        log.warn(s"datetime을 생성하는데 실패했습니다. timestamp=$timestamp, timezone=$timezone", e)
        null.asInstanceOf[DateTime]
    }
  }

  def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) = {
    val time = value.asInstanceOf[DateTime]
    if (time == null) {
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
    } else {
      log.trace(s"save timestamp and timezone. value=$value, time=$time")
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, new Timestamp(time.getMillis), index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, time.getZone.getID, index + 1, session)
    }
  }

  def deepCopy(value: Any) = value.asInstanceOf[AnyRef]

  def isMutable = true

  def disassemble(value: Any) = deepCopy(value).asInstanceOf[Serializable]

  def assemble(cached: Serializable, owner: Any) = deepCopy(cached)

  def replace(original: Any, target: Any, owner: Any) = deepCopy(original)
}
