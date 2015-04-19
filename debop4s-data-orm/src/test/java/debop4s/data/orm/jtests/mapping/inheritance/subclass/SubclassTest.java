package debop4s.data.orm.jtests.mapping.inheritance.subclass;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.inheritance.subclass.SubclassJUnitSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 5:42
 */
@Slf4j
@Transactional
public class SubclassTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void subclassEntity() {
        SubclassBankAccount bankAccount = new SubclassBankAccount();
        bankAccount.setAccount("account");
        bankAccount.setBankname("은행이름");
        bankAccount.setOwner("배성혁");

        SubclassCreditCard creditCard = new SubclassCreditCard();
        creditCard.setNumber("1111-1111-1111-1111");
        creditCard.setExpYear(2020);
        creditCard.setExpMonth(12);
        creditCard.setOwner("배성혁");

        em.persist(bankAccount);
        em.persist(creditCard);
        em.flush();

        em.clear();

        SubclassBankAccount bankAccount1 = em.find(SubclassBankAccount.class, bankAccount.getId());
        assertThat(bankAccount1).isEqualTo(bankAccount);

        SubclassCreditCard creditCard1 = em.find(SubclassCreditCard.class, creditCard.getId());
        assertThat(creditCard1).isEqualTo(creditCard);

        em.remove(bankAccount1);
        em.remove(creditCard1);

        assertThat(em.find(SubclassBankAccount.class, bankAccount.getId())).isNull();
        assertThat(em.find(SubclassCreditCard.class, creditCard.getId())).isNull();
    }

}
