package debop4s.mongo.springdata.core

import java.math.BigDecimal

import debop4s.mongo.springdata.ApplicationConfigurationFunSuite
import debop4s.mongo.springdata.model.Product
import org.springframework.data.domain.{PageRequest, Sort}

import scala.collection.JavaConverters._

/**
 * ProductRepositoryFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ProductRepositoryFunSuite extends ApplicationConfigurationFunSuite {

  before {
    setup()
  }

  test("create product") {
    val product = new Product("Camera bag", new BigDecimal(49.99))
    productRepo.save(product)

    product.getId should not be null
  }

  test("lookup products by description") {
    val pageable = new PageRequest(0, 1, Sort.Direction.DESC, "name")
    val page = productRepo.findByDescriptionContaining("Apple", pageable)

    page.getContent.size shouldEqual 1
    page.isFirst shouldEqual true
    page.isLast shouldEqual false
    page.hasNext shouldEqual true
  }

  // NOTE: spring-data-mongodb 1.6.0.RELEASE 부터 제공되지 않습니다.
  ignore("finds products by attributes") {

    val products = productRepo.findByAttributes("attributes.connector", "plug")
    products.size() shouldEqual 2

    val containsDock = products.asScala.exists(p => p.name.contains("Dock"))
    containsDock shouldEqual true
  }

}
