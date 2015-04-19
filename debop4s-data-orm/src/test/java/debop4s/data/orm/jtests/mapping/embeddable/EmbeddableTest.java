package debop4s.data.orm.jtests.mapping.embeddable;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.embeddable.EmbeddableJUnitSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 5:23
 */
@Slf4j
@Transactional
public class EmbeddableTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void embeddableAddressTest() {
        EmbeddableUser user = new EmbeddableUser();

        user.setUsername("배성혁");

        user.getHomeAddress().setCity("서울");
        user.getHomeAddress().setStreet("정릉로");
        user.getHomeAddress().setZipcode("100-100");

        user.getOfficeAddress().setCity("서울");
        user.getOfficeAddress().setStreet("안국로");
        user.getOfficeAddress().setZipcode("200-200");

        em.persist(user);
        em.flush();
        em.clear();

        EmbeddableUser loaded = em.find(EmbeddableUser.class, user.getId());

        assertThat(loaded).isNotNull();
        assertThat(loaded.getHomeAddress().getZipcode()).isEqualTo(user.getHomeAddress().getZipcode());
        assertThat(loaded.getOfficeAddress().getZipcode()).isEqualTo(user.getOfficeAddress().getZipcode());

        em.remove(loaded);
        em.flush();

        assertThat(em.find(EmbeddableUser.class, user.getId())).isNull();
    }
}
