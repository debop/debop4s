package debop4s.data.tests.mapping.associations

import debop4s.core.ValueObject
import debop4s.core.utils.Hashs
import debop4s.data.model.HibernateEntity
import debop4s.data.tests.AbstractJpaTest
import debop4s.data.tests.mapping.associations.ProductStatus.ProductStatus
import java.security.Timestamp
import java.util.Date
import java.{ lang, util }
import javax.persistence._
import org.hibernate.annotations.{ Parent, LazyCollection, LazyCollectionOption }
import org.junit.Test
import scala.beans.BeanProperty


/**
 * OneToManySetTest
 * Created by debop on 2014. 3. 6.
 */
@org.springframework.transaction.annotation.Transactional
class OneToManySetTest extends AbstractJpaTest {

  @PersistenceContext val em: EntityManager = null

  @Test
  def bidding() {

    val item = new BiddingItem()
    val bid1 = new Bid(item, new java.math.BigDecimal(100.0))
    val bid2 = new Bid(item, new java.math.BigDecimal(200.0))

    em.persist(item)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[BiddingItem], item.id)
    assert(loaded != null)
    assert(loaded.bids.size == 2)

    val bid11 = loaded.bids.iterator().next()
    loaded.bids.remove(bid11)

    em.persist(loaded)
    em.flush()
    em.clear()

    val loaded2 = em.find(classOf[BiddingItem], item.id)
    assert(loaded2 != null)
    assert(loaded2.bids.size == 1)

    em.remove(loaded2)
    em.flush()

    assert(em.find(classOf[BiddingItem], item.id) == null)
  }

  @Test
  def productTest() {
    val item = new ProductItem()

    val image1 = new ProductImage()
    item.images.add(image1)
    image1.setItem(item)
    image1.name = "image1"


    val image2 = new ProductImage()
    item.images.add(image2)
    image2.item = item
    image2.name = "image2"

    item.status = ProductStatus.Active

    em.persist(item)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[ProductItem], item.id)
    assert(loaded != null)
    assert(loaded.images.size == 2)

    loaded.images.clear()
    em.persist(loaded)
    em.flush()
    em.clear()

    val loaded2 = em.find(classOf[ProductItem], item.id)
    assert(loaded2 != null)
    assert(loaded2.images.size == 0)

    em.remove(loaded2)
    em.flush()
    assert(em.find(classOf[ProductItem], item.id) == null)
  }
}

@Entity
@Access(AccessType.FIELD)
class Bid extends HibernateEntity[lang.Long] {

  def this(item: BiddingItem, amount: java.math.BigDecimal) {
    this()
    this.item = item
    this.item.bids.add(this)
    this.amount = amount
  }

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  @ManyToOne
  @JoinColumn(name = "itemId")
  var item: BiddingItem = _

  @Column(nullable = false)
  var amount: java.math.BigDecimal = _

  @Transient
  var timestamp: Timestamp = _

  override def hashCode(): Int = Hashs.compute(amount)
}

@Entity
@Access(AccessType.FIELD)
class BiddingItem extends HibernateEntity[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  var description: String = _

  @OneToMany(mappedBy = "item", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @LazyCollection(value = LazyCollectionOption.EXTRA)
  var bids: util.Set[Bid] = new util.HashSet[Bid]()

  override def hashCode(): Int = Hashs.compute(name)
}

@Embeddable
class ProductImage extends ValueObject {

  // 이 놈은 @BeanProperty가 없으면 예외가 발생합니다.
  @Parent
  @BeanProperty
  var item: ProductItem = _
  @BeanProperty
  var name: String = _
  @BeanProperty
  var filename: String = _
  @BeanProperty
  var sizeX: lang.Integer = 0
  @BeanProperty
  var sizeY: lang.Integer = 0

  override def hashCode(): Int = Hashs.compute(name, filename)
}

@Entity
@Access(AccessType.FIELD)
class ProductItem extends HibernateEntity[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _
  var description: String = _
  var initalPrice: java.math.BigDecimal = _
  var reservePrice: java.math.BigDecimal = _

  @Temporal(TemporalType.DATE)
  var startDate: Date = _

  @Temporal(TemporalType.DATE)
  var endDate: Date = _

  @Column(name = "status")
  var statusInt: lang.Integer = _

  def status: ProductStatus = ProductStatus(statusInt)

  def status_=(v: ProductStatus.Value) {
    statusInt = v.id
  }

  @CollectionTable(name = "ProductItemImage", joinColumns = Array(new JoinColumn(name = "ProductItemId")))
  @ElementCollection(targetClass = classOf[ProductImage])
  @org.hibernate.annotations.Cascade(Array(org.hibernate.annotations.CascadeType.ALL))
  var images: util.Set[ProductImage] = new util.HashSet[ProductImage]()

  def removeImage(image: ProductImage): Boolean = {
    images.remove(image)
  }

  override def hashCode(): Int = Hashs.compute(name)
}

object ProductStatus extends Enumeration {

  type ProductStatus = Value

  val Unknown = Value(0, "Unknown")
  val Active = Value(1, "Active")
  val Inactive = Value(2, "Inactive")
}