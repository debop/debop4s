package debop4s.data.orm.jtests.hibernate.repository;

import debop4s.data.orm.jtests.hibernate.HibernateTestBase;
import debop4s.data.orm.jtests.mapping.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
public class HibernateRepositoryTest extends HibernateTestBase {

    @Autowired
    EmployeeRepository employeeRepository;

    @Test
    public void createHibernateRepository() {
        assertThat(sessionFactory).isNotNull();
        assertThat(dao).isNotNull();
        assertThat(employeeRepository).isNotNull();

        List<Employee> list = employeeRepository.findAll();
        assertThat(list).isNotNull();
    }
}
