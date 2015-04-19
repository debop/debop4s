package debop4s.data.orm.jtests.mapping.associations.manytoone;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
@Transactional
public class ManyToOneTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    public void uniDirectionalManyToOne() {

        Jug jug = new Jug("JUG Summer Camp");

        JugMember emmanuel = new JugMember("Emmanuel Bernard");
        emmanuel.setMemberOf(jug);

        JugMember jerome = new JugMember("Jerome");
        jerome.setMemberOf(jug);

        em.persist(jug);
        em.persist(emmanuel);
        em.persist(jerome);
        em.flush();
        em.clear();

        emmanuel = em.find(JugMember.class, emmanuel.getId());
        assertThat(emmanuel).isNotNull();

        jug = emmanuel.getMemberOf();
        assertThat(jug).isNotNull();

        em.remove(emmanuel);

        jerome = em.find(JugMember.class, jerome.getId());

        assertThat(jerome).isNotNull();

        em.remove(jerome);
        em.remove(jug);
        em.flush();

        assertThat(em.find(JugMember.class, jerome.getId())).isNull();
    }

    @Test
    @Transactional
    public void testBidirectionalManyToOneRegular() throws Exception {

        SalesForce force = new SalesForce("Red Hat");
        em.persist(force);

        SalesGuy eric = new SalesGuy();
        eric.setName("Eric");
        eric.setSalesForce(force);
        force.getSalesGuys().add(eric);
        em.persist(eric);

        SalesGuy simon = new SalesGuy();
        simon.setName("Simon");
        simon.setSalesForce(force);
        force.getSalesGuys().add(simon);
        em.persist(simon);

        em.flush();
        em.clear();

        force = em.find(SalesForce.class, force.getId());
        assertThat(force.getSalesGuys()).isNotNull();
        assertThat(force.getSalesGuys().size()).isEqualTo(2);
        for (SalesGuy guy : force.getSalesGuys()) {
            assertThat(guy.getId()).isNotNull();
        }

        simon = em.find(SalesGuy.class, simon.getId());
        assertThat(simon).isNotNull();

        // Cascade 때문에
        force.getSalesGuys().remove(simon);
        em.remove(simon);
        em.persist(force);
        em.flush();
        em.clear();

        force = em.find(SalesForce.class, force.getId());
        assertThat(force.getSalesGuys()).isNotNull();
        assertThat(force.getSalesGuys().size()).isEqualTo(1);
        assertThat(force.getSalesGuys().contains(simon)).isFalse();
        assertThat(force.getSalesGuys().contains(eric)).isTrue();

        em.remove(force);
        em.flush();

        assertThat(em.find(SalesGuy.class, force.getSalesGuys().iterator().next().getId())).isNull();
    }

    @Test
    @Transactional
    public void testBiDirManyToOneInsertUpdateFalse() throws Exception {

        Brewery hoeBrewery = new Brewery();
        Beer hoegaarden = new Beer();
        hoeBrewery.getBeers().add(hoegaarden);
        hoegaarden.setBrewery(hoeBrewery);
        em.persist(hoeBrewery);
        em.flush();
        em.clear();

        hoegaarden = em.find(Beer.class, hoegaarden.getId());
        assertThat(hoegaarden).isNotNull();
        assertThat(hoegaarden.getBrewery()).isNotNull();
        assertThat(hoegaarden.getBrewery().getId()).isNotNull();
        assertThat(hoegaarden.getBrewery().getBeers()).hasSize(1).containsOnly(hoegaarden);

        Beer citron = new Beer();
        hoeBrewery = hoegaarden.getBrewery();
        hoeBrewery.getBeers().remove(hoegaarden);
        hoeBrewery.getBeers().add(citron);
        citron.setBrewery(hoeBrewery);
        em.remove(hoegaarden);
        em.flush();
        em.clear();

        citron = em.find(Beer.class, citron.getId());
        assertThat(citron).isNotNull();
        assertThat(citron.getBrewery().getBeers()).hasSize(1).containsOnly(citron);

        hoeBrewery = citron.getBrewery();
        citron.setBrewery(null);
        hoeBrewery.getBeers().clear();

        em.remove(citron);
        em.remove(hoeBrewery);
        em.flush();

        assertThat(em.find(Beer.class, citron.getId())).isNull();
    }
}
