package org.hibernate.cache.redis.tests.domain

import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy


@Entity
@org.hibernate.annotations.Cache(region = "account", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Access(AccessType.FIELD)
class VersionedItem extends Serializable {

    @Id
    @GeneratedValue
    var id: Long = _

    @Version
    var version: Long = _

    var name: String = _

    var description: String = _

}
