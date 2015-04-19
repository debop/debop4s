package debop4s.data.orm.jtests.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateMySqlConfig.class })
public class HibernateMappingTest {

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void mappingTest() {
        assertThat(sessionFactory).isNotNull();
    }
}
