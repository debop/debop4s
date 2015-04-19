package debop4s.data.orm.mapping.associations

import java.util.Date
import java.{lang, util}
import javax.persistence._

import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.HibernateEntityBase
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.{annotations => hba}
import org.junit.Test


/**
 * OneToManyListJUnitSuite
 * Created by debop on 2014. 3. 3.
 */
@org.springframework.transaction.annotation.Transactional
class OneToManyListJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  @org.springframework.transaction.annotation.Transactional
  def simpleOneToMany() {
    val order = new OneToManyOrder()
    order.no = "123456"

    val item1 = new OneToManyOrderItem()
    item1.name = "Item1"
    item1.order = order
    order.items.add(item1)

    val item2 = new OneToManyOrderItem()
    item2.name = "Item2"
    item2.order = order
    order.items.add(item2)

    em.persist(order)
    em.flush()
    em.clear()

    val order2 = em.find(classOf[OneToManyOrder], order.id)
    assert(order2 != null)
    assert(order2.items.size == 2)

    val i1 = order2.items.iterator().next()
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

@javax.persistence.Entity
@Access(AccessType.FIELD)
class OneToManyUser extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var city: String = _

  @OneToMany(cascade = Array(javax.persistence.CascadeType.ALL))
  @JoinTable(name = "OneToMany_User_Address")
  @MapKeyColumn(name = "nick")
  @hba.Fetch(hba.FetchMode.SUBSELECT)
  val addresses: util.Map[String, OneToManyAddress] = new util.HashMap[String, OneToManyAddress]

  @ElementCollection
  @JoinTable(name = "OneToMany_Nicks", joinColumns = Array(new JoinColumn(name = "userId")))
  @hba.Cascade(Array(hba.CascadeType.ALL))
  val nicknames: util.Set[String] = new util.HashSet[String]

  override def hashCode(): Int = Hashs.compute(city)
}

@Entity
@org.hibernate.annotations.Cache(region = "association", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
@Access(AccessType.FIELD)
class OneToManyAddress extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var city: String = _

  override def hashCode(): Int =
    super.hashCode()
}

@Entity
@org.hibernate.annotations.Cache(region = "association", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
@Access(AccessType.FIELD)
class OneToManyChild extends HibernateEntityBase[lang.Long] {

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
@org.hibernate.annotations.Cache(region = "association", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
@Access(AccessType.FIELD)
class OneToManyFather extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  @OneToMany(cascade = Array(javax.persistence.CascadeType.ALL), fetch = FetchType.EAGER)
  @JoinTable(name = "Father_Child")
  @OrderColumn(name = "birthday")
  @hba.LazyCollection(hba.LazyCollectionOption.EXTRA)
  val orderedChildren: util.List[OneToManyChild] = new util.ArrayList[OneToManyChild]()

  override def hashCode(): Int =
    Hashs.compute(name)
}

@Entity
// @org.hibernate.annotations.Cache(region = "association", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
@Access(AccessType.FIELD)
class OneToManyOrder extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var no: String = _

  @OneToMany(mappedBy = "order", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @hba.LazyCollection(hba.LazyCollectionOption.EXTRA)
  val items: util.List[OneToManyOrderItem] = new util.ArrayList[OneToManyOrderItem]

  @inline
  override def hashCode(): Int = Hashs.compute(no)

  @inline
  override protected def buildStringHelper =
    super.buildStringHelper
    .add("no", no)
}

@Entity
// @org.hibernate.annotations.Cache(region = "association", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
@Access(AccessType.FIELD)
class OneToManyOrderItem extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  @Column(name = "orderItemId")
  var id: lang.Long = _

  def getId = id

  @ManyToOne
  @JoinColumn(name = "orderId")
  var order: OneToManyOrder = _

  var name: String = _

  @inline
  override def hashCode(): Int = Hashs.compute(name)
}

