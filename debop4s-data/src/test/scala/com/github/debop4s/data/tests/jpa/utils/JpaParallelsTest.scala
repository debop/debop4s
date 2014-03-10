package com.github.debop4s.data.tests.jpa.utils

import com.github.debop4s.data.jpa.utils.JpaParallels
import com.github.debop4s.data.tests.AbstractJpaTest
import com.github.debop4s.data.tests.mapping.associations.{OneToManyOrderItem, OneToManyOrder}
import javax.persistence.{EntityManager, PersistenceContext}
import org.hibernate.Hibernate
import org.junit.Test
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

/**
 * 테스트 시 RDMBS의 Connection 수를 많이 늘리던가 entityCount 수를 줄여야 합니다.
 * Created by debop on 2014. 3. 10.
 */
@Transactional
class JpaParallelsTest extends AbstractJpaTest {

  @PersistenceContext val em: EntityManager = null

  val entityCount = 500

  @Test
  @Transactional
  @Rollback(false)
  def parallelSaveAndRead() {

    JpaParallels.run(emf, (0 until entityCount).toIterable) { (em, x) =>
      val order = createOrder(x)
      em.persist(order)
    }

    var orders =
      JpaParallels.call(emf, (0 until entityCount).toIterable) { (em, x) =>
        val order = em.find(classOf[OneToManyOrder], (x + 1).toLong)
        Hibernate.initialize(order.items)
        em.detach(order)
        order
      }.sortBy(_.id)

    assert(orders.forall(x => x != null))

    // 여기서부터는 hibernate-redis second 캐시에서도 잘 되는지 알아보기 위함입니다.
    orders =
      JpaParallels.call(emf, (0 until entityCount).toIterable) { (em, x) =>
        val order = em.find(classOf[OneToManyOrder], (x + 1).toLong)
        Hibernate.initialize(order.items)
        em.detach(order)
        order
      }.sortBy(_.id)

    assert(orders.forall(x => x != null))

    orders =
      JpaParallels.call(emf, (0 until entityCount).toIterable) { (em, x) =>
        val order = em.find(classOf[OneToManyOrder], (x + 1).toLong)
        Hibernate.initialize(order.items)
        em.detach(order)
        order
      }.sortBy(_.id)

    assert(orders.forall(x => x != null))
  }

  private def createOrder(x: Long) = {
    val order = new OneToManyOrder()

    val item1 = new OneToManyOrderItem()
    item1.name = "Item1-" + x
    item1.order = order
    order.items.add(item1)

    val item2 = new OneToManyOrderItem()
    item2.name = "Item2-" + x
    item2.order = order
    order.items.add(item2)

    order
  }
}
