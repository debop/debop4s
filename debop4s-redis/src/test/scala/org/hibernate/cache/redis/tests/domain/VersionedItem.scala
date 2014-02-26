package org.hibernate.cache.redis.tests.domain

import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import scala.beans.BeanProperty


@Entity
@org.hibernate.annotations.Cache(region = "account", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Access(AccessType.FIELD)
class VersionedItem extends Serializable {

    @Id
    @GeneratedValue
    @BeanProperty
    var id: java.lang.Long = _

    @Version
    @BeanProperty
    var version: java.lang.Long = _

    var name: String = _

    var description: String = _

}
