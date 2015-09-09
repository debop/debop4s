package debop4s.mongo.springdata.order

import debop4s.mongo.springdata.ApplicationConfigurationFunSuite
import debop4s.mongo.springdata.model.EmailAddress

/**
 * OrderRepositoryFunSuite
 * @author sunghyouk.bae@gmail.com 14. 10. 22.
 */
class OrderRepositoryFunSuite extends ApplicationConfigurationFunSuite {

  before {
    setup()
  }

  test("find orders by customer") {
    val customer = customerRepo.findByEmailAddress(new EmailAddress("sunghyouk.bae@gmail.com"))
    customer should not be null

    val orders = orderRepo.findByCustomer(customer)
    orders.size() should be > 0
  }

}
