package debop4s.data.orm.jtests.hibernate.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfig.class })
public class HibernateConfigTest {

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void configuration() throws Exception {
        assertThat(sessionFactory).isNotNull();
    }

    @Test
    @Transactional
    public void retrieveAccount() {
        Session session = sessionFactory.getCurrentSession();

        Account account = new Account();
        account.setName("John Smith");
        account.setCashBalance(500.0);
        session.save(account);
        session.flush();
        session.clear();

        Query query = session.createQuery("from Account a where a.id=:id").setLong("id", account.getId());
        Account a = (Account) query.uniqueResult();
        assertThat(a).isNotNull();
        assertThat(a.isPersisted()).isTrue();
        a.setName("foo");
        session.saveOrUpdate(a);
        session.flush();
    }

}
