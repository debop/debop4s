package com.github.debop4s.data.hibernate.usertype

import com.github.debop4s.core.utils.Options
import com.github.debop4s.timeperiod.{TimeRange, ITimePeriod}
import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import org.hibernate.`type`.{StandardBasicTypes, TimestampType, Type}
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.CompositeUserType
import org.joda.time.DateTime

/**
 * com.github.debop4s.data.hibernate.usertype.TimeRangeUserType 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 3:26
 */
class TimeRangeUserType extends CompositeUserType {

    private def asTimeRange(value: Any): ITimePeriod = {
        value match {
            case x: ITimePeriod => x.asInstanceOf[ITimePeriod]
            case _ => null
        }
    }

    override def getPropertyTypes: Array[Type] = Array(TimestampType.INSTANCE, TimestampType.INSTANCE)

    override def getPropertyNames: Array[String] = Array("startTime", "endTime")

    override def returnedClass(): Class[_] = classOf[TimeRange]

    override def getPropertyValue(component: Any, property: Int): AnyRef = {
        property match {

            case 0 => asTimeRange(component).start
            case 1 => asTimeRange(component).end
            case _ => null
        }
    }

    override def setPropertyValue(component: Any, property: Int, value: Any) {
        val range = asTimeRange(component)
        property match {
            case 0 => range.setup(value.asInstanceOf[DateTime], range.end)
            case 1 => range.setup(range.start, value.asInstanceOf[DateTime])
            case _ =>
        }
    }

    override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
        val start = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session)
        val end = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(1), session)

        TimeRange(Options.toOption(start).map(new DateTime(_)),
                     Options.toOption(end).map(new DateTime(_)))
    }

    override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
        val range = asTimeRange(value)
        if (range == null) {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session)
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index + 1, session)
        } else {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, range.start, index, session)
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, range.end, index + 1, session)
        }
    }

    override def hashCode(x: Any): Int =
        if (x == null) 0 else x.hashCode()

    override def equals(x: Any, y: Any): Boolean =
        (x == y) || (x != null && (x == y))


    override def deepCopy(value: Any): AnyRef =
        if (value == null) null
        else TimeRange(asTimeRange(value))

    override def replace(original: Any, target: Any, session: SessionImplementor, owner: Any): AnyRef =
        deepCopy(original)

    override def assemble(cached: Serializable, session: SessionImplementor, owner: Any): AnyRef =
        deepCopy(cached)

    override def disassemble(value: Any, session: SessionImplementor): Serializable =
        deepCopy(value).asInstanceOf[Serializable]

    override def isMutable: Boolean = true


}
