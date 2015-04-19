package debop4s.data.orm.jtests.mapping.associations.unidirectional;

import debop4s.data.orm.jtests.hibernate.HibernateTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * debop4s.data.orm.s.mapping.associations.unidirectional.CollectionUnidirectionalTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 7. 오후 9:11
 */
@Slf4j
@Transactional
public class CollectionUnidirectionalTest extends HibernateTestBase {

    @Test
    public void unidirectionalCollection() throws Exception {

        SnowFlake sf = new SnowFlake();
        sf.setDescription("Snowflake 1");
        dao.save(sf);

        SnowFlake sf2 = new SnowFlake();
        sf2.setDescription("Snowflake 2");
        dao.save(sf2);

        Cloud cloud = new Cloud();
        cloud.setLength(23.0);
        cloud.getProducedSnowFlakes().add(sf);
        cloud.getProducedSnowFlakes().add(sf2);
        dao.persist(cloud);
        dao.flush();

        dao.getSession().clear();

        cloud = (Cloud) dao.get(Cloud.class, cloud.getId());
        assertNotNull(cloud.getProducedSnowFlakes());
        assertEquals(2, cloud.getProducedSnowFlakes().size());

        final SnowFlake removedSf = cloud.getProducedSnowFlakes().iterator().next();
        SnowFlake sf3 = new SnowFlake();
        sf3.setDescription("Snowflake 3");
        dao.persist(sf3);

        cloud.getProducedSnowFlakes().remove(removedSf);
        cloud.getProducedSnowFlakes().add(sf3);

        dao.flush();
        dao.getSession().clear();

        cloud = (Cloud) dao.get(Cloud.class, cloud.getId());
        assertNotNull(cloud.getProducedSnowFlakes());
        assertEquals(2, cloud.getProducedSnowFlakes().size());
        boolean present = false;
        for (SnowFlake current : cloud.getProducedSnowFlakes()) {
            if (current.getDescription().equals(removedSf.getDescription())) {
                present = true;
            }
        }
        assertFalse("flake not removed", present);
        for (SnowFlake current : cloud.getProducedSnowFlakes()) {
            dao.delete(current);
        }
        dao.delete(dao.load(SnowFlake.class, removedSf.getId()));
        cloud.getProducedSnowFlakes().clear();
        dao.flush();
        dao.getSession().clear();

        cloud = (Cloud) dao.get(Cloud.class, cloud.getId());
        assertNotNull(cloud.getProducedSnowFlakes());
        assertEquals(0, cloud.getProducedSnowFlakes().size());
        dao.delete(cloud);
        dao.flush();
    }
}
