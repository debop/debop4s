package debop4s.data.orm.jtests.mapping.property.enumerated;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.property.enumerated.EnumeratedEntityTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 6:07
 */
@Slf4j
@Transactional
public class EnumeratedEntityTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    @Rollback(false)
    public void enumeratedTest() {
        EnumeratedEntity entity = new EnumeratedEntity();
        entity.setIntValue(OrdianlEnum.Second);
        entity.setStringValue(StringEnum.Integer);
        entity.setAgeType(AgeType.A40);

        em.persist(entity);
        em.flush();
        em.clear();

        EnumeratedEntity loaded = em.find(EnumeratedEntity.class, entity.getId());
        assertThat(loaded).isEqualTo(entity);

        // load from 2nd cache
        loaded = em.find(EnumeratedEntity.class, entity.getId());
        assertThat(loaded).isEqualTo(entity);

        //em.remove(loaded);
        //assertThat(em.find(EnumeratedEntity.class, entity.getId())).isNull();
    }
}
