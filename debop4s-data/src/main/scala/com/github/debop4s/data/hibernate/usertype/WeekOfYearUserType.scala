package com.github.debop4s.data.hibernate.usertype

import com.github.debop4s.timeperiod.YearWeek
import java.io.Serializable
import java.sql.{ResultSet, PreparedStatement}
import org.hibernate.HibernateException
import org.hibernate.`type`.{IntegerType, Type}
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.CompositeUserType

/**
 * 주차 정보를 표현하는 UserType 입니
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 2:19
 */
class WeekOfYearUserType extends CompositeUserType {

    def asYearWeek(value: Any): YearWeek = value match {
        case yw: YearWeek => yw
        case _ => null
    }

    override def getPropertyNames: Array[String] = Array("weekyear", "weekOfYear")

    override def getPropertyTypes: Array[Type] = Array[Type](IntegerType.INSTANCE, IntegerType.INSTANCE)

    override def getPropertyValue(component: Any, property: Int): Int = {
        val yw = asYearWeek(component)

        property match {
            case 0 => yw.weekyear
            case 1 => yw.weekOfWeekyear
            case _ => throw new HibernateException(s"property index가 범위를 벗어났습니다. [0, 1]이어야 합니다. property=$property")
        }
    }

    override def setPropertyValue(component: Any, property: Int, value: Any) {
        val yw = asYearWeek(component)

        property match {
            case 0 => yw.weekyear = value.asInstanceOf[Int]
            case 1 => yw.weekOfWeekyear = value.asInstanceOf[Int]
            case _ =>
        }
    }

    override def returnedClass(): Class[_] = classOf[YearWeek]

    override def equals(x: Any, y: Any): Boolean = (x == y) || (x != null && (x == y))

    override def hashCode(x: Any): Int = if (x != null) x.hashCode() else 0

    override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
        val weekyear = IntegerType.INSTANCE.nullSafeGet(rs, names(0), session)
        val weekOfWeekyear = IntegerType.INSTANCE.nullSafeGet(rs, names(1), session)

        YearWeek(weekyear, weekOfWeekyear)
    }

    override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
        val yw = asYearWeek(value)
        if (yw == null) {
            IntegerType.INSTANCE.nullSafeSet(st, null, index, session)
            IntegerType.INSTANCE.nullSafeSet(st, null, index + 1, session)
        } else {
            IntegerType.INSTANCE.nullSafeSet(st, yw.weekyear, index, session)
            IntegerType.INSTANCE.nullSafeSet(st, yw.weekOfWeekyear, index + 1, session)
        }
    }

    override def deepCopy(value: Any): AnyRef =
        if (value == null) null else YearWeek(asYearWeek(value))

    override def isMutable: Boolean = true

    override def disassemble(value: Any, session: SessionImplementor): Serializable =
        deepCopy(value).asInstanceOf[Serializable]

    override def assemble(cached: Serializable, session: SessionImplementor, owner: Any): AnyRef =
        deepCopy(cached)

    override def replace(original: Any, target: Any, session: SessionImplementor, owner: Any): AnyRef =
        deepCopy(original)
}
