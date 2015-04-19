package debop4s.data.orm.jtests.hibernate.repository;

import debop4s.data.orm.hibernate.repository.HibernateRepository;
import debop4s.data.orm.jtests.mapping.Employee;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public interface EmployeeRepository extends HibernateRepository<Employee> {

    Employee findByEmpNo(String empNo);
}
