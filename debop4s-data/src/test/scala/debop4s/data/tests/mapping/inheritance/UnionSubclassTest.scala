package debop4s.data.tests.mapping.inheritance

import debop4s.core.utils.Hashs
import debop4s.data.model.UuidEntity
import debop4s.data.tests.AbstractJpaTest
import java.util.Date
import javax.persistence._
import org.hibernate.{annotations => hba}
import org.junit.Test


/**
 * 여러 테이블에 걸쳐 Identity를 유지하기 위해 Sequence 를 제작 (HSql, PostgreSql 에서만 지원)
 *
 * Created by debop on 2014. 3. 9.
 */
@org.springframework.transaction.annotation.Transactional
class UnionSubclassTest extends AbstractJpaTest {

    @PersistenceContext val em: EntityManager = null

    @Test
    def unionSubclass() {

        val bankAccount = new UnionSubclassBankAccount()
        bankAccount.account = "account"
        bankAccount.bankname = "bank name"
        bankAccount.owner = "debop"
        em.persist(bankAccount)
        em.flush()

        val creditCard = new UnionSubclassCreditCard()
        creditCard.number = "1111-1111-1111-1111"
        creditCard.companyName = "카드사"
        creditCard.expYear = 2020
        creditCard.expMonth = 12
        creditCard.owner = "debop"
        em.persist(creditCard)
        em.flush()

        em.clear()

        assert(bankAccount.id != null)
        val bankAccount1 = em.find(classOf[UnionSubclassBankAccount], bankAccount.id)
        assert(bankAccount1 == bankAccount)

        val creditCard1 = em.find(classOf[UnionSubclassCreditCard], creditCard.id)
        assert(creditCard1 == creditCard)

        em.remove(bankAccount1)
        em.remove(creditCard1)
        em.flush()
        em.clear()

        assert(em.find(classOf[UnionSubclassBankAccount], bankAccount.id) == null)
        assert(em.find(classOf[UnionSubclassCreditCard], creditCard.id) == null)
    }
}

/**
 * 상속관계의 엔테티들을 독립적인 테이블로 만든다.
 * 주의할 점은 Identifier 는 상속된 모든 class에 대해 고유한 값을 가져야 한다. (테이블 범위의 identity는 사용하면 안된다)
 *
 * 여러 테이블에 걸쳐 Identity를 유지하기 위해 고유의 값을 가지도록 UUID를 사용한다.
 */
@Entity
@hba.Cache(region = "inheritance", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@hba.DynamicInsert
@hba.DynamicUpdate
abstract class UnionSubclassBillingBase extends UuidEntity {

    @Column(name = "owner", nullable = false)
    var owner: String = _

    override def hashCode(): Int = Hashs.compute(owner)
}

@Entity
@hba.Cache(region = "inheritance", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
class UnionSubclassBankAccount extends UnionSubclassBillingBase {

    @Column(nullable = false)
    var account: String = _

    @Column(nullable = false)
    var bankname: String = _

    var swift: String = _

    override def hashCode(): Int = Hashs.compute(owner, account)
}

@Entity
@hba.Cache(region = "inheritance", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
class UnionSubclassCreditCard extends UnionSubclassBillingBase {

    @Column(nullable = false)
    var companyName: String = _

    @Column(nullable = false)
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