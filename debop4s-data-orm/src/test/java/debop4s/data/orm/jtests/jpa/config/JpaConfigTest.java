package debop4s.data.orm.jtests.jpa.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.jpa.config.JpaConfigTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 21. 오후 2:34
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaConfig.class })
@Transactional
public class JpaConfigTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    JpaAccountRepository repository;

    @Test
    public void configurationTest() {
        assertThat(em).isNotNull();
    }

    @Test
    public void crudTest() {
        JpaAccount account = new JpaAccount();
        account.setName("Sunghyouk Bae");
        account.setCashBalance(100.0);
        account = repository.saveAndFlush(account);
        em.clear();

        JpaAccount loaded = repository.findOne(account.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(account);


        assertThat(loaded.isPersisted()).isTrue();
    }
}
