package com.github.debop4s.data.tests.jpa.parallels

import com.github.debop4s.data.tests.AbstractJpaTest
import com.github.debop4s.data.tests.mapping.associations.{OneToManyOrder, OneToManyOrderItem}
import javax.persistence.{EntityManager, PersistenceContext}
import org.hibernate.Hibernate
import org.junit.Test
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional


/**
 * CRUD를 병렬로 작업하기 위해서는 각 단위 작업으로 쪼개고, 각각의 작업에 새로운 Transaction을 주어야 한다.
 * Created by debop on 2014. 3. 10.
 */
@Transactional
class ParallelTest extends AbstractJpaTest {

  @PersistenceContext val em: EntityManager = null

  val entityCount = 1000: Int

  @Test
  @Transactional
  @Rollback(false)
  def parallelInsertRead1() {
    (0 until entityCount).grouped(8).foreach { group =>
      group.par.foreach { x =>
        val order = createOrder(x)
        insertAction(order)
      }
    }
  }

  @Test
  @Transactional
  @Rollback(false)
  def parallelInsertRead2() {

    val orders =
      (0 until entityCount).grouped(8).map { collection =>
        collection.map(x => loadAction(x + 1))
      }.flatten.toIndexedSeq.sortBy(_.id)

    assert(orders.forall(x => x != null))
  }

  @Test
  @Transactional
  @Rollback(false)
  def parallelInsertRead3() {
    (0 until entityCount).par.foreach { x =>
      val order = createOrder(x)
      insertAction(order)
    }
  }

  @Test
  @Transactional
  @Rollback(false)
  def parallelInsertRead4() {

    val orders =
      (0 until entityCount).par.map { x =>
        loadAction(x + 1)
      }.toIndexedSeq.sortBy(_.id)

    assert(orders.forall(x => x != null))
  }

  def insertAction(order: OneToManyOrder) {
    val em = emf.createEntityManager()
    val tx = em.getTransaction
    tx.begin()

    em.persist(order)

    tx.commit()
    em.close()
  }

  def loadAction(id: Long): OneToManyOrder = {
    val em = emf.createEntityManager()
    val tx = em.getTransaction
    tx.begin()

    val order = em.find(classOf[OneToManyOrder], id)
    Hibernate.initialize(order.items)
    em.detach(order)

    tx.commit()
    em.close()

    order
  }

  def createOrder(x: Int) = {
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
