package debop4s.data.hibernate.usertype

import java.io.Serializable
import java.sql.{ResultSet, PreparedStatement}
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType

/**
 * AbstractEnumerationUserType
 * @author Sunghyouk Bae
 */
class AbstractEnumerationUserType(val enum: Enumeration) extends UserType {

  override def sqlTypes(): Array[Int] = ???

  override def returnedClass(): Class[_] = ???

  override def equals(x: scala.Any, y: scala.Any): Boolean = ???

  override def hashCode(x: scala.Any): Int = ???

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: scala.Any): AnyRef = ???

  override def nullSafeSet(st: PreparedStatement, value: scala.Any, index: Int, session: SessionImplementor): Unit = ???

  override def deepCopy(value: scala.Any): AnyRef = ???

  override def isMutable: Boolean = ???

  override def disassemble(value: scala.Any): Serializable = ???

  override def assemble(cached: Serializable, owner: scala.Any): AnyRef = ???

  override def replace(original: scala.Any, target: scala.Any, owner: scala.Any): AnyRef = ???
}
