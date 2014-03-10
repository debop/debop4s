package com.github.debop4s.data.tests.mapping.inheritance

import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.model.LongEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.util.Date
import javax.persistence._
import org.hibernate.{annotations => hba}
import org.junit.Test


/**
 * subclass 로 구현한 상속 클래스의 속성은 모두 nullable 이어야 한다.
 * (한 테이블에 다른 클래스의 속성이 들어갈 때 현 클래스의 속성은 nullable 로 설정되어야 한다.)
 * Created by debop on 2014. 3. 9.
 */
@org.springframework.transaction.annotation.Transactional
class SubclassTest extends AbstractJpaTest {

  @PersistenceContext val em: EntityManager = null

  @Test
  def subclass() {
    val bankAccount = new SubclassBankAccount()
    bankAccount.account = "account"
    bankAccount.bankname = "bank name"
    bankAccount.owner = "debop"
    em.persist(bankAccount)

    val creditCard = new SubclassCreditCard()
    creditCard.number = "1111-1111-1111-1111"
    creditCard.companyName = "카드사"
    creditCard.expYear = 2020
    creditCard.expMonth = 12
    creditCard.owner = "debop"
    em.persist(creditCard)

    em.flush()
    em.clear()

    val bankAccount1 = em.find(classOf[SubclassBankAccount], bankAccount.id)
    assert(bankAccount1 == bankAccount)

    val creditCard1 = em.find(classOf[SubclassCreditCard], creditCard.id)
    assert(creditCard1 == creditCard)

    em.remove(bankAccount1)
    em.remove(creditCard1)
    em.flush()
    em.clear()

    assert(em.find(classOf[SubclassBankAccount], bankAccount.id) == null)
    assert(em.find(classOf[SubclassCreditCard], creditCard.id) == null)
  }

}

/**
 *  한 테이블에 Super-Sub class 들 모두 저장됩니다. (subclass)
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@hba.DynamicInsert
@hba.DynamicUpdate
abstract class SubclassBillingBase extends LongEntity {

  @Column(nullable = false)
  @Index(name = "ix_subclass_billing_owner", columnList = "owner")
  var owner: String = _

  override def hashCode(): Int = Hashs.compute(owner)
}

@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class SubclassBankAccount extends SubclassBillingBase {

  var account: String = _
  var bankname: String = _
  var swift: String = _

  override def hashCode(): Int = Hashs.compute(owner, account)
}

@Entity
@hba.Cache(region = "inheritance", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class SubclassCreditCard extends SubclassBillingBase {

  var companyName: String = _
  var number: String = _
  var expMonth: Integer = _
  var expYear: Integer = _

  @Temporal(TemporalType.DATE)
  var startDate: Date = _

  @Temporal(TemporalType.DATE)
  var endDate: Date = _

  var swift: String = _

  override def hashCode(): Int = Hashs.compute(owner, number)
}