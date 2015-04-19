package debop4s.data.orm.jtests;

import debop4s.data.orm.hibernate.repository.HibernateDao;
import debop4s.data.orm.jtests.hibernate.repository.EmployeeRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackageClasses = { EmployeeRepository.class, HibernateDao.class })
public class AppConfiguration {
}
