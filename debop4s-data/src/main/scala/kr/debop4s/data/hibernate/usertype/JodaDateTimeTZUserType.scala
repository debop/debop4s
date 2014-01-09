package kr.debop4s.data.hibernate.usertype

import java.io.Serializable
import java.sql.{Timestamp, Types, ResultSet, PreparedStatement}
import java.util.Objects
import kr.debop4s.core.logging.Logger
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.{DateTimeZone, DateTime}

/**
 * kr.debop4s.data.hibernate.usertype.JodaDateTimeTZUserType
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 9. 오후 10:18
 */
class JodaDateTimeTZUserType extends UserType {

    private lazy val log = Logger[JodaDateTimeTZUserType]

    def sqlTypes() = Array(Types.TIMESTAMP, Types.VARCHAR)
    def returnedClass() = classOf[DateTime]
    def equals(x: scala.Any, y: scala.Any) = Objects.equals(x, y)
    def hashCode(x: scala.Any) = Objects.hashCode(x)
    def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: scala.Any) = {
        val timestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session, owner).asInstanceOf[Timestamp]
        val timezone = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session, owner).asInstanceOf[String]

        var value: DateTime = null
        if (timezone == null) {
            value = new DateTime(timestamp)
        } else {
            value = new DateTime(timestamp, DateTimeZone.forID(timezone))
        }
        log.trace(s"Load timestamp=[$timestamp], timezone=[$timezone] => value=[$value]")

        value
    }
    def nullSafeSet(st: PreparedStatement, value: scala.Any, index: Int, session: SessionImplementor) = {
        val time = value.asInstanceOf[DateTime]
        log.trace(s"Save DateTime with TimeZone... time=[$time]")

        if (time == null) {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session)
            StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
        } else {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, time.toDate, index, session)
            StandardBasicTypes.STRING.nullSafeSet(st, time.getZone.getID, index + 1, session)
        }

    }
    def deepCopy(value: Any) = value.asInstanceOf[AnyRef]
    def isMutable = true
    def disassemble(value: scala.Any) = deepCopy(value).asInstanceOf[Serializable]
    def assemble(cached: Serializable, owner: scala.Any) = deepCopy(cached)
    def replace(original: scala.Any, target: scala.Any, owner: scala.Any) = deepCopy(original)
}
