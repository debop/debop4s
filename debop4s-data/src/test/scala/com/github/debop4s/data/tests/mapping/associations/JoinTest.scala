package com.github.debop4s.data.tests.mapping.associations

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.model.HibernateEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.util
import java.util.Date
import javax.persistence._
import org.hibernate.annotations.{Cascade, FetchMode, Fetch, GenerationTime}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository


/**
 * JoinTest 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
class JoinTest extends AbstractJpaTest {

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
}

@Embeddable
class JoinAddress extends ValueObject {

    var street: String = _
    var city: String = _
    var zipcode: String = _

    override def hashCode(): Int = Hashs.compute(street, city, zipcode)
}

@Entity
class JoinAddressEntity extends HibernateEntity[Long] {

    @Id
    @GeneratedValue
    val id: Long = 0

    var street: String = _
    var city: String = _
    var zipcode: String = _

    override def getId: Long = id

    override def hashCode(): Int = Hashs.compute(street, city, zipcode)
}

@Entity
@Table(name = "JoinCustomer")
@SecondaryTable(name = "JoinCustomerAddress", pkJoinColumns = Array(new PrimaryKeyJoinColumn(name = "customerId")))
class JoinCustomer extends HibernateEntity[java.lang.Long] {

    @Id
    @GeneratedValue
    val id: Long = 0

    override def getId: java.lang.Long = 0

    var name: String = _
    var email: String = _

    @Embedded
    @AttributeOverrides(Array(new AttributeOverride(name = "street", column = new Column(name = "street", table = "JoinCustomerAddress")),
                                 new AttributeOverride(name = "city", column = new Column(name = "city", table = "JoinCustomerAddress")),
                                 new AttributeOverride(name = "zipcode", column = new Column(name = "zipcode", table = "JoinCustomerAddress"))
                             ))
    val joinAddress: JoinAddress = new JoinAddress()

    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    @Column(insertable = false, updatable = false)
    var createdAt: Date = _

    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
    var updatedAt: Date = _

    override def hashCode(): Int = Hashs.compute(name, email)
}


@Entity
class JoinUser extends HibernateEntity[java.lang.Long] {

    @Id
    @GeneratedValue
    val id: java.lang.Long = 0

    override def getId: java.lang.Long = 0

    var name: String = _

    // @OneToMany 를 이용한 Mapping 대상은 Entity여야 합니다.
    //
    @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)
    @JoinTable(name = "JoinUserAddressMap")
    @MapKeyColumn(name = "nick")
    @ElementCollection(targetClass = classOf[JoinAddressEntity], fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    var addresses: util.Map[String, JoinAddressEntity] = new util.HashMap[String, JoinAddressEntity]()

    @JoinTable(name = "JoinUserNicknameMap", joinColumns = Array(new JoinColumn(name = "userId")))
    @ElementCollection(targetClass = classOf[String], fetch = FetchType.EAGER)
    @Cascade(value = Array(org.hibernate.annotations.CascadeType.ALL))
    var nicknames: util.Set[String] = new util.HashSet[String]()

    override def hashCode(): Int = Hashs.compute(name)
}

// Repository는 Java로 만들어야겠다. QueryDSL이 제재로 안되네...

trait JoinCustomerRepository
    extends JpaRepository[JoinCustomer, java.lang.Long] {
    //with QueryDslPredicateExecutor[JoinCustomer] {

    def findByName(name: String): JoinCustomer

    def findByNameLike(name: String): util.List[JoinCustomer]

    def findByEmail(email: String): JoinCustomer
}

trait JoinUserRepository
    extends JpaRepository[JoinUser, java.lang.Long] {
    // with QueryDslPredicateExecutor[JoinUser] {

}