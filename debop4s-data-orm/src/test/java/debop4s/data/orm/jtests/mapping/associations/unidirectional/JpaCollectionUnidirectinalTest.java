package debop4s.data.orm.jtests.mapping.associations.unidirectional;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;


/**
 * debop4s.data.orm.s.mapping.associations.unidirectional.JpaCollectionUnidirectinalTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 3:07
 */
@Transactional
public class JpaCollectionUnidirectinalTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void unidirectionalCollection() throws Exception {

        SnowFlake sf = new SnowFlake();
        sf.setDescription("Snowflake 1");
        em.persist(sf);

        SnowFlake sf2 = new SnowFlake();
        sf2.setDescription("Snowflake 2");
        em.persist(sf2);

        Cloud cloud = new Cloud();
        cloud.setLength(23.0);
        cloud.getProducedSnowFlakes().add(sf);
        cloud.getProducedSnowFlakes().add(sf2);
        em.persist(cloud);
        em.flush();

        em.clear();

        cloud = (Cloud) em.find(Cloud.class, cloud.getId());
        assertThat(cloud.getProducedSnowFlakes()).isNotNull();
        assertThat(cloud.getProducedSnowFlakes()).hasSize(2);

        final SnowFlake removedSf = cloud.getProducedSnowFlakes().iterator().next();
        SnowFlake sf3 = new SnowFlake();
        sf3.setDescription("Snowflake 3");
        em.persist(sf3);

        cloud.getProducedSnowFlakes().remove(removedSf);
        cloud.getProducedSnowFlakes().add(sf3);

        em.flush();
        em.clear();

        cloud = (Cloud) em.find(Cloud.class, cloud.getId());
        assertThat(cloud.getProducedSnowFlakes()).isNotNull();
        assertThat(cloud.getProducedSnowFlakes()).hasSize(2);

        boolean present = false;
        for (SnowFlake current : cloud.getProducedSnowFlakes()) {
            if (current.getDescription().equals(removedSf.getDescription())) {
                present = true;
            }
        }
        assertThat(present).isFalse();

        for (SnowFlake current : cloud.getProducedSnowFlakes()) {
            em.remove(current);
        }
        em.remove(em.find(SnowFlake.class, removedSf.getId()));
        cloud.getProducedSnowFlakes().clear();
        em.flush();
        em.clear();

        cloud = (Cloud) em.find(Cloud.class, cloud.getId());
        assertThat(cloud.getProducedSnowFlakes()).isNotNull();
        assertThat(cloud.getProducedSnowFlakes()).hasSize(0);

        em.remove(cloud);
        em.flush();

        assertThat(em.find(Cloud.class, cloud.getId())).isNull();
    }
}
