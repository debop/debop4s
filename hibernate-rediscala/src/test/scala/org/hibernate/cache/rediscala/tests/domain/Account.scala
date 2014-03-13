package org.hibernate.cache.rediscala.tests.domain

import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import scala.beans.BeanProperty

/**
 * org.hibernate.cache.rediscala.tests.domain.Account
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 3:59
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Access(AccessType.FIELD)
@SerialVersionUID(6662300674854084326L)
class Account extends Serializable {

    @Id
    @GeneratedValue
    var id: Long = _

    @ManyToOne
    @JoinColumn(name = "personId")
    @BeanProperty
    var person: Person = _
}
