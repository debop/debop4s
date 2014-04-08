package debop4s.data.tests.mapping.inheritance

import debop4s.core.utils.Hashs
import debop4s.data.model.LongEntity
import debop4s.data.tests.AbstractJpaTest
import java.util
import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.{annotations => hba}
import org.junit.Test

/**
 * 부모 클래스를 공통으로 사용하고, 자식 클래스들은 각각의 고유의 정보를 각자 테이블에 저장합니다.
 * subclass 와는 달리 자식 클래스들이 not null 속성을 가집니다.
 * 단 MySQL 같이 join 성능이 않좋은 RDBMS에서는 사용을 자제해야 합니다.
 *
 * Created by debop on 2014. 3. 9.
 */
@org.springframework.transaction.annotation.Transactional
class JoinedSubclassTest extends AbstractJpaTest {

    @PersistenceContext val em: EntityManager = null

    @Test
    def joinedSubclass() {
        val employee = new JoinedSubclassEmployee()
        employee.empNo = "21011"
        employee.name = "배성혁"
        employee.regidentNo = "111111-1111111"

        val customer = new JoinedSubclassCustomer()
        customer.name = "customer1"
        customer.contactEmployee = employee
        customer.regidentNo = "222222-2222222"
        customer.mobile = "010-1111-2222"

        em.persist(employee)
        em.persist(customer)
        em.flush()
        em.clear()

        val customer1 = em.find(classOf[JoinedSubclassCustomer], customer.id)
        assert(customer1 == customer)

        val employee1 = em.find(classOf[JoinedSubclassEmployee], employee.id)
        assert(employee1 == employee)

        em.remove(customer1)
        em.remove(employee1)
        em.flush()

        assert(em.find(classOf[JoinedSubclassCustomer], customer.id) == null)
        assert(em.find(classOf[JoinedSubclassEmployee], employee.id) == null)
    }

}

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class JoinedSubclassPerson extends LongEntity {

    @Column(name = "personName", nullable = false, length = 128)
    @Index(name = "ix_joinedsubclassperson_name", columnList = "name, regidentNo")
    var name: String = _

    @Column(nullable = false, length = 128)
    var regidentNo: String = _

    override def hashCode(): Int = Hashs.compute(name, regidentNo)
}

@Entity
@org.hibernate.annotations.Cache(region = "inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class JoinedSubclassCustomer extends JoinedSubclassPerson {

    @Column(nullable = false)
    @Index(name = "ix_joinedsubclasscustomer_mobile", columnList = "mobile")
    var mobile: String = _

    @ManyToOne
    @JoinColumn(name = "contactEmployeeId", nullable = false)
    var contactEmployee: JoinedSubclassEmployee = _

    override def hashCode(): Int = Hashs.compute(super.hashCode(), mobile)
}

@Entity
@org.hibernate.annotations.Cache(region = "inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class JoinedSubclassEmployee extends JoinedSubclassPerson {

    @Column(name = "empNo", nullable = false)
    @Index(name = "ix_joinedsubclassemployee_empno", columnList = "empNo")
    var empNo: String = _

    @ManyToOne
    @JoinColumn(name = "managerId")
    var manager: JoinedSubclassEmployee = _

    @OneToMany(mappedBy = "manager", cascade = Array(CascadeType.ALL))
    @hba.LazyCollection(hba.LazyCollectionOption.EXTRA)
    var members: util.Set[JoinedSubclassEmployee] = new util.HashSet[JoinedSubclassEmployee]

    override def hashCode(): Int = Hashs.compute(super.hashCode(), empNo)
}
