package debop4s.data.hibernate.usertype

import debop4s.timeperiod.{ TimeRange, ITimePeriod }
import java.io.Serializable
import java.sql.{ PreparedStatement, ResultSet }
import org.hibernate.`type`.{ StandardBasicTypes, TimestampType, Type }
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.CompositeUserType
import org.joda.time.DateTime

/**
 * [[ITimePeriod]] 정보를 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 3:26
 */
class TimeRangeUserType extends CompositeUserType {

  private def asTimePeriod(value: Any): ITimePeriod = {
    value match {
      case x: ITimePeriod => x.asInstanceOf[ITimePeriod]
      case _ => null
    }
  }

  override def getPropertyTypes: Array[Type] = Array(TimestampType.INSTANCE, TimestampType.INSTANCE)

  override def getPropertyNames: Array[String] = Array("startTime", "endTime")

  override def returnedClass(): Class[_] = classOf[TimeRange]

  override def getPropertyValue(component: Any, property: Int): AnyRef = {
    val timeRange = asTimePeriod(component)
    if (timeRange != null) {
      property match {
        case 0 => return timeRange.start
        case 1 => return timeRange.end
        case _ => null
      }
    }
    null
  }

  override def setPropertyValue(component: Any, property: Int, value: Any) {
    val period = asTimePeriod(component)
    property match {
      case 0 => period.setup(value.asInstanceOf[DateTime], period.end)
      case 1 => period.setup(period.start, value.asInstanceOf[DateTime])
      case _ =>
    }
  }

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    val start = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(0), session)
    val end = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names(1), session)

    TimeRange(
               if (start != null) new DateTime(start) else null,
               if (end != null) new DateTime(end) else null
             )
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
    val period = asTimePeriod(value)
    if (period == null) {
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session)
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index + 1, session)
    } else {
      val start = if (period.start != null) period.start.toDate else null
      val end = if (period.end != null) period.end.toDate else null
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, start, index, session)
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, end, index + 1, session)
    }
  }

  override def hashCode(x: Any): Int =
    if (x == null) 0 else x.hashCode()

  override def equals(x: Any, y: Any): Boolean =
    ( x == y ) || ( x != null && ( x == y ) )


  override def deepCopy(value: Any): AnyRef =
    if (value == null) null
    else TimeRange(asTimePeriod(value))

  override def replace(original: Any, target: Any, session: SessionImplementor, owner: Any): AnyRef =
    deepCopy(original)

  override def assemble(cached: Serializable, session: SessionImplementor, owner: Any): AnyRef =
    deepCopy(cached)

  override def disassemble(value: Any, session: SessionImplementor): Serializable =
    deepCopy(value).asInstanceOf[Serializable]

  override def isMutable: Boolean = true


}
