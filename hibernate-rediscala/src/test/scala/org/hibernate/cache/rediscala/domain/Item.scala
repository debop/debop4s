package org.hibernate.cache.rediscala.domain

import javax.persistence._

import org.hibernate.annotations.CacheConcurrencyStrategy

@Entity
@org.hibernate.annotations.Cache(region = "item", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Access(AccessType.FIELD)
@SerialVersionUID(5597936606448211014L)
class Item extends Serializable {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  var name: String = _

  var description: String = _


}
