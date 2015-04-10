package debop4s.data.slick3.model

import java.util.UUID

/**
 * DDD의 Entity 를 나타내는 trait 입니다. (당연히 Identifier를 가지고 있습니다)
 *
 * @author sunghyouk.bae@gmail.com
 */
trait SlickEntity[Id] extends Serializable {

  def id: Option[Id]

  def id_=(newId: Option[Id])

  def withId(id: Id): this.type = {
    this.id = Option(id)
    this
  }
  def isPersisted: Boolean = id match {
    case Some(_) => true
    case None => false
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case vo: SlickEntity[_] => ( this.getClass == obj.getClass ) && ( this.hashCode() == obj.hashCode() )
      case _ => false
    }
  }

  override def hashCode: Int = id match {
    case Some(id) => id.hashCode()
    case None => 0
  }

  // override def toString: String = s"${ this.getClass.getSimpleName }#id=$id"
}

trait IntEntity extends SlickEntity[Int]
trait LongEntitiy extends SlickEntity[Long]
trait UuidEntity extends SlickEntity[UUID]
trait StringEntity extends SlickEntity[String]

trait Versionable extends Serializable {
  def version: Long
  def version_=(newVersion: Long)
  def withVersion(version: Long): this.type = {
    this.version = version
    this
  }
}