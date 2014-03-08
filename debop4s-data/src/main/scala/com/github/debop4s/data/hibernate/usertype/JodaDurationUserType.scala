package com.github.debop4s.data.hibernate.usertype

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.{Duration, LocalDateTime}

/**
 *  Joda-Time의 [[Duration]] 을 Timestamp 값으로 저장하고, 로드 시에는 LocalDateTime으로 변환하는 UserType입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 4:00
 */
class JodaDurationUserType extends UserType {

  private def asDuration(value: Any): Duration = {
    value match {
      case x: Duration => x
      case x: Long => new Duration(x)
      case _ => null
    }
  }

  override def sqlTypes(): Array[Int] = Array(StandardBasicTypes.LONG.sqlType())

  override def returnedClass(): Class[_] = classOf[LocalDateTime]

  override def hashCode(x: Any): Int = if (x != null) x.hashCode() else 0

  override def equals(x: Any, y: Any): Boolean = (x == y) || (x != null && (x == y))

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    val value = StandardBasicTypes.LONG.nullSafeGet(rs, names(0), session)
    asDuration(value)
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
    val duration = asDuration(value)
    val x = if (duration == null) null else duration.getMillis
    StandardBasicTypes.TIMESTAMP.nullSafeSet(st, x, index, session)
  }

  override def deepCopy(value: Any): AnyRef = value.asInstanceOf[AnyRef]

  override def replace(original: Any, target: Any, owner: Any): AnyRef = deepCopy(original)

  override def assemble(cached: Serializable, owner: Any): AnyRef = deepCopy(cached)

  override def disassemble(value: Any): Serializable = deepCopy(value).asInstanceOf[Serializable]

  override def isMutable: Boolean = true
}
