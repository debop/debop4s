package debop4s.data.orm.jpa.mysql

import javax.persistence.{EntityManager, EntityManagerFactory, PersistenceContext}

import debop4s.data.orm.jpa.mysql.model.{MySqlOrder, MySqlOrderItem}
import debop4s.data.orm.jpa.mysql.repository.{MySqlOrderItemRepository, MySqlOrderRepository}
import debop4s.data.orm.jpa.mysql.service.MySqlOrderService
import org.hibernate.LazyInitializationException
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.transaction.annotation.{Propagation, Transactional}

import scala.collection.JavaConverters._

/**
 * MySQL Master-Slaves Replication 환경에서의 JPA 테스트
 *
 * NOTE: ReadOnly 테스트를 정확하게 하기 위해 2nd Cache를 사용하지 말아야 합니다.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[ScalaJpaMySqlReplicationConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class MySqlReplicationJUnitSuite extends JUnitSuite with Matchers with BeforeAndAfterAll {

  private val log = LoggerFactory.getLogger(getClass)

  @Autowired val orderService: MySqlOrderService = null
  @Autowired val orderRepo: MySqlOrderRepository = null
  @Autowired val orderItemRepo: MySqlOrderItemRepository = null

  @Autowired val emf: EntityManagerFactory = null
  @PersistenceContext val em: EntityManager = null

  override def beforeAll(): Unit = {
    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  @Test
  def testSaveAndLoadOrder(): Unit = {

    // 1. 샘플 데이터 생성 및 Write Order and OrderItem
    var order = createSampleOrder()
    order = orderService.saveOrder(order)
    log.debug(s"saved order=$order")

    // 2. Read Order (not OrderItem - lazyness)
    val loaded = orderService.getOrder(order.id)
    log.debug(s"loaded=$loaded")
    loaded should not be null
    loaded shouldEqual order

    // 3 lazy initialization 시도
    // 테스트 메소드가 @Transactional(readOnly=false) 가 지정되지 않았으므로 예외 발생
    intercept[LazyInitializationException] {
      loaded.items should not be null
      loaded.items.asScala.foreach { item =>
        item should not be null
        item.order shouldEqual loaded
      }
    }

    // 4. Read OrderItems
    // OrderItem.Order 도 @ManyToOne(fetch=Fetch.LAZY) 이므로 lazyness 로 미리 읽어오지 않습니다
    val items = orderService.getOrderItems(order.id)
    items should not be null
    items.asScala.foreach { item =>
      item should not be null
      log.trace(s"order item=$item")
      // NOTE: item.order 는 lazyness 이므로 여기서 예외가 발생한다.
      intercept[LazyInitializationException] {
        item.order shouldEqual loaded
      }
    }

    // 5. JPQL로 OrderItems 와 Order 를 FETCH.EAGER로 강제로 로드한다.
    val joinItems = orderService.getOrderItemsJpqlWithOrder(order.id)
    joinItems should not be null
    joinItems.asScala.foreach { item =>
      item should not be null
      log.trace(s"order item=$item")
      item.order shouldEqual loaded
    }

    // 6. Item 변경 후 저장 (실제 Detached 된 엔티티인데, JpaRepository가 내부에서 merge 한 후 저장합니다.)
    joinItems.asScala.foreach { item =>
      item.name = "오더아이템 " + item.name
    }
    orderService.saveOrderItems(joinItems)

    // 7. OrderItem 들이 갱신되었는지 확인한다.
    val updatedItems = orderService.getOrderItems(order.id)
    updatedItems should not be null
    updatedItems.asScala.foreach { item => log.trace(s"updated item=$item") }
  }

  @Test
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  def testSaveAndLoadOrderInTransactional(): Unit = {

    // 1. 샘플 데이터 생성 및 Write Order and OrderItem
    var order = createSampleOrder()
    order = orderService.saveOrder(order)
    log.debug(s"saved order=$order")

    // 2. Read Order (not OrderItem - lazyness)
    val loaded = orderService.getOrder(order.id)
    log.debug(s"loaded=$loaded")
    loaded should not be null
    loaded shouldEqual order

    // 2.2 JPQL 로 로드하는 경우 Tx 가 완료가 안되었는데, DB에서 직접 얻으므로 null 을 반환한다.
    //    val loadByJpql = orderService.getOrderByJPQL(order.id)
    //    loadByJpql shouldEqual null

    // 3. lazy initialization 시도
    loaded.items should not be null
    loaded.items.asScala.foreach { item =>
      item should not be null
      item.order shouldEqual loaded
    }

    // 4. Read OrderItems
    val items = orderService.getOrderItems(order.id)
    items should not be null
    items.asScala.foreach { item =>
      item should not be null
      log.trace(s"order item=$item")
      item.order shouldEqual loaded
    }

    // 5. JPQL로 OrderItems 와 Order 를 FETCH.EAGER로 강제로 로드한다.
    val joinItems = orderService.getOrderItemsJpqlWithOrder(order.id)
    joinItems should not be null
    joinItems.asScala.foreach { item =>
      item should not be null
      log.trace(s"order item=$item")
      item.order shouldEqual loaded
    }

    // 6. Item 변경 후 저장 (실제 Detached 된 엔티티인데, JpaRepository가 내부에서 merge 한 후 저장합니다.)
    joinItems.asScala.foreach { item =>
      item.name = "오더아이템 " + item.name
    }
    orderService.saveOrderItems(joinItems)

    // 7. OrderItem 들이 갱신되었는지 확인한다.
    val updatedItems = orderService.getOrderItems(order.id)
    updatedItems should not be null
    updatedItems.asScala.foreach { item => log.trace(s"updated item=$item") }
  }

  private def createSampleOrder(): MySqlOrder = {
    val order = new MySqlOrder()
    order.no = "order-1"
    (0 until 2).foreach { i =>
      val item = new MySqlOrderItem()
      item.name = s"orderItem-$i"
      item.order = order
      order.items.add(item)
    }
    order
  }

}
