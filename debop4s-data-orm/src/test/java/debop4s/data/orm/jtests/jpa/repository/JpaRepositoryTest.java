package debop4s.data.orm.jtests.jpa.repository;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import debop4s.data.orm.jtests.mapping.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class JpaRepositoryTest extends JpaTestBase {

    @Autowired
    EmployeeRepository employeeRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void injectEmployeeRepository() {
        assertThat(employeeRepository).isNotNull();
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees).isNotNull();
    }

    @Test
    public void employeeFindByEmpNo() {
        employeeRepository.deleteAll();
        employeeRepository.flush();

        Employee emp = new Employee();
        emp.setName("Sunghyouk Bae");
        emp.setEmpNo("21011");
        emp = employeeRepository.saveAndFlush(emp);
        em.clear();

        Employee loaded = employeeRepository.findByEmpNo(emp.getEmpNo());
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(emp);
        assertThat(loaded.getUpdatedTimestamp()).isNotNull();
        log.debug("ScalaEmployee=[{}]", loaded);

        loaded = employeeRepository.findByEmpNo(emp.getEmpNo());
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(emp);
        assertThat(loaded.getUpdatedTimestamp()).isNotNull();
        log.debug("ScalaEmployee=[{}]", loaded);
    }
}
