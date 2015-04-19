package debop4s.data.orm.jtests.hibernate.repository;

import com.mysema.query.jpa.hibernate.HibernateQuery;
import debop4s.data.orm.hibernate.repository.HibernateRepositoryImpl;
import debop4s.data.orm.jtests.mapping.Employee;
import debop4s.data.orm.jtests.mapping.QEmployee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository(value = "employeeRepository")
@Transactional(readOnly = true)
public class EmployeeRepositoryImpl extends HibernateRepositoryImpl<Employee> implements EmployeeRepository {

    public EmployeeRepositoryImpl() {
        super(Employee.class);
    }

    public Employee findByEmpNo(String empNo) {
        QEmployee employee = QEmployee.employee;
        HibernateQuery query = new HibernateQuery(getSession());

        return query.from(employee)
                    .where(employee.empNo.eq(empNo))
                    .uniqueResult(employee);
    }

}
