package debop4s.data.orm.mapping.compositeId

import java.util
import java.util.Date
import javax.persistence._

import debop4s.core.ValueObject
import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.{HibernateEntityBase, LongEntity, PersistentObjectBase}
import org.hibernate.annotations.{LazyCollection, LazyCollectionOption, LazyToOne, LazyToOneOption}
import org.junit.Test

@org.springframework.transaction.annotation.Transactional
class CompositeIdJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  def embeddedId() {
    val car = new EmbeddableIdCar(new EmbeddableCarIdentifier("Kia", 2012))
    car.serialNo = "6675"
    em.persist(car)
    em.flush()
    em.clear()

    var loaded = em.find(classOf[EmbeddableIdCar], car.id)
    assert(loaded != null)
    assert(loaded.id == car.id)
    assert(loaded.id.brand == "Kia")

    loaded.serialNo = "5081"
    em.persist(loaded)
    em.flush()
    em.clear()

    loaded = em.find(classOf[EmbeddableIdCar], new EmbeddableCarIdentifier("Kia", 2012))
    assert(loaded != null)

    em.remove(loaded)
    em.flush()
    em.clear()

    assert(em.find(classOf[EmbeddableIdCar], new EmbeddableCarIdentifier("Kia", 2012)) == null)
  }

}


@Embeddable
@Access(AccessType.FIELD)
class EmbeddableCarIdentifier(private[this] var _brand: String,
                              private[this] var _year: Int) extends ValueObject {

  def this() {
    this("", 0)
  }

  @Column(nullable = false, length = 32)
  var brand: String = _brand

  @Column(nullable = false)
  var year: Int = _year

  override def hashCode(): Int = Hashs.compute(brand, year)
}

@Entity
@Access(AccessType.FIELD)
class EmbeddableIdCar extends HibernateEntityBase[EmbeddableCarIdentifier] {

  def this(id: EmbeddableCarIdentifier) {
    this()
    this.id = id
  }

  @EmbeddedId
  @Column(name = "carId")
  var id: EmbeddableCarIdentifier = _

  def getId = id

  var serialNo: String = _

  override def hashCode(): Int = Hashs.compute(serialNo)
}

@SerialVersionUID(100L)
@Access(AccessType.FIELD)
class CarIdentifier(private[this] var _brand: String,
                    private[this] var _year: Int) extends ValueObject {

  def this() = this("", 0)

  @Column(nullable = false, length = 32)
  var brand: String = _brand

  @Column(nullable = false)
  var year: Int = _year

  override def hashCode(): Int = Hashs.compute(brand, year)
}

@Entity
@IdClass(classOf[CarIdentifier])
@Access(AccessType.FIELD)
class IdClassCar extends PersistentObjectBase {

  @Id
  var brand: String = _

  @Id
  var year: Int = 0

  var serialNo: String = _

  override def hashCode(): Int = Hashs.compute(serialNo)
}


@Embeddable
@Access(AccessType.FIELD)
class OrderDetailIdentifier extends ValueObject {

  def this(order: Order, product: Product) {
    this()
    this.order = order
    this.product = product
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderId")
  @LazyToOne(LazyToOneOption.PROXY)
  var order: Order = _

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "productId")
  @LazyToOne(LazyToOneOption.PROXY)
  var product: Product = _

  override def hashCode(): Int = Hashs.compute(order, product)
}

@Entity
@Access(AccessType.FIELD)
class OrderDetail extends HibernateEntityBase[OrderDetailIdentifier] {

  def this(id: OrderDetailIdentifier) {
    this()
    this.id = id
  }

  @EmbeddedId
  var id: OrderDetailIdentifier = _

  def getId = id

  var unitPrice: java.math.BigDecimal = _
  var quantity: java.lang.Integer = _
  var discount: java.lang.Float = _

  override def hashCode(): Int = Hashs.compute(unitPrice, quantity, discount)
}


@Entity
@Table(name = "CompositeId_Order")
@Access(AccessType.FIELD)
class Order extends LongEntity {

  var number: String = _

  @Temporal(TemporalType.DATE)
  var orderDate: Date = _

  @OneToMany(mappedBy = "id.order", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.EXTRA)
  var orderDetails: util.List[OrderDetail] = new util.ArrayList[OrderDetail]

  override def hashCode(): Int = Hashs.compute(number)
}

@Entity
@Table(name = "CompositeId_Product")
@Access(AccessType.FIELD)
class Product extends LongEntity {

  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)
}

