package debop4s.mongo.springdata.core

import debop4s.mongo.springdata.model.{Customer, EmailAddress}
import org.springframework.data.mongodb.repository.MongoRepository


/**
 * CustomerRepository
 * @author sunghyouk.bae@gmail.com 14. 10. 19.
 */
@org.springframework.stereotype.Repository
trait CustomerRepository extends MongoRepository[Customer, String] {

  def save(customer: Customer): Customer

  def findByEmailAddress(emailAddress: EmailAddress): Customer

}
