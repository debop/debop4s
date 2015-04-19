package debop4s.data.orm.model

import javax.persistence.{Access, AccessType, Entity, Id}

import scala.beans.BeanProperty

/**
 * StringEntity
 * @author sunghyouk.bae@gmail.com
 */
@Entity
@Access(AccessType.FIELD)
class StringEntity extends HibernateEntityBase[String] {

  @Id
  @BeanProperty
  var id: String = _

}
