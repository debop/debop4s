package com.github.debop4s.data.tests.mapping.parallel

import com.github.debop4s.core.utils.{ToStringHelper, Hashs}
import com.github.debop4s.data.jpa.utils.{JpaParCallable, JpaParRunnable, JpaParallels}
import com.github.debop4s.data.model.HibernateEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.{util, lang}
import javax.persistence._
import org.hibernate.Hibernate
import org.hibernate.{annotations => hba}
import org.junit.{Before, Test}
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import scala.collection.mutable.ArrayBuffer
import org.hibernate.annotations.LazyCollectionOption

/**
 * 테스트 시 RDMBS의 Connection 수를 많이 늘리던가 entityCount 수를 줄여야 합니다.
 * Created by debop on 2014. 3. 10.
 */
@Transactional
class JpaParallelsTest extends AbstractJpaTest {

    private lazy val log = LoggerFactory.getLogger(getClass)

    @PersistenceContext val em: EntityManager = null

    val entityCount = 100

    @Before
    def before() {
        // 캐시는 삭제되지 않는다!
        em.createQuery("delete from ParallelOrderItem").executeUpdate()
        em.createQuery("delete from ParallelOrder").executeUpdate()
        em.flush()
        em.clear()
    }

    @Test
    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //    @Rollback(false)
    def parallelSaveAndRead() {

        val orderIds = ArrayBuffer[Long]()

        JpaParallels.run(emf, (0 until entityCount).toIterable) { (em, x) =>
            val order = createOrder(x)
            em.persist(order)
            orderIds += order.id
        }

        Thread.sleep(10)

        var orders =
            JpaParallels.call(emf, orderIds) { (em, x) =>
                val order = em.find(classOf[ParallelOrder], x)
                Hibernate.initialize(order.items)
                em.detach(order)
                order
            }

        assert(orders.forall(x => x != null))

        // 여기서부터는 hibernate-redis second 캐시에서도 잘 되는지 알아보기 위함입니다.
        orders =
            JpaParallels.call(emf, orderIds) { (em, x) =>
                val order = em.find(classOf[ParallelOrder], x)
                Hibernate.initialize(order.items)
                em.detach(order)
                order
            }

        assert(orders.forall(x => x != null))

        orders =
            JpaParallels.call(emf, orderIds) { (em, x) =>
                val order = em.find(classOf[ParallelOrder], x)
                Hibernate.initialize(order.items)
                em.detach(order)
                order
            }

        assert(orders.forall(x => x != null))
    }

    @Test
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Rollback(false)
    def parallelSaveAndReadForJava() {

        val orderIds = ArrayBuffer[Long]()

        JpaParallels.runAction(emf, (0 to entityCount).toIterable,
            new JpaParRunnable[Int] {
                override def run(em: EntityManager, x: Int): Unit = {
                    val order = createOrder(x)
                    em.persist(order)
                    orderIds += order.id
                }
            })

        Thread.sleep(10)

        var orders =
            JpaParallels.callFunc(emf, orderIds,
                new JpaParCallable[Long, ParallelOrder] {
                    override def call(em: EntityManager, id: Long): ParallelOrder = {
                        val order = em.find(classOf[ParallelOrder], id)
                        Hibernate.initialize(order.items)
                        em.detach(order)
                        order
                    }
                })

        assert(orders.forall(x => x != null))

        // 여기서부터는 hibernate-redis second 캐시에서도 잘 되는지 알아보기 위함입니다.
        orders =
            JpaParallels.callFunc(emf, orderIds,
                new JpaParCallable[Long, ParallelOrder] {
                    override def call(em: EntityManager, id: Long): ParallelOrder = {
                        val order = em.find(classOf[ParallelOrder], id)
                        Hibernate.initialize(order.items)
                        em.detach(order)
                        order
                    }
                })

        assert(orders.forall(x => x != null))

        orders =
            JpaParallels.callFunc(emf, orderIds,
                new JpaParCallable[Long, ParallelOrder] {
                    override def call(em: EntityManager, id: Long): ParallelOrder = {
                        val order = em.find(classOf[ParallelOrder], id)
                        Hibernate.initialize(order.items)
                        em.detach(order)
                        order
                    }
                })

        assert(orders.forall(x => x != null))

    }

    private def createOrder(x: Long) = {
        val order = new ParallelOrder()
        order.no = "order no. " + x

        val item1 = new ParallelOrderItem()
        item1.name = "Item1-" + x
        item1.order = order
        order.items.add(item1)

        val item2 = new ParallelOrderItem()
        item2.name = "Item2-" + x
        item2.order = order
        order.items.add(item2)

        order
    }
}

@Entity
@hba.Cache(region = "parallel", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class ParallelOrder extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    @Column(name = "orderId")
    var id: lang.Long = _

    def getId = id

    var no: String = _

    @OneToMany(mappedBy = "order", cascade = Array(CascadeType.ALL), orphanRemoval = true)
    val items: util.List[ParallelOrderItem] = new util.ArrayList[ParallelOrderItem]

    @inline
    override def hashCode(): Int = Hashs.compute(no)

    @inline
    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
        .add("no", no)
}

@Entity
@hba.Cache(region = "parallel", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class ParallelOrderItem extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    @Column(name = "orderItemId")
    var id: lang.Long = _

    def getId = id

    @ManyToOne
    @JoinColumn(name = "orderId")
    var order: ParallelOrder = _

    var name: String = _

    @inline
    override def hashCode(): Int = Hashs.compute(name)
}
