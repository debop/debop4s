package com.github.debop4s.data.tests.mapping.associations

import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.model.HibernateEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.lang
import java.util
import java.util.Date
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence._
import org.hibernate.annotations._
import org.junit.Test
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._


/**
 * OneToManyListTest
 * Created by debop on 2014. 3. 3.
 */
@org.springframework.transaction.annotation.Transactional
class OneToManyListTest extends AbstractJpaTest {

    private val log = LoggerFactory.getLogger(getClass)

    @PersistenceContext val em: EntityManager = null

    @Test
    def simpleOneToMany() {
        val order = new OneToManyOrder

        val item1 = new OneToManyOrderItem
        item1.name = "Item1"
        item1.order = order
        order.items.add(item1)

        val item2 = new OneToManyOrderItem
        item2.name = "Item2"
        item2.order = order
        order.items.add(item2)

        em.persist(order)
        em.flush()
        em.clear()

        val order2 = em.find(classOf[OneToManyOrder], order.id)
        assert(order2 != null)
        assert(order2.items.size == 2)

        val i1 = order2.items.head
        order2.items.remove(i1)
        em.persist(order2)
        em.flush()
        em.clear()

        val order3 = em.find(classOf[OneToManyOrder], order.id)
        assert(order3 != null)
        assert(order3.items.size == 1)

        em.remove(order3)
        em.flush()

        assert(em.find(classOf[OneToManyOrder], order.id) == null)

    }

}

@Entity
class OneToManyUser extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var city: String = _

    @OneToMany(cascade = Array(javax.persistence.CascadeType.ALL))
    @JoinTable(name = "OneToMany_User_Address")
    @MapKeyColumn(name = "nick")
    @Fetch(FetchMode.SUBSELECT)
    val addresses: util.Map[String, OneToManyAddress] = new util.HashMap[String, OneToManyAddress]

    @ElementCollection
    @JoinTable(name = "OneToMany_Nicks", joinColumns = Array(new JoinColumn(name = "userId")))
    @Cascade(Array(org.hibernate.annotations.CascadeType.ALL))
    val nicknames: util.Set[String] = new util.HashSet[String]

    override def hashCode(): Int =
        Hashs.compute(city)
}

@Entity
class OneToManyAddress extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var city: String = _

    override def hashCode(): Int =
        super.hashCode()
}

@Entity
class OneToManyChild extends HibernateEntity[lang.Long] {

    def this(name: String) {
        this()
        this.name = name
    }

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var name: String = _

    @Temporal(TemporalType.DATE)
    var birthday: Date = _
}

@Entity
class OneToManyFather extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var name: String = _

    @OneToMany(cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER)
    @JoinTable(name = "Father_Child")
    @OrderColumn(name = "birthday")
    @LazyCollection(LazyCollectionOption.EXTRA)
    val orderedChildren: util.List[OneToManyChild] = new util.ArrayList[OneToManyChild]()

    override def hashCode(): Int =
        Hashs.compute(name)
}

@Entity
class OneToManyOrder extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var no: String = _

    @OneToMany(mappedBy = "order", cascade = Array(CascadeType.ALL), orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.EXTRA)
    val items: util.List[OneToManyOrderItem] = new util.ArrayList[OneToManyOrderItem]
}

@Entity
class OneToManyOrderItem extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    @ManyToOne
    var order: OneToManyOrder = _

    var name: String = _

    override def hashCode(): Int =
        Hashs.compute(name)
}

