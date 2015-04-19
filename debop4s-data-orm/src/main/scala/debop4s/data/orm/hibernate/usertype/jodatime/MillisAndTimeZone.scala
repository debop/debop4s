package debop4s.data.orm.hibernate.usertype.jodatime

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import java.util.Objects

import debop4s.core.utils.Strings
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * Joda-Time 의 DateTime의 정보를 `Milliseconds` 와 `DateTimeZone` 정보로 분리하여 저장하도록 합니다.
 * 로드 시에는 해당 TimeZone으로 설정된 `DateTime` 을 반환합니다.
 *
 * Timestamp의 Long 값과 TimeZone Id 로 분리해서 저장하고, 로드 시에는 통합합니다.
 * {{{
 * `@Columns`( columns = { @Column(name = "startMillis"), @Column(name = "startTimeZone") })
 * `@Type`( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.MillisAndTimeZone")
 *  private DateTime startTZ;
 *
 * `@Columns`( columns = { @Column(name = "endMillis"), @Column(name = "endTimeZone") })
 * `@Type`( `type` = "debop4s.data.orm.hibernate.usertype.jodatime.MillisAndTimeZone")
 *  private DateTime endTZ;
 * }}}
 *
 * @author sunghyouk.bae@gmail.com
 */
class MillisAndTimeZone extends UserType {

  protected lazy val log = LoggerFactory.getLogger(getClass)

  def sqlTypes() = Array(StandardBasicTypes.LONG.sqlType(), StandardBasicTypes.STRING.sqlType())

  def returnedClass() = classOf[DateTime]

  def equals(x: Any, y: Any) = Objects.equals(x, y)

  def hashCode(x: Any) = Objects.hashCode(x)

  def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any) = {
    val millis = StandardBasicTypes.LONG.nullSafeGet(rs, names(0), session, owner).asInstanceOf[Long]
    val timezone = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session, owner).asInstanceOf[String]

    log.trace(s"load MillisAndTimeZone. millis=$millis, timezone=$timezone")
    try {
      if (Strings.isEmpty(timezone))
        new DateTime(millis)
      else
        new DateTime(millis, DateTimeZone.forID(timezone))
    } catch {
      case NonFatal(e) =>
        log.warn(s"MillisAndTimeZone 값 로드하는데 실패했습니다. millis=$millis, timezone=$timezone", e)
        null
    }
  }

  def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) = {
    val time = value.asInstanceOf[DateTime]
    if (time == null) {
      StandardBasicTypes.LONG.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
    } else {
      log.trace(s"MillisAndTimeZone 값 저장. value=$value, time=$time")
      StandardBasicTypes.LONG.nullSafeSet(st, time.getMillis, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, time.getZone.getID, index + 1, session)
    }
  }

  def deepCopy(value: Any) = value.asInstanceOf[AnyRef]

  def isMutable = true

  def disassemble(value: Any) = deepCopy(value).asInstanceOf[Serializable]

  def assemble(cached: Serializable, owner: Any) = deepCopy(cached)

  def replace(original: Any, target: Any, owner: Any) = deepCopy(original)

}
