package com.github.debop4s.data.tests.mapping.embeddable

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.model.LongEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import javax.persistence._
import org.hibernate.annotations.{DynamicUpdate, DynamicInsert}
import org.junit.Test

/**
 * Created by debop on 2014. 3. 9.
 */
@org.springframework.transaction.annotation.Transactional
class EmbeddableTest extends AbstractJpaTest {

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

    val loaded = em.find(classOf[EmbeddableUser], user.id)
    assert(loaded != null)
    assert(loaded.homeAddress == user.homeAddress)
    assert(loaded.officeAddress == user.officeAddress)

    em.remove(loaded)
    em.flush()

    assert(em.find(classOf[EmbeddableUser], user.id) == null)
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
@Index(name = "idx_user_username", columnList = "username, password")
class EmbeddableUser extends LongEntity {

  var firstname: String = _
  var lastname: String = _

  @Column(length = 128, nullable = false)
  var username: String = _

  @Column(length = 64, nullable = false)
  var password: String = _

  @Column(length = 128, nullable = false)
  @Index(name = "ix_user_email", columnList = "email")
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
