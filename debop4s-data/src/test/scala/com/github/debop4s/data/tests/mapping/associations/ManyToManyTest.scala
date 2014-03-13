package com.github.debop4s.data.tests.mapping.associations

import com.github.debop4s.core.utils.{ToStringHelper, Hashs}
import com.github.debop4s.data.model.HibernateEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.lang.{Long => jLong}
import java.util
import javax.persistence._
import org.hibernate.{annotations => hba}
import org.junit.Test
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

/**
 * ManyToManyTest 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 3. 3.
 */
@org.springframework.transaction.annotation.Transactional
class ManyToManyTest extends AbstractJpaTest {

    private val log = LoggerFactory.getLogger(getClass)

    @PersistenceContext private val em: EntityManager = null

    @Test
    def manyToMany() {
        val owner = new AccountOwner()
        owner.SSN = "0123456"
        val soge = new BankAccount()
        soge.accountNumber = "X2345000"

        soge.owners.add(owner)
        owner.bankAccounts.add(soge)

        // mappedBy가 AccountOwner로 설정되었다는 것은 AccountOwner를 기준으로 cascading이 된다는 뜻이다.
        // em.persist(soge)
        em.persist(owner)
        em.flush()
        em.clear()

        val soge2 = em.find(classOf[BankAccount], soge.id)
        assert(soge2.owners.size == 1)
        assert(soge2.owners.exists(o => o.id == owner.id))

        em.clear()

        val owner2 = em.find(classOf[AccountOwner], owner.id)
        assert(owner2.bankAccounts.size == 1)
        assert(owner2.bankAccounts.exists(x => x.id == soge.id))

        val soge3 = owner2.bankAccounts.head
        log.debug(s"BankAccount = $soge3")

        owner2.bankAccounts.remove(soge3)

        val barclays = new BankAccount()
        barclays.accountNumber = "ZZZ-999"
        barclays.owners.add(owner2)
        owner2.bankAccounts.add(barclays)

        em.remove(soge3)
        em.flush()
        em.clear()

        val owner3 = em.find(classOf[AccountOwner], owner.id)
        assert(owner3.bankAccounts.size == 1)
        assert(owner3.bankAccounts.exists(x => x.id == barclays.id))

        owner3.bankAccounts.foreach { x =>
            log.debug(s"BankAccount=$x")
        }

        val barclays2 = owner3.bankAccounts.head
        barclays.owners.clear()
        owner3.bankAccounts.clear()

        em.persist(owner3)
        em.persist(barclays2)
        em.flush()
    }

}

@Entity
@Access(AccessType.FIELD)
@hba.Cache(region = "association", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class AccountOwner extends HibernateEntity[java.lang.Long] {

    // NOTE: LongEntity를 사용하고 싶지만, BankAccounts 에 association 되는 컬럼을 찾지 못하는 버그가 있다.
    // NOTE: 어쩔 수 없이 id의 컬럼명을 정의하기 위해 (ownerId) 아래와 같이 정의하였다.
    @Id
    @GeneratedValue
    @Column(name = "ownerId")
    var id: java.lang.Long = _

    override def getId = id

    @Column(length = 32)
    var SSN: String = _

    @ManyToMany(cascade = Array(CascadeType.ALL))
    @JoinTable(name = "BankAccounts",
        joinColumns = Array(new JoinColumn(name = "ownerId")),
        inverseJoinColumns = Array(new JoinColumn(name = "accountId")))
    @hba.LazyCollection(hba.LazyCollectionOption.EXTRA)
    var bankAccounts: util.Set[BankAccount] = new util.HashSet[BankAccount]()

    override def hashCode(): Int = Hashs.compute(SSN)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
        .add("SSN", SSN)
}

@Entity
@Access(AccessType.FIELD)
@hba.Cache(region = "association", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class BankAccount extends HibernateEntity[java.lang.Long] {

    // NOTE: LongEntity를 사용하고 싶지만, BankAccounts 에 association 되는 컬럼을 찾지 못하는 버그가 있다.
    // NOTE: 어쩔 수 없이 id의 컬럼명을 정의하기 위해 (ownerId) 아래와 같이 정의하였다.
    @Id
    @GeneratedValue
    @Column(name = "accountId")
    var id: java.lang.Long = _

    override def getId = id

    @Column(length = 32)
    var accountNumber: String = _

    // NOTE: @ManyToMany 에서는 둘 중 하나는 mappedBy 를 지정해야 합니다.
    @ManyToMany(mappedBy = "bankAccounts")
    var owners: util.Set[AccountOwner] = new util.HashSet[AccountOwner]()

    override def hashCode(): Int = Hashs.compute(accountNumber)
}


