package debop4s.data.orm.hibernate.usertype.jodatime

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import java.util.Objects

import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.DateTimeZone
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * joda-time 의 `DateTimeZone` 을 DB에 TimeZone ID와 offset (msec)으로 저장하도록 한다.
 *
 * DateTimeZone 값을 Id 와 offset으로 분리해서 저장한다.
 * {{{
 *   `@Columns`(columns = { @Column(name="zoneId"), @Column(name="offset") })
 *   `@Type`(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeZoneAsIdAndOffset")
 *   private DateTimeZone timezone;
 * }}}
 *
 *
 * @author sunghyouk.bae@gmail.com
 */
class TimeZoneAsIdAndOffset extends UserType {

  private val log = LoggerFactory.getLogger(getClass)

  override def sqlTypes(): Array[Int] = Array(StandardBasicTypes.STRING.sqlType(), StandardBasicTypes.INTEGER.sqlType())
  override def returnedClass(): Class[_] = classOf[DateTimeZone]
  override def equals(x: Any, y: Any): Boolean = Objects.equals(x, y)
  override def hashCode(x: Any): Int = Objects.hashCode(x)

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    val zoneId = StandardBasicTypes.STRING.nullSafeGet(rs, names(0), session, owner).asInstanceOf[String]
    val offset = StandardBasicTypes.INTEGER.nullSafeGet(rs, names(1), session, owner).asInstanceOf[Integer]

    log.trace(s"load TimeZone Id and Offset. zoneId=$zoneId, offset=$offset")
    try {
      DateTimeZone.forID(zoneId)
    } catch {
      case NonFatal(e) =>
        log.warn(s"TimeZone id and offset 를 로드하는데 실패했습니다. zoneId=$zoneId, offset=$offset", e)
        DateTimeZone.getDefault
    }
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor): Unit = {
    log.trace(s"save DateTimeZone. value=$value")
    val timezone = value.asInstanceOf[DateTimeZone]
    if (timezone == null) {
      StandardBasicTypes.STRING.nullSafeSet(st, null, index, session)
      StandardBasicTypes.INTEGER.nullSafeSet(st, null, index + 1, session)
    } else {
      StandardBasicTypes.STRING.nullSafeSet(st, timezone.getID, index, session)
      StandardBasicTypes.INTEGER.nullSafeSet(st, timezone.getOffset(0L), index + 1, session)
    }
  }

  override def deepCopy(value: Any): AnyRef = value.asInstanceOf[AnyRef]
  override def isMutable: Boolean = true
  override def disassemble(value: Any): Serializable = deepCopy(value).asInstanceOf[Serializable]
  override def assemble(cached: Serializable, owner: Any): AnyRef = deepCopy(cached)
  override def replace(original: Any, target: Any, owner: Any): AnyRef = deepCopy(original)
}
