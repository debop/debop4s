package org.hibernate.cache.rediscala.domain

import javax.persistence._

import org.hibernate.annotations.CacheConcurrencyStrategy

import scala.beans.BeanProperty


@Entity
@org.hibernate.annotations.Cache(region = "versionedItem", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Access(AccessType.FIELD)
class VersionedItem extends Serializable {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: java.lang.Long = _

  @Version
  @BeanProperty
  var version: Long = _

  var name: String = _

  var description: String = _

}
