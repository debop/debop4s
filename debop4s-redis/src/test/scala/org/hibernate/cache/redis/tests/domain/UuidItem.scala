package org.hibernate.cache.redis.tests.domain

import javax.persistence._

/**
 * org.hibernate.cache.redis.tests.domain.UuidItem 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 4:12
 */
@Entity
@Access(AccessType.FIELD)
class UuidItem extends Serializable {

    @Id
    @GeneratedValue
    var id: String = _

    var name: String = _

    var description: String = _

}
