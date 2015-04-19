package debop4s.data.orm.hibernate.usertype.jodatime

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import java.util.Objects

import debop4s.core.conversions.jodatime._
import debop4s.core.jodatime.JodaISODateTimeFormat
import debop4s.core.utils.Strings
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * Joda-Time 라이브러리의 [[org.joda.time.DateTime]] 수형을 Database에 ISO8601 포맷 중 초단위까지만 저장합니다.
 * 로컬 시각 그대로 저장할 때 사용합니다.
 *
 * Format : YYYY-MM-DD'T'HH:mm:ss 문자열로 저장됩니다.
 *
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
class DateTimeAsIsoFormatHMS extends UserType {

  private lazy val log = LoggerFactory.getLogger(getClass)

  override def sqlTypes() = Array(StandardBasicTypes.STRING.sqlType())
  override def returnedClass() = classOf[DateTime]
  override def equals(x: Any, y: Any) = Objects.equals(x, y)
  override def hashCode(x: Any): Int = Objects.hashCode(x)

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    val value = StandardBasicTypes.STRING.nullSafeGet(rs, names(0), session, owner).asInstanceOf[String]

    log.trace(s"load HMS. value=$value")
    if (Strings.isEmpty(value)) {
      null.asInstanceOf[AnyRef]
    } else {
      DateTime.parse(value, JodaISODateTimeFormat.dateHourMinuteSecond)
    }
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor): Unit = {
    val dt = value.asInstanceOf[DateTime]

    if (dt == null) {
      StandardBasicTypes.STRING.nullSafeSet(st, null, index, session)
    } else {
      val hms = dt.asIsoFormatDateHMS
      log.trace(s"save HMS. value=$value, dt=$dt, hms=$hms")
      StandardBasicTypes.STRING.nullSafeSet(st, hms, index, session)
    }
  }

  override def deepCopy(value: Any): AnyRef = value.asInstanceOf[AnyRef]
  override def isMutable: Boolean = true

  override def disassemble(value: Any): Serializable = deepCopy(value).asInstanceOf[Serializable]
  override def assemble(cached: Serializable, owner: Any): AnyRef = deepCopy(cached)
  override def replace(original: Any, target: Any, owner: Any): AnyRef = deepCopy(original)
}
