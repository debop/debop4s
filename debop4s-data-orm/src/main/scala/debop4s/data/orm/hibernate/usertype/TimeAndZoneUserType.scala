package debop4s.data.orm.hibernate.usertype

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet, Timestamp}

import debop4s.core.TimeAndZone
import debop4s.core.utils.Objects
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType

/**
 * `TimeAndZone` 정보를 DB에 저장할 수 있도록 해주는 Hibernate UserType 입니다.
 *
 * {{{
 *  `@Column`(columns= Array(new Column(name="startTimestamp"), new Column(name="startTimeZone")))
 *  `@org.hibernate.annotation.Type`(`type`="debop4s.data.orm.hibernate.usertype.TimeAndZoneUserType")
 *  var startTimeAndZone : TimeAndZone = _
 * }}}
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
class TimeAndZoneUserType extends UserType {

  def sqlTypes() = Array(StandardBasicTypes.TIMESTAMP.sqlType(), StandardBasicTypes.STRING.sqlType())

  def returnedClass() = classOf[TimeAndZone]

  def equals(x: Any, y: Any) = Objects.equals(x, y)

  def hashCode(x: Any) = Objects.hashCode(x)

  def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any) = {
    val timestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session, owner).asInstanceOf[Timestamp]
    val timezone = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session, owner).asInstanceOf[String]

    var value: TimeAndZone = null
    if (timezone == null) {
      value = new TimeAndZone(timestamp.getTime)
    } else {
      value = new TimeAndZone(timestamp.getTime, timezone)
    }
    value
  }

  def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) = {
    val timeAndZone = value.asInstanceOf[TimeAndZone]
    if (timeAndZone == null) {
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
    } else {
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, timeAndZone.getTime, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, timeAndZone.getZoneId, index + 1, session)
    }
  }

  def deepCopy(value: Any) = value.asInstanceOf[AnyRef]

  def isMutable = true

  def disassemble(value: Any) = deepCopy(value).asInstanceOf[Serializable]

  def assemble(cached: Serializable, owner: Any) = deepCopy(cached)

  def replace(original: Any, target: Any, owner: Any) = deepCopy(original)
}
