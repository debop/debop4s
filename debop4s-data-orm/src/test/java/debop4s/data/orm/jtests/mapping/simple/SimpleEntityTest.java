package debop4s.data.orm.jtests.mapping.simple;

import debop4s.core.io.Serializers;
import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.simple.SimpleEntityJUnitSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 11:25
 */
@Slf4j
@Transactional
public class SimpleEntityTest extends JpaTestBase {

    @PersistenceContext EntityManager em;

    @Test
    public void lifecycleTest() {

        LifecycleEntity entity = new LifecycleEntity();
        entity.setName("이름");
        em.persist(entity);
        em.flush();

        em.detach(entity);
        entity.setName("갱신");
        em.merge(entity);
        em.flush();
        em.clear();

        LifecycleEntity loaded = em.find(LifecycleEntity.class, entity.getId());
        assertThat(loaded).isNotNull();

        assertThat(loaded.getCreatedAt()).isNotNull();
        assertThat(loaded.getUpdatedAt()).isNotNull();

        em.remove(loaded);
        em.flush();

        assertThat(em.find(LifecycleEntity.class, entity.getId())).isNull();
    }

    @Test
    public void transientObjectTest() {
        SimpleEntity transientObj = new SimpleEntity();
        transientObj.setName("transient");

        SimpleEntity transientObj2 = Serializers.copyObject(transientObj);

        transientObj2.setDescription("desc");
        assertThat(transientObj2).isEqualTo(transientObj);

        SimpleEntity savedObj = Serializers.copyObject(transientObj);
        em.persist(savedObj);
        em.flush();
        em.clear();

        // Id를 발급받은 Persistent Object 와 Transient Object 와의 비교. hashCode에서
        assertThat(savedObj).isNotEqualTo(transientObj);

        SimpleEntity loaded = em.find(SimpleEntity.class, savedObj.getId());

        assertThat(loaded).isNotNull();
        // Persistent Object 간의 비교
        assertThat(loaded).isEqualTo(savedObj);

        // Persistent Object 와 Transient Object 간의 비교
        assertThat(loaded).isNotEqualTo(transientObj);

        SimpleEntity savedObj2 = Serializers.copyObject(transientObj);
        em.persist(savedObj2);
        em.flush();
        em.clear();

        SimpleEntity loaded2 = em.find(SimpleEntity.class, savedObj2.getId());
        assertThat(loaded2).isNotNull();
        assertThat(loaded2).isEqualTo(savedObj2);
        assertThat(loaded2).isNotEqualTo(transientObj);
        assertThat(loaded2).isNotEqualTo(loaded);

        em.remove(loaded2);
        em.flush();

        assertThat(em.find(SimpleEntity.class, savedObj2.getId())).isNull();
    }
}
