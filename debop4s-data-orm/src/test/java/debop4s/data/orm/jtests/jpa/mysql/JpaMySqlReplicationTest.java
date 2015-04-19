package debop4s.data.orm.jtests.jpa.mysql;

import debop4s.data.orm.jtests.mapping.simple.SimpleEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaMySqlReplicationConfig.class })
public class JpaMySqlReplicationTest {

    @Autowired SimpleEntityService simpleEntityJpaService;
    @Autowired SimpleEntityRepository repo;

    @Test
    public void testReadOnlyWork() {
        log.trace("start readonly work...");

        List<SimpleEntity> entities = simpleEntityJpaService.findAll();
        assertThat(entities).isNotNull();

        log.trace("finish readonly work!!!");
    }

    @Test
    public void testReadWriteWork() {
        final SimpleEntity entity = new SimpleEntity();
        entity.setName("readwrite");

        simpleEntityJpaService.save(entity);

        // JPQL 로 로드하기
        SimpleEntity loadByJpql = simpleEntityJpaService.loadByJPQL(entity.getId());

        log.debug("loadByJpql={}", loadByJpql);
        assertThat(loadByJpql).isNotNull();
        assertThat(loadByJpql.getId()).isNotNull();
        assertThat(loadByJpql.getId()).isEqualTo(entity.getId());
        assertThat(loadByJpql).isEqualTo(entity);

        SimpleEntity loaded = simpleEntityJpaService.load(entity.getId());

        log.debug("loaded={}", loaded);
        assertThat(loaded).isNotNull();
        assertThat(loaded.getId()).isNotNull();
        assertThat(loaded.getId()).isEqualTo(entity.getId());
        assertThat(loaded).isEqualTo(entity);
    }

    @Test
    public void testReadWriteWorkWithJpaDao() {
        final SimpleEntity entity = new SimpleEntity();
        entity.setName("readwrite");

        repo.save(entity);

        // JPQL 로 로드하기
        SimpleEntity loadByJpql = repo.findById(entity.getId());

        log.debug("loadByJpql={}", loadByJpql);
        assertThat(loadByJpql).isNotNull();
        assertThat(loadByJpql.getId()).isNotNull();
        assertThat(loadByJpql.getId()).isEqualTo(entity.getId());
        assertThat(loadByJpql).isEqualTo(entity);

        // JpaRepository#findOne 으로 로드하기
        SimpleEntity loaded = repo.findOne(entity.getId());
        log.debug("loaded={}", loaded);
        assertThat(loaded).isNotNull();
        assertThat(loaded.getId()).isNotNull();
        assertThat(loaded.getId()).isEqualTo(entity.getId());
        assertThat(loaded).isEqualTo(entity);
    }

}
