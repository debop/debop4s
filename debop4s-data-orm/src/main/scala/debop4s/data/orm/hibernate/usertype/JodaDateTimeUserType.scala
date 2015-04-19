package debop4s.data.orm.hibernate.usertype

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet, Timestamp}
import java.util.{Date, Objects}

import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.DateTime

/**
 * Joda-Time 라이브러리의 [[org.joda.time.DateTime]] 수형을 표현하는 UserType 입니다.
 * 저장 시에는 Timestamp 값이 저장되고, 로드 시에는 [[org.joda.time.DateTime]]으로 변환됩니다.
 *
 * NOTE: MySql 의 DateTime은 milliseconds 를 지원하지 않습니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:52
 */
class JodaDateTimeUserType extends UserType {

  private def asDateTime(value: Any): DateTime = {
    value match {
      case x: java.lang.Long => new DateTime(x)
      case x: Long => new DateTime(x)
      case x: Date => new DateTime(x)
      case x: Timestamp => new DateTime(x)
      case x: DateTime => new DateTime(x)
      case _ => null
    }
  }

  def sqlTypes(): Array[Int] = Array(StandardBasicTypes.TIMESTAMP.sqlType())

  def returnedClass(): Class[DateTime] = classOf[DateTime]

  def equals(x: Any, y: Any) = Objects.equals(x, y)

  def hashCode(x: Any) = Objects.hashCode(x)

  def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any) = {
    val value = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session, owner).asInstanceOf[Timestamp]
    asDateTime(value)
  }

  def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) = {
    val timestamp = value match {
      case x: DateTime => new Timestamp(x.getMillis)
      case _ => null
    }
    StandardBasicTypes.TIMESTAMP.nullSafeSet(st, timestamp, index, session)
  }

  def deepCopy(value: Any) = value.asInstanceOf[AnyRef]

  def isMutable = true

  def disassemble(value: Any) = deepCopy(value).asInstanceOf[Serializable]

  def assemble(cached: Serializable, owner: Any) = deepCopy(cached)

  def replace(original: Any, target: Any, owner: Any) = deepCopy(original)
}
