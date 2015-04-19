package debop4s.data.orm.jpa.mysql.repository

import javax.persistence.QueryHint

import debop4s.data.orm.jpa.ScalaJpaEntity2
import org.springframework.data.jpa.repository.{JpaRepository, Query, QueryHints}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
trait ScalaJpaEntity2Repository extends JpaRepository[ScalaJpaEntity2, java.lang.Long] {

  @Transactional(readOnly = true)
  @Query("select x from ScalaJpaEntity2 x where x.id = :id")
  @QueryHints(value = Array(new QueryHint(name = "org.hibernate.readOnly", value = "true")), forCounting = false)
  def findById(@Param("id") id: java.lang.Long): ScalaJpaEntity2

}
