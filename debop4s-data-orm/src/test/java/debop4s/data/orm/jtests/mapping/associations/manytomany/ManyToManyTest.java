package debop4s.data.orm.jtests.mapping.associations.manytomany;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
@Transactional
public class ManyToManyTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void manyToManyTest() {

        AccountOwner owner = new AccountOwner();
        owner.setSSN("0123456");
        BankAccount soge = new BankAccount();
        soge.setAccountNumber("X2345000");
        owner.getBankAccounts().add(soge);
        soge.getOwners().add(owner);

        // mappedBy 가 AccountOwner로 설정되었다는 것은 AccountOwner 를 기준으로 cascading 이 된다는 뜻이다.
        em.persist(owner);
        em.flush();
        em.clear();

        // read from inverse side
        soge = em.find(BankAccount.class, soge.getId());
        assertThat(soge.getOwners()).hasSize(1);
        assertThat(soge.getOwners()).onProperty("id").contains(owner.getId());

        em.clear();

        // read from non-inverse side and update orm
        owner = em.find(AccountOwner.class, owner.getId());
        assertThat(owner.getBankAccounts().size()).isEqualTo(1);
        assertThat(owner.getBankAccounts()).onProperty("id").contains(soge.getId());

        soge = owner.getBankAccounts().iterator().next();
        log.debug("BankAccount=[{}]", soge);
        owner.getBankAccounts().remove(soge);

        BankAccount barclays = new BankAccount();
        barclays.setAccountNumber("ZZZ-009");
        barclays.getOwners().add(owner);
        owner.getBankAccounts().add(barclays);

        em.remove(soge);
        em.flush();
        em.clear();

        // del orm
        owner = em.find(AccountOwner.class, owner.getId());
        assertThat(owner.getBankAccounts().size()).isEqualTo(1);
        assertThat(owner.getBankAccounts().iterator().next().getId()).isEqualTo(barclays.getId());

        for (BankAccount account : owner.getBankAccounts()) {
            log.debug("BankAccount=[{}]", account);
        }

        barclays = owner.getBankAccounts().iterator().next();
        barclays.getOwners().clear();
        owner.getBankAccounts().clear();

        em.flush();
        em.clear();
    }
}
