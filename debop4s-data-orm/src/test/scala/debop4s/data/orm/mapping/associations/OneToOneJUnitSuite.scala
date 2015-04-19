package debop4s.data.orm.mapping.associations

import java.lang
import java.util.Date
import javax.persistence._

import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.HibernateEntityBase
import org.hibernate.annotations.Cascade
import org.junit.Test
import org.springframework.transaction.annotation.Transactional

/**
 * OneToOneJUnitSuite
 * Created by debop on 2014. 3. 6.
 */
@Transactional
class OneToOneJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  /**
   * Bidiractional One-To-One Mapping
   */
  @Test
  def authorBiography() {

    var author = new OneToOneAuthor()
    author.name = "debop"

    author.biography.information = "Sunghyouk Bae"
    author.picture.picturePath = "file://a/b/c"

    em.persist(author)
    em.flush()
    em.clear()

    // biography 로드 시 author는 lazy loading
    log.debug("load biography")
    val biography = em.find(classOf[OneToOneBiography], author.id)
    biography.author should not be null
    biography.author.id shouldEqual author.id

    log.debug("load author...")
    author = em.find(classOf[OneToOneAuthor], author.id)
    assert(author != null)

    val bio = author.biography
    assert(bio != null)
    assert(bio.information == "Sunghyouk Bae")

    em.remove(author)
    em.flush()

    assert(em.find(classOf[OneToOneAuthor], author.id) == null)
  }

  @Test
  def unidirectionalManyToOne() {
    val horse = new Horse()
    horse.name = "적토마"

    val cavalier = new Cavalier()
    cavalier.name = "관우"
    cavalier.horse = horse

    em.persist(horse)
    em.persist(cavalier)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[Cavalier], cavalier.getId)
    assert(loaded != null)
    assert(loaded.horse != null)

    em.remove(loaded)
    em.remove(loaded.horse)
    em.flush()
    em.clear()

    assert(em.find(classOf[Cavalier], cavalier.getId) == null)
  }

  @Test
  def unidirectionalOneToOne() {
    val vehicle = new Vehicle()
    vehicle.brand = "Mercedes"

    var wheel = new Wheel()
    wheel.vehicle = vehicle

    em.persist(vehicle)
    em.persist(wheel)
    em.flush()
    em.clear()

    wheel = em.find(classOf[Wheel], wheel.id)
    assert(wheel != null)

    val vehicle2 = wheel.vehicle
    assert(vehicle2 != null)

    em.remove(wheel)
    em.remove(vehicle2)
    em.flush()

    assert(em.find(classOf[Vehicle], vehicle.id) == null)
  }
}

@Entity
@Access(AccessType.FIELD)
class Cavalier extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  // Unidirectional One To One
  @OneToOne
  @JoinColumn(name = "horseId")
  var horse: Horse = _

  override def hashCode(): Int = Hashs.compute(name)
}

@Entity
@Access(AccessType.FIELD)
class Horse extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)
}

@Entity(name = "OneToOne_Husband")
@Access(AccessType.FIELD)
class Husband extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wifeId")
  var wife: Wife = _

  override def hashCode(): Int = Hashs.compute(name)
}

@Entity(name = "OneToOne_Wife")
@Access(AccessType.FIELD)
class Wife extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  @OneToOne(mappedBy = "wife", fetch = FetchType.LAZY)
  var husband: Husband = _

  override def hashCode(): Int = Hashs.compute(name)
}

@Entity
@Access(AccessType.FIELD)
class OneToOneAddress extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var street: String = _
  var city: String = _
  var zipcode: String = _

  override def hashCode(): Int = Hashs.compute(street, zipcode, city)
}


@Entity
@Access(AccessType.FIELD)
class OneToOneAuthor extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  var biography = new OneToOneBiography(this)

  @OneToOne(cascade = Array(CascadeType.ALL), fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn(name = "authorId")
  var picture = new OneToOneAuthorPicture(this)

  override def hashCode(): Int = Hashs.compute(name)
}

@Entity
@Access(AccessType.FIELD)
class OneToOneBiography(private[this] val _author: OneToOneAuthor) extends HibernateEntityBase[lang.Long] {

  def this() {
    this(null)
  }

  @Id
  @Column
  var id: lang.Long = _

  def getId = id

  // JPA 2 스타일의 설정 방식
  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "authorId")
  var author = _author

  var information: String = _

  override def hashCode(): Int = Hashs.compute(id)
}

@Entity
@Access(AccessType.FIELD)
class OneToOneAuthorPicture(private[this] val _author: OneToOneAuthor) extends HibernateEntityBase[lang.Long] {

  def this() {
    this(null)
  }

  @Id
  @Column(name = "authorId")
  var id: lang.Long = _

  def getId = id

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "authorId")
  var author = _author

  var picturePath: String = _

  override def hashCode(): Int = Hashs.compute(id)
}

@Entity
@Access(AccessType.FIELD)
class OneToOneItem extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  var description: String = _

  var initialPrice: java.math.BigDecimal = _

  override def hashCode(): Int = Hashs.compute(name)
}

@Entity
@Table(name = "OneToOneShipment")
@SecondaryTable(name = "OneToOneShipmentItem")
@Access(AccessType.FIELD)
class OneToOneShipment extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var state: String = _
  var createOn: Date = _

  @ManyToOne
  @JoinColumn(table = "OneToOneShipmentItem", name = "ItemId")
  var action = new OneToOneItem()

  override def hashCode(): Int = Hashs.compute(state, createOn)
}

@Entity
@Access(AccessType.FIELD)
class OneToOneUser extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var firstname: String = _
  var lastname: String = _
  var username: String = _
  var password: String = _
  var email: String = _
  var ranking: String = _
  var admin: String = _

  @OneToOne
  @JoinColumn(name = "shippingAddressId")
  var shppingAddress = new OneToOneAddress

  override def hashCode(): Int = Hashs.compute(username, email)
}


@Entity
@Access(AccessType.FIELD)
class Vehicle extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var brand: String = _

  override def hashCode(): Int = Hashs.compute(brand)
}

@Entity
@Access(AccessType.FIELD)
class Wheel extends HibernateEntityBase[lang.Long] {

  @Id
  var id: lang.Long = _

  def getId = id

  var name: String = _

  var diameter: lang.Double = _

  @MapsId
  @PrimaryKeyJoinColumn
  @OneToOne(cascade = Array(CascadeType.PERSIST))
  @Cascade(Array(org.hibernate.annotations.CascadeType.SAVE_UPDATE))
  var vehicle: Vehicle = _


}