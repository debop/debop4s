package debop4s.data.orm.jpa.mysql.repository

import java.lang.{Long => JLong}
import javax.persistence.QueryHint

import debop4s.data.orm.jpa.mysql.model.MySqlOrder
import org.springframework.data.jpa.repository.{JpaRepository, Query, QueryHints}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * MySqlOrderRepository
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
@Repository
trait MySqlOrderRepository extends JpaRepository[MySqlOrder, Integer] {

  @Transactional(readOnly = true)
  @Query("select x from MySqlOrder x where x.id = :id")
  @QueryHints(value = Array(new QueryHint(name = "org.hibernate.readOnly", value = "true")), forCounting = false)
  def findById(@Param("id") id: Integer): MySqlOrder

}
