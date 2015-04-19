package debop4s.data.orm.jpa.mysql.repository

import java.lang.{Long => JLong}
import java.util.{List => JList}
import javax.persistence.QueryHint

import debop4s.data.orm.jpa.mysql.model.{MySqlOrder, MySqlOrderItem}
import org.springframework.data.jpa.repository.{JpaRepository, Query, QueryHints}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * MySqlOrderItemRepository
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
@Repository
trait MySqlOrderItemRepository extends JpaRepository[MySqlOrderItem, Integer] {

  @Transactional(readOnly = true)
  @Query("select x from MySqlOrderItem x where x.id = :id")
  @QueryHints(value = Array(new QueryHint(name = "org.hibernate.readOnly", value = "true")), forCounting = false)
  def findById(@Param("id") id: Integer): MySqlOrderItem

  @Transactional(readOnly = true)
  def findByOrder(order: MySqlOrder): JList[MySqlOrderItem]

  // Order 는 lazyness 로 가져온다.
  @Transactional(readOnly = true)
  @Query("select item from MySqlOrderItem item where item.order.id = :orderId")
  def findByOrderJpql(@Param("orderId") orderId: Integer): JList[MySqlOrderItem]

  // Order 를 fetch 로 가져온다.
  @Transactional(readOnly = true)
  @Query("select item from MySqlOrderItem item LEFT JOIN FETCH item.order where item.order.id = :orderId")
  def findByOrderJpqlWithOrder(@Param("orderId") orderId: Integer): JList[MySqlOrderItem]

}
