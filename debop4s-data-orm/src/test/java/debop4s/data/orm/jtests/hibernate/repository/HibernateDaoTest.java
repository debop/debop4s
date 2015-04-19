package debop4s.data.orm.jtests.hibernate.repository;

import debop4s.data.orm.jtests.hibernate.HibernateTestBase;
import debop4s.data.orm.jtests.mapping.Employee;
import debop4s.data.orm.jtests.mapping.simple.SimpleEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
@Transactional
@SuppressWarnings("unchecked")
public class HibernateDaoTest extends HibernateTestBase {

    @Test
    @Transactional(readOnly = true)
    public void createHibernateRepository() {
        assertThat(dao).isNotNull();

        List<SimpleEntity> users = (List<SimpleEntity>) dao.findAll(SimpleEntity.class);
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    @Transactional(readOnly = true)
    public void createEmployeeHiberateRepository() {
        List<Employee> categories = (List<Employee>) dao.findAll(Employee.class);
        assertThat(categories.size()).isEqualTo(0);
    }

    @Test
    @Transactional(readOnly = true)
    public void loadSessionFactory() {
        Session session = sessionFactory.openSession();
        assertThat(session).isNotNull();
        List<SimpleEntity> events = (List<SimpleEntity>) session.createCriteria(SimpleEntity.class).list();
    }

    @Test
    @Transactional(readOnly = true)
    public void findAllTest() throws Exception {
        List<Employee> categories = (List<Employee>) dao.findAll(Employee.class);
        assertThat(categories).isNotNull();
        assertThat(categories.size()).isEqualTo(0);
    }

}
