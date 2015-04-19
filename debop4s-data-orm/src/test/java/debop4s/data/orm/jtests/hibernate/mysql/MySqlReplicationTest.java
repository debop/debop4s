package debop4s.data.orm.jtests.hibernate.mysql;

import debop4s.data.orm.ReadOnlyConnection;
import debop4s.data.orm.hibernate.repository.HibernateQueryDslDao;
import debop4s.data.orm.jtests.mapping.simple.SimpleEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MySqlReplicationConfig.class })
public class MySqlReplicationTest {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    HibernateQueryDslDao dao;

    @Autowired
    SimpleEntityService simpleEntityService;

    @Test
    @Transactional(readOnly = true)
    @ReadOnlyConnection
    public void readOnlyWork() {
        log.trace("start readonly work...");

        List<SimpleEntity> entities = simpleEntityService.getAll();
        assertThat(entities).isNotNull();

        log.trace("finish readonly work!!!");
    }

    @Test
    @Transactional
    public void readAndWriteWork() {

        log.trace("save new entity");
        SimpleEntity entity = new SimpleEntity();
        entity.setName("new entity");
        simpleEntityService.save(entity);

        log.trace("load entities");
        List<SimpleEntity> entities = simpleEntityService.getAll();
        assertThat(entities).isNotNull();

        log.trace("save loaded entity");
        entity.setDescription("updated entity");
        simpleEntityService.save(entity);
    }
}
