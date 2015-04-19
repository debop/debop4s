package debop4s.data.orm.jtests.hibernate.repository;

import debop4s.data.orm.hibernate.repository.HibernateQueryDslDao;
import debop4s.data.orm.jtests.hibernate.HibernateTestBase;
import debop4s.data.orm.jtests.mapping.QEmployee;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
public class QueryDslDaoTest extends HibernateTestBase {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    HibernateQueryDslDao dao;

    @Test
    @Transactional(readOnly = true)
    public void retrieveEmployee() {
        QEmployee employee = QEmployee.employee;
        dao.from(employee)
           .where(employee.empNo.startsWith("A"))
           .offset(0)
           .limit(10)
           .list(employee);
    }

}
