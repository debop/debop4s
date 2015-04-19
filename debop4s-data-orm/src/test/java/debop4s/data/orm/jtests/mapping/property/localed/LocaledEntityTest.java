package debop4s.data.orm.jtests.mapping.property.localed;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.property.localed.LocaledEntityTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 5:58
 */
@Slf4j
@Transactional
public class LocaledEntityTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void localedEntity() {
        SampleLocaleEntity entity = new SampleLocaleEntity();
        entity.setTitle("제목");
        entity.setDescription("내용");
        entity.addLocaleValue(Locale.KOREAN, new SampleLocaleValue("제목", "내용"));
        entity.addLocaleValue(Locale.ENGLISH, new SampleLocaleValue("Title", "Body"));

        em.persist(entity);
        em.flush();
        em.clear();

        SampleLocaleEntity loaded = em.find(SampleLocaleEntity.class, entity.getId());
        assertThat(loaded.getLocaleMap().size()).isEqualTo(2);

        assertThat(loaded.getLocaleValue(Locale.KOREAN).getTitle()).isEqualTo("제목");
        assertThat(loaded.getLocaleValue(Locale.KOREAN).getDescription()).isEqualTo("내용");
        assertThat(loaded.getLocaleValue(Locale.ENGLISH).getTitle()).isEqualTo("Title");
        assertThat(loaded.getLocaleValue(Locale.ENGLISH).getDescription()).isEqualTo("Body");

        // 없는 Locale 에 대해서는 기본 Locale 정보를 제공합니다.
        assertThat(loaded.getLocaleValue(Locale.CHINESE).getTitle()).isEqualTo("제목");
        assertThat(loaded.getLocaleValue(Locale.CHINESE).getDescription()).isEqualTo("내용");

        // second cache test
        loaded = em.find(SampleLocaleEntity.class, entity.getId());
        assertThat(loaded.getLocaleMap().size()).isEqualTo(2);

        assertThat(loaded.getLocaleValue(Locale.KOREAN).getTitle()).isEqualTo("제목");
        assertThat(loaded.getLocaleValue(Locale.KOREAN).getDescription()).isEqualTo("내용");
        assertThat(loaded.getLocaleValue(Locale.ENGLISH).getTitle()).isEqualTo("Title");
        assertThat(loaded.getLocaleValue(Locale.ENGLISH).getDescription()).isEqualTo("Body");

        // 없는 Locale 에 대해서는 기본 Locale 정보를 제공합니다.
        assertThat(loaded.getLocaleValue(Locale.CHINESE).getTitle()).isEqualTo("제목");
        assertThat(loaded.getLocaleValue(Locale.CHINESE).getDescription()).isEqualTo("내용");

        em.remove(loaded);
        em.flush();

        assertThat(em.find(SampleLocaleEntity.class, entity.getId())).isNull();
    }
}
