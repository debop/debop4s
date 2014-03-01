package org.hibernate.cache.redis.tests.domain

import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import scala.beans.BeanProperty


@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class VersionedItem extends Serializable {

    @Id
    @GeneratedValue
    @BeanProperty
    var id: Long = _

    @Version
    @BeanProperty
    var version: Long = _

    var name: String = _

    var description: String = _

}
