package org.hibernate.cache.rediscala.tests.jpa.repository

import javax.persistence.QueryHint
import org.hibernate.cache.rediscala.tests.domain.Item
import org.springframework.data.jpa.repository._

/**
 * ItemRepository 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
trait ItemRepository extends JpaRepository[Item, java.lang.Long] {

    @QueryHints(value = Array(new QueryHint(name = "org.hibernate.cacheable", value = "true")))
    def findByName(name: String): java.util.List[Item]
}
