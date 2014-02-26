package org.hibernate.cache.redis.tests.domain

import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import scala.beans.BeanProperty

/**
 * org.hibernate.cache.redis.tests.domain.Account 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 3:59
 */
@Entity
@org.hibernate.annotations.Cache(region = "account", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Access(AccessType.FIELD)
class Account extends Serializable {

    @Id
    @GeneratedValue
    var id: java.lang.Long = _

    @ManyToOne(targetEntity = classOf[Person])
    @JoinColumn(name = "personId")
    @BeanProperty
    var person: Person = null
}
