package org.hibernate.cache.rediscala.tests.jpa

import org.springframework.data.jpa.repository.{Query, JpaRepository}
import org.springframework.data.repository.query.Param

/**
 * JpaAccountRepository 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
trait JpaAccountRepository extends JpaRepository[JpaAccount, java.lang.Long] {

    @Query(value = "select a from JpaAccount a where a.name = :name")
    def findByName(@Param("name") name: String): JpaAccount

}
