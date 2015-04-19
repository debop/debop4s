package debop4s.data.orm.jtests.mapping.property;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * PropertyEntityTest
 * Created by debop on 2014. 1. 15..
 */
@Slf4j
@Transactional
public class PropertyEntityTest extends JpaTestBase {

    @PersistenceContext EntityManager em;

    @Test
    public void blob() throws Exception {
        PropertyEntity pe = new PropertyEntity();
        pe.setName("name");
        pe.setData("Long Long Data...");
        pe.setScore(14.407f);

        em.persist(pe);
        em.flush();
        em.clear();

        PropertyEntity loaded = em.find(PropertyEntity.class, pe.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(pe);
        assertThat(loaded.getScore()).isEqualTo(14.407f);

        loaded = em.find(PropertyEntity.class, pe.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(pe);
        assertThat(loaded.getScore()).isEqualTo(14.407f);

        loaded = em.find(PropertyEntity.class, pe.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(pe);
        assertThat(loaded.getScore()).isEqualTo(14.407f);

        em.remove(loaded);
        assertThat(em.find(PropertyEntity.class, pe.getId())).isNull();
    }
}
