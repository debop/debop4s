package com.github.debop4s.data.hibernate.usertype

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import java.util.Date
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.{DateTime, LocalDateTime}

/**
 *  Joda-Time의 [[LocalDateTime]] 을 Timestamp 값으로 저장하고, 로드 시에는 LocalDateTime으로 변환하는 UserType입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 3:50
 */
class JodaLocalDateTimeUserType extends UserType {

  private def asLocalDateTime(value: Any): LocalDateTime = {
    value match {
      case x: LocalDateTime => x
      case x: DateTime => new LocalDateTime(x)
      case x: Date => new LocalDateTime(x)
      case x: Long => new LocalDateTime(x)
      case _ => null
    }
  }

  override def sqlTypes(): Array[Int] = Array(StandardBasicTypes.TIMESTAMP.sqlType())

  override def returnedClass(): Class[_] = classOf[LocalDateTime]

  override def hashCode(x: Any): Int = if (x != null) x.hashCode() else 0

  override def equals(x: Any, y: Any): Boolean = (x == y) || (x != null && (x == y))

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    val value = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session)
    asLocalDateTime(value)
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
    val x = asLocalDateTime(value)
    StandardBasicTypes.TIMESTAMP.nullSafeSet(st, if (x == null) null else x.toDate, index, session)
  }

  override def deepCopy(value: Any): AnyRef = value.asInstanceOf[AnyRef]

  override def replace(original: Any, target: Any, owner: Any): AnyRef = deepCopy(original)

  override def assemble(cached: Serializable, owner: Any): AnyRef = deepCopy(cached)

  override def disassemble(value: Any): Serializable = deepCopy(value).asInstanceOf[Serializable]

  override def isMutable: Boolean = true

}
