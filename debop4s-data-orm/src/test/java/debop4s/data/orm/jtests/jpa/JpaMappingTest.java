package debop4s.data.orm.jtests.jpa;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManagerFactory;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaH2Config.class })
public class JpaMappingTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    public void mappingTest() {
        assertThat(emf).isNotNull();
    }
}
