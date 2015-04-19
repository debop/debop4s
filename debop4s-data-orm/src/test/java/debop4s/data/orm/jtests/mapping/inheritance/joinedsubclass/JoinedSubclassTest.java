package debop4s.data.orm.jtests.mapping.inheritance.joinedsubclass;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.inheritance.joinedsubclass.JoinedSubclassJUnitSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 5:34
 */
@Slf4j
@Transactional
public class JoinedSubclassTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void personTest() {
        JoinedSubclassEmployee employee = new JoinedSubclassEmployee();
        employee.setEmpNo("21011");
        employee.setName("배성혁");
        employee.setRegidentNo("111111-11111111");

        JoinedSubclassCustomer customer = new JoinedSubclassCustomer();
        customer.setName("customer1");
        customer.setContactEmployee(employee);
        customer.setRegidentNo("222222-22222222");

        em.persist(employee);
        em.persist(customer);
        em.flush();

        em.clear();

        JoinedSubclassCustomer customer1 = em.find(JoinedSubclassCustomer.class, customer.getId());
        assertThat(customer1).isEqualTo(customer);
        assertThat(customer1.getRegidentNo()).isEqualTo(customer.getRegidentNo());

        JoinedSubclassEmployee employee1 = em.find(JoinedSubclassEmployee.class, employee.getId());
        assertThat(employee1).isEqualTo(employee);
        assertThat(employee1.getRegidentNo()).isEqualTo(employee.getRegidentNo());

        customer1 = em.find(JoinedSubclassCustomer.class, customer.getId());
        assertThat(customer1).isEqualTo(customer);
        assertThat(customer1.getRegidentNo()).isEqualTo(customer.getRegidentNo());

        employee1 = em.find(JoinedSubclassEmployee.class, employee.getId());
        assertThat(employee1).isEqualTo(employee);
        assertThat(employee1.getRegidentNo()).isEqualTo(employee.getRegidentNo());

        em.remove(customer1);
        em.remove(employee1);
        em.flush();

        assertThat(em.find(JoinedSubclassCustomer.class, customer.getId())).isNull();
        assertThat(em.find(JoinedSubclassEmployee.class, employee.getId())).isNull();
    }
}
