package debop4s.data.orm.jtests.jpa.repository;

import com.mysema.query.jpa.impl.JPAQuery;
import debop4s.data.orm.jpa.repository.JpaQueryDslDao;
import debop4s.data.orm.jtests.jpa.JpaTestBase;
import debop4s.data.orm.jtests.mapping.Employee;
import debop4s.data.orm.jtests.mapping.QEmployee;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * QueryDSL 을 이용한 JPA Repository 구현 예제 테스트
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 21. 오후 3:08
 */
@Slf4j
public class JpaQueryDslRepositoryTest extends JpaTestBase {

    @Autowired
    EmployeeRepository employeeRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    JpaQueryDslDao dao;

    @Test
    public void findAllTest() {
        Employee emp = new Employee();
        emp.setName("Sunghyouk Bae");
        emp.setEmpNo("21011");
        emp = employeeRepository.saveAndFlush(emp);

        QEmployee $ = QEmployee.employee;
        JPAQuery query = new JPAQuery(em);

        Employee loaded = query.from($)
                               .where($.empNo.eq("21011"))
                               .uniqueResult($);

        assertThat(loaded).isNotNull();
        assertThat(loaded.getEmpNo()).isEqualToIgnoringCase(emp.getEmpNo());
        assertThat(loaded).isEqualTo(emp);
    }

    @Test
    public void retriveEmployee() {
        QEmployee $ = QEmployee.employee;

        List<Employee> results = dao.from($).where($.empNo.startsWith("A"))
                                    .offset(0)
                                    .limit(10)
                                    .list($);

        assertThat(results).isNotNull();

    }
}
