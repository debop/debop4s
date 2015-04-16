package debop4s.mongo.springdata.core

import debop4s.mongo.springdata.ApplicationConfigurationFunSuite
import debop4s.mongo.springdata.model.{Address, Customer, EmailAddress}
import org.springframework.dao.DuplicateKeyException


class CustomerRepositoryFunSuite extends ApplicationConfigurationFunSuite {

  before {
    setup()
  }

  test("configuration test") {
    customerRepo should not be null
  }

  test("save customer") {
    val email = new EmailAddress("debop@hconnect.co.kr")
    val debop = new Customer("Sunghyouk", "Bae")
    debop.emailAddress = email
    debop.add(new Address("성북구 정릉", "서울", "대한민국"))

    val result = customerRepo.save(debop)
    log.debug(s"saved customer=$result")
    result.getId should not be null
  }

  test("load customer by email") {
    val debop = new Customer("Sunghyouk", "Bae")
    val email = new EmailAddress("debop@hconnect.co.kr")
    debop.emailAddress = email
    customerRepo.save(debop)

    val result = customerRepo.findByEmailAddress(email)
    result should not be null
    result shouldEqual debop
  }

  test("prevent Duplicate Email") {
    val debop = new Customer("Sunghyouk", "Bae")
    val email = new EmailAddress("debop@hconnect.co.kr")
    debop.emailAddress = email
    customerRepo.save(debop)

    val dup = new Customer("Dup", "Email")
    dup.emailAddress = debop.emailAddress

    intercept[DuplicateKeyException] {
      customerRepo.save(dup)
    }
  }

}
