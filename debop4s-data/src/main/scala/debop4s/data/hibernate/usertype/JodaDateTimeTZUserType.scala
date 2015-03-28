package debop4s.data.hibernate.usertype

import java.io.Serializable
import java.sql.{ ResultSet, PreparedStatement }
import java.util.Objects
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.{ DateTimeZone, DateTime }


/**
 * DateTime을 UTC Time 과 Timzone으로 분리하여 저장하고, 관리합니다.
 *
 * NOTE: MySql 의 DateTime은 milliseconds 를 지원하지 않습니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 9. 오후 10:18
 */
class JodaDateTimeTZUserType extends UserType {

  def sqlTypes() = Array(StandardBasicTypes.LONG.sqlType(), StandardBasicTypes.STRING.sqlType())

  def returnedClass() = classOf[DateTime]

  def equals(x: Any, y: Any) = Objects.equals(x, y)

  def hashCode(x: Any) = Objects.hashCode(x)

  def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any) = {
    val timestamp = StandardBasicTypes.LONG.nullSafeGet(rs, names(0), session, owner).asInstanceOf[Long]
    val timezone = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session, owner).asInstanceOf[String]

    var value: DateTime = null
    if (timezone == null) {
      value = new DateTime(timestamp)
    } else {
      value = new DateTime(timestamp, DateTimeZone.forID(timezone))
    }
    value
  }

  def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) = {
    val time = value.asInstanceOf[DateTime]
    if (time == null) {
      StandardBasicTypes.LONG.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
    } else {
      StandardBasicTypes.LONG.nullSafeSet(st, time.toDateTime(DateTimeZone.UTC).getMillis, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, time.getZone.getID, index + 1, session)
    }
  }

  def deepCopy(value: Any) = value.asInstanceOf[AnyRef]

  def isMutable = true

  def disassemble(value: Any) = deepCopy(value).asInstanceOf[Serializable]

  def assemble(cached: Serializable, owner: Any) = deepCopy(cached)

  def replace(original: Any, target: Any, owner: Any) = deepCopy(original)
}
