package debop4s.data.orm.mapping.embeddable

import javax.persistence._

import debop4s.core.ValueObject
import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.LongEntity
import org.hibernate.annotations.{DynamicInsert, DynamicUpdate}
import org.junit.Test

@org.springframework.transaction.annotation.Transactional
class EmbeddableJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  def embeddableAddress() {
    val user = new EmbeddableUser()
    user.username = "debop"
    user.password = "1234"
    user.email = "debop@gmail.com"

    user.homeAddress.city = "서울"
    user.homeAddress.street = "정릉"
    user.homeAddress.zipcode = "100-200"

    user.officeAddress.city = "서울"
    user.officeAddress.street = "안국"
    user.officeAddress.zipcode = "100-999"

    em.persist(user)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[EmbeddableUser], user.getId)
    assert(loaded != null)
    assert(loaded.homeAddress == user.homeAddress)
    assert(loaded.officeAddress == user.officeAddress)

    em.remove(loaded)
    em.flush()

    assert(em.find(classOf[EmbeddableUser], user.getId) == null)
  }
}

@Embeddable
@DynamicInsert
@DynamicUpdate
class EmbeddableAddress extends ValueObject {

  var street: String = _
  var zipcode: String = _
  var city: String = _

  override def hashCode(): Int = Hashs.compute(street, zipcode, city)
}

@Entity
@Access(AccessType.FIELD)
@org.hibernate.annotations.Index(name = "idx_user_username", columnNames = Array("username", "password"))
// @Index(name = "idx_user_username", columnList = "username, password")
class EmbeddableUser extends LongEntity {

  var firstname: String = _
  var lastname: String = _

  @Column(length = 128, nullable = false)
  var username: String = _

  @Column(length = 64, nullable = false)
  var password: String = _

  @Column(length = 128, nullable = false)
  @org.hibernate.annotations.Index(name = "ix_user_email", columnNames = Array("email"))
  //    @Index(name = "ix_user_email", columnList = "email")
  var email: String = _

  var active: Boolean = true

  @Embedded
  @AttributeOverrides(Array(
                             new AttributeOverride(name = "street", column = new Column(name = "HomeStreet", length = 128)),
                             new AttributeOverride(name = "zipcode", column = new Column(name = "HomeZipCode", length = 24)),
                             new AttributeOverride(name = "city", column = new Column(name = "HomeCity", length = 128))
                           ))
  var homeAddress = new EmbeddableAddress()

  @Embedded
  @AttributeOverrides(Array(
                             new AttributeOverride(name = "street", column = new Column(name = "OfficeStreet", length = 128)),
                             new AttributeOverride(name = "zipcode", column = new Column(name = "OfficeZipCode", length = 24)),
                             new AttributeOverride(name = "city", column = new Column(name = "OfficeCity", length = 128))
                           ))
  var officeAddress = new EmbeddableAddress()

  override def hashCode(): Int = Hashs.compute(username, password)
}
