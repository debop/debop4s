package debop4s.data.orm.jtests.hibernate;

import debop4s.data.orm.hibernate.repository.HibernateDao;
import debop4s.data.orm.hibernate.repository.HibernateQueryDslDao;
import debop4s.data.orm.jtests.AppConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfiguration.class, HibernateMySqlConfig.class })
public abstract class HibernateTestBase {

    @Autowired
    protected SessionFactory sessionFactory;

    @Autowired
    protected HibernateDao dao;

    @Autowired
    protected HibernateQueryDslDao queryDao;

}
