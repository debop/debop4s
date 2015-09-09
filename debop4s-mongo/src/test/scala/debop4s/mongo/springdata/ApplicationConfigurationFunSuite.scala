package debop4s.mongo.springdata

import java.math.BigDecimal

import debop4s.mongo.AbstractMongoFunSuite
import debop4s.mongo.springdata.core.{CustomerRepository, ProductRepository}
import debop4s.mongo.springdata.model._
import debop4s.mongo.springdata.order.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

import scala.collection.JavaConverters._

@ContextConfiguration(classes = Array(classOf[ApplicationConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class ApplicationConfigurationFunSuite extends AbstractMongoFunSuite {

  @Autowired val context: ApplicationContext = null

  @Autowired val orderRepo: OrderRepository = null
  @Autowired val customerRepo: CustomerRepository = null
  @Autowired val productRepo: ProductRepository = null

  override def beforeAll(): Unit = {
    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  test("bootstrap application from java config") {
    context should not be null
  }

  protected def setup(): Unit = {

    // Customers
    customerRepo.deleteAll()

    val address = new Address("성북구 정릉1동", "서울", "대한민국")
    val debop = new Customer("성혁", "배")
    debop.emailAddress = new EmailAddress("sunghyouk.bae@gmail.com")
    debop.add(address)
    customerRepo.save(debop)

    // Products
    productRepo.deleteAll()

    val iPad = new Product("iPad", new BigDecimal(499.0))
    iPad.description = "Apple tablet device"
    iPad.setAttribute("connector", "plug")

    val macBook = new Product("MacBook Pro", new BigDecimal(1299.0))
    macBook.description = "Apple notebook"

    val dock = new Product("Dock", new BigDecimal(49.0))
    dock.description = "Dock for iPhone/iPad"
    dock.setAttribute("connector", "plug")

    productRepo.save(Seq(iPad, macBook, dock).asJava)

    // Orders / LineItems
    orderRepo.deleteAll()

    val iPadLineItem = new LineItem(iPad, 2)
    iPadLineItem.price = BigDecimal.valueOf(499.0)

    val macBookLineItem = new LineItem(macBook, 1)

    val order = new Order(debop, address)
    order.addItem(iPadLineItem)
    order.addItem(macBookLineItem)

    orderRepo.save(order)
  }

}
