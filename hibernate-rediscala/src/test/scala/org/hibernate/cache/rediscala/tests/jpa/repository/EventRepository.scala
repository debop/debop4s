package org.hibernate.cache.rediscala.tests.jpa.repository

import java.util.Date
import javax.persistence.QueryHint
import org.hibernate.cache.rediscala.tests.domain.Event
import org.springframework.data.jpa.repository._
import org.springframework.data.repository.query.Param


/**
 * EventRepository 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
trait EventRepository extends JpaRepository[Event, java.lang.Long] {

  @Query("select evt from Event evt where evt.title = :title")
  @QueryHints(value = Array(new QueryHint(name = "org.hibernate.cacheable", value = "true")))
  def findByTitle(@Param("title") title: String): java.util.List[Event]

  @QueryHints(value = Array(new QueryHint(name = "org.hibernate.cacheable", value = "true")))
  def findByDate(date: Date): java.util.List[Event]

}
