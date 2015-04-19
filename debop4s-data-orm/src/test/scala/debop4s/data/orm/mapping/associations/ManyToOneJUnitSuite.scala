package debop4s.data.orm.mapping.associations

import java.{lang, util}
import javax.persistence._

import debop4s.core.ToStringHelper
import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.HibernateEntityBase
import org.hibernate.annotations.{LazyCollection, LazyCollectionOption, LazyToOne, LazyToOneOption}
import org.junit.Test

/**
 * ManyToOneJUnitSuite
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 3. 3.
 */
@org.springframework.transaction.annotation.Transactional
class ManyToOneJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  def unidirectionalManyToOne() {
    val jug = new Jug("JUG Summer Camp")
    val emmanuel = new JugMember("Emmanuel Bernard")
    emmanuel.memberOf = jug

    val jerome = new JugMember("Jerome")
    jerome.memberOf = jug

    em.persist(jug)
    em.persist(emmanuel)
    em.persist(jerome)
    em.flush()
    em.clear()

    val emmanuel2 = em.find(classOf[JugMember], emmanuel.id)
    assert(emmanuel2 != null)
    assert(emmanuel2.id == emmanuel.id)
    assert(emmanuel2.memberOf != null)
    assert(emmanuel2.memberOf == jug)

    em.remove(emmanuel2)
    em.flush()

    val jerome2 = em.find(classOf[JugMember], jerome.id)
    assert(jerome2 != null)
    assert(jerome2.memberOf != null)

    val jug2 = jerome2.memberOf
    em.remove(jerome2)
    em.remove(jug2)
    em.flush()

    assert(em.find(classOf[Jug], jug.id) == null)
  }

}

@Entity
@Access(AccessType.FIELD)
class Beer extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  var price: lang.Double = _

  @ManyToOne(fetch = FetchType.LAZY, optional = false,
              cascade = Array(CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH))
  @JoinColumn(nullable = false)
  @LazyToOne(LazyToOneOption.PROXY)
  var brewery: Brewery = _

  override def hashCode(): Int = Hashs.compute(name)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("name", name)
    .add("price", price)
}


@Entity
@Access(AccessType.FIELD)
class Brewery extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  @OneToMany(cascade = Array(CascadeType.ALL), fetch = FetchType.LAZY)
  @LazyCollection(LazyCollectionOption.EXTRA)
  val beers: util.Set[Beer] = new util.HashSet[Beer]()

  override def hashCode(): Int =
    Hashs.compute(name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("name", name)
}

@Entity
@Access(AccessType.FIELD)
class Jug extends HibernateEntityBase[lang.Long] {

  def this(name: String) {
    this()
    this.name = name
  }

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  override def hashCode(): Int =
    Hashs.compute(name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("name", name)
}


@Entity
@Access(AccessType.FIELD)
class JugMember(private[this] val _name: String) extends HibernateEntityBase[lang.Long] {

  def this() {
    this(null)
  }

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _name

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "memberOf", nullable = false)
  @LazyToOne(LazyToOneOption.PROXY)
  var memberOf: Jug = _

  override def hashCode(): Int =
    Hashs.compute(name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("name", name)
}

@Entity
@Access(AccessType.FIELD)
class SalesForce extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var corporation: String = _

  @OneToMany(mappedBy = "salesForce", cascade = Array(CascadeType.ALL))
  @LazyCollection(LazyCollectionOption.EXTRA)
  val salesGuys: util.Set[SalesGuy] = new util.HashSet[SalesGuy]

  override def hashCode(): Int =
    Hashs.compute(corporation)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("corporation", corporation)
}

@Entity
@Access(AccessType.FIELD)
class SalesGuy extends HibernateEntityBase[lang.Long] {

  def this(name: String) {
    this()
    this.name = name
  }

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  @ManyToOne(fetch = FetchType.LAZY, cascade = Array(CascadeType.ALL))
  @JoinColumn(name = "salesForceId")
  @LazyToOne(LazyToOneOption.PROXY)
  var salesForce: SalesForce = _

}