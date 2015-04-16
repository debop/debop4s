package debop4s.mongo.springdata.order

import java.util

import debop4s.mongo.springdata.model.{Customer, Order}
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * OrderRepository
 * @author sunghyouk.bae@gmail.com 14. 10. 22.
 */
@Repository
trait OrderRepository extends MongoRepository[Order, String] {

  def findByCustomer(customer: Customer): util.List[Order]

  def findByLineItemsExists(exists: Boolean): util.List[Order]
}
