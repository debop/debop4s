package debop4s.data.orm.mapping.associations

import java.util
import java.util.Date
import javax.persistence._

import debop4s.core.ValueObjectBase
import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.HibernateEntityBase
import org.hibernate.{annotations => hba}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Transactional
class JoinJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null
  @Autowired val userRepository: JoinUserRepository = null
  @Autowired val customerRepository: JoinCustomerRepository = null

  @Test
  def configurationTest() {
    assert(emf != null)
    assert(em != null)
    assert(userRepository != null)
    assert(customerRepository != null)
  }

  @Test
  def joinUser() {
    val user = new JoinUser()
    user.nicknames.add("debop")
    user.nicknames.add("sunghyouk")

    val home = new JoinAddressEntity()
    home.city = "Seoul"
    home.street = "Jungreung"
    home.zipcode = "100-100"
    user.addresses.put("home", home)

    val office = new JoinAddressEntity()
    office.city = "Seoul"
    office.street = "Ankook"
    office.zipcode = "200-200"
    user.addresses.put("office", office)

    userRepository.saveAndFlush(user)
    em.clear()

    val loaded = userRepository.findOne(user.id)

    assert(loaded == user)
    assert(loaded.addresses.size == 2)
    assert(loaded.nicknames.size == 2)

    userRepository.delete(loaded)
    userRepository.flush()

    assert(userRepository.findOne(user.id) == null)
  }

  @Test
  def joinCustomer() {
    val customer = new JoinCustomer
    customer.name = "배성혁"
    customer.email = "sunghyouk.bae@gmail.com"

    val addr = new JoinAddress
    addr.city = "Seoul"
    addr.street = "Jungreung"
    addr.zipcode = "100-100"
    customer.joinAddress = addr
    customerRepository.save(customer)
    em.flush()
    em.clear()

    val loaded: JoinCustomer = customerRepository.findByName(customer.name)

    assert(loaded != null)
    assert(loaded.joinAddress != null)
    assert(loaded.joinAddress.city == addr.city)

    customerRepository.delete(loaded)
    val loaded2 = customerRepository.findByName(customer.name)
    assert(loaded2 == null)
  }
}

@Embeddable
@Access(AccessType.FIELD)
class JoinAddress extends ValueObjectBase {

  var street: String = _
  var city: String = _
  var zipcode: String = _

  override def hashCode(): Int = Hashs.compute(street, city, zipcode)
}

@Entity
@hba.Cache(region = "associations", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class JoinAddressEntity extends HibernateEntityBase[java.lang.Long] {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  var street: String = _
  var city: String = _
  var zipcode: String = _

  override def getId: java.lang.Long = id

  override def hashCode(): Int = Hashs.compute(street, city, zipcode)
}

@Entity
@hba.Cache(region = "associations", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
@Table(name = "JoinCustomer")
@SecondaryTable(name = "JoinCustomerAddress", pkJoinColumns = Array(new PrimaryKeyJoinColumn(name = "customerId")))
class JoinCustomer extends HibernateEntityBase[java.lang.Long] {

  @Id
  @GeneratedValue
  @Column(name = "customerId")
  var id: java.lang.Long = _

  override def getId: java.lang.Long = 0

  var name: String = _
  var email: String = _

  @Embedded
  @AttributeOverrides(Array(new AttributeOverride(name = "street", column = new Column(name = "street", table = "JoinCustomerAddress")),
                             new AttributeOverride(name = "city", column = new Column(name = "city", table = "JoinCustomerAddress")),
                             new AttributeOverride(name = "zipcode", column = new Column(name = "zipcode", table = "JoinCustomerAddress"))
                           ))
  var joinAddress: JoinAddress = _

  @PrePersist
  def onPrePersist() {
    this.createdAt = new util.Date()
  }

  @PreUpdate
  def onPreUpdate() {
    this.updatedAt = new util.Date()
  }

  @Temporal(TemporalType.TIMESTAMP)
  // @org.hibernate.annotations.Generated(GenerationTime.INSERT)
  @Column(insertable = false, updatable = false)
  var createdAt: Date = _

  @Temporal(TemporalType.TIMESTAMP)
  // @org.hibernate.annotations.Generated(GenerationTime.ALWAYS)
  @Column(insertable = false, updatable = false)
  var updatedAt: Date = _

  override def hashCode(): Int = Hashs.compute(name, email)
}


@Entity
@hba.Cache(region = "associations", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class JoinUser extends HibernateEntityBase[java.lang.Long] {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  override def getId: java.lang.Long = id

  var name: String = _

  // @OneToMany 를 이용한 Mapping 대상은 Entity여야 합니다.
  //
  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @JoinTable(name = "JoinUserAddressMap",
              joinColumns = Array(new JoinColumn(name = "userId")),
              inverseJoinColumns = Array(new JoinColumn(name = "addressId")))
  @MapKeyColumn(name = "nick")
  @ElementCollection(targetClass = classOf[JoinAddressEntity], fetch = FetchType.EAGER)
  @hba.Fetch(hba.FetchMode.SUBSELECT)
  var addresses: util.Map[String, JoinAddressEntity] = new util.HashMap[String, JoinAddressEntity]()

  @JoinTable(name = "JoinUserNicknameMap", joinColumns = Array(new JoinColumn(name = "userId")))
  @ElementCollection(targetClass = classOf[String], fetch = FetchType.EAGER)
  @hba.Cascade(value = Array(hba.CascadeType.ALL))
  var nicknames: util.Set[String] = new util.HashSet[String]()

  override def hashCode(): Int = Hashs.compute(name)
}

// Repository는 Java로 만들어야겠다. QueryDSL이 제재로 안되네...

@Repository
trait JoinCustomerRepository extends JpaRepository[JoinCustomer, java.lang.Long] {
  //with QueryDslPredicateExecutor[JoinCustomer] {

  def findByName(name: String): JoinCustomer

  def findByNameLike(name: String): util.List[JoinCustomer]

  def findByEmail(email: String): JoinCustomer
}

@Repository
trait JoinUserRepository extends JpaRepository[JoinUser, java.lang.Long] {
  // with QueryDslPredicateExecutor[JoinUser] {

}