package debop4s.data.orm.jtests.mapping.embeddable;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import debop4s.data.orm.model.MinMaxNumber;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * MinMaxNumberTest
 *
 * @author sunghyouk.bae@gmail.com
 */
@Slf4j
@Transactional
public class MinMaxNumberTest extends JpaTestBase {

    @PersistenceContext EntityManager em;

    @Test
    public void crud() {
        MinMaxNumberEntity entity = new MinMaxNumberEntity();
        entity.setIntMinMax(new MinMaxNumber<Integer>(0, 100));
        entity.setLongMinMax(new MinMaxNumber<Long>(10000L, 20000L));
        entity.setFloatMinMax(new MinMaxNumber<Float>(0.5f, 10.5f));
        entity.setDoubleMinMax(new MinMaxNumber<Double>(0.1, 11.1));

        em.persist(entity);
        em.flush();
        em.clear();

        MinMaxNumberEntity loaded = em.find(MinMaxNumberEntity.class, entity.getId());

        log.debug("loaded={}", loaded);

        assertThat(loaded).isEqualTo(entity);
        assertThat(loaded.getIntMinMax()).isEqualTo(entity.getIntMinMax());
        assertThat(loaded.getLongMinMax()).isEqualTo(entity.getLongMinMax());
        assertThat(loaded.getLongMinMax()).isEqualTo(entity.getLongMinMax());
        assertThat(loaded.getLongMinMax()).isEqualTo(entity.getLongMinMax());

        em.remove(loaded);
        em.flush();
        assertThat(em.find(MinMaxNumberEntity.class, entity.getId())).isNull();
    }
}
