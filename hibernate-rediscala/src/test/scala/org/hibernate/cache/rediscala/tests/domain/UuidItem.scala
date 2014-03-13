package org.hibernate.cache.rediscala.tests.domain

import javax.persistence._
import scala.beans.BeanProperty

/**
 * org.hibernate.cache.rediscala.tests.domain.UuidItem
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 4:12
 */
@Entity
@Access(AccessType.FIELD)
class UuidItem extends Serializable {

    @Id
    @GeneratedValue
    @BeanProperty
    var id: String = _

    @BeanProperty
    var name: String = _

    @BeanProperty
    var description: String = _

}
