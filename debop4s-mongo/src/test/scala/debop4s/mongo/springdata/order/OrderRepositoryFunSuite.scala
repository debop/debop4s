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

  test("create order") {
    val debop = customerRepo.findByEmailAddress(new EmailAddress("debop@hconnect.co.kr"))
  }

}
