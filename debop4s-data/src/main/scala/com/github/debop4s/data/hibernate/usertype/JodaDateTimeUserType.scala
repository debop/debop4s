package com.github.debop4s.data.hibernate.usertype

import java.io.Serializable
import java.sql.{ResultSet, PreparedStatement}
import java.util.{Objects, Date}
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * Joda-Time 라이브러리의 [[org.joda.time.DateTime]] 수형을 표현하는 UserType 입니다.
 * 저장 시에는 Timestamp 값이 저장되고, 로드 시에는 [[org.joda.time.DateTime]]으로 변환됩니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:52
 */
class JodaDateTimeUserType extends UserType {

    lazy val log = LoggerFactory.getLogger(getClass)

    private def asDateTime(value: Any): DateTime = {
        log.trace(s"DB 값을 DateTime으로 변환합니다. value=[$value]")
        value match {
            case x: java.lang.Long => new DateTime(x)
            case x: Long => new DateTime(x)
            case x: Date => new DateTime(x)
            case x: DateTime => new DateTime(x)
            case _ => null
        }
    }

    def sqlTypes(): Array[Int] = Array(StandardBasicTypes.TIMESTAMP.sqlType())

    def returnedClass(): Class[DateTime] = classOf[DateTime]

    def equals(x: Any, y: Any) = Objects.equals(x, y)

    def hashCode(x: Any) = Objects.hashCode(x)

    def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any) = {
        val value = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session, owner)
        asDateTime(value)
    }

    def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) = {
        val date = if (value == null) null else value.asInstanceOf[DateTime].toDate
        StandardBasicTypes.TIMESTAMP.nullSafeSet(st, date, index, session)
    }

    def deepCopy(value: Any) = value.asInstanceOf[AnyRef]

    def isMutable = true

    def disassemble(value: Any) = deepCopy(value).asInstanceOf[Serializable]

    def assemble(cached: Serializable, owner: Any) = deepCopy(cached)

    def replace(original: Any, target: Any, owner: Any) = deepCopy(original)
}
