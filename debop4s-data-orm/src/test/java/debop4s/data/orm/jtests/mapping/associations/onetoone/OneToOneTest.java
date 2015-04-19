package debop4s.data.orm.jtests.mapping.associations.onetoone;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneJUnitSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 7. 오후 11:21
 */
@Slf4j
@Transactional
public class OneToOneTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void authorBiography() throws Exception {
        OneToOneAuthor author = new OneToOneAuthor();
        author.setName("debop");

        author.getBiography().setInformation("Sunghyouk Bae");
        author.getPicture().setPicturePath("file://a/b/c");

        em.persist(author);
        em.flush();
        em.clear();

        log.debug("load biography");
        OneToOneBiography biography = em.find(OneToOneBiography.class, author.getId());
        assertThat(biography).isNotNull();
        assertThat(biography.getId()).isNotNull();

        log.debug("retrieve biography.author.id");
        assertThat(biography.getAuthor().getId()).isNotNull();

        log.debug("load author");
        author = em.find(OneToOneAuthor.class, author.getId());
        assertThat(author).isNotNull();

        log.debug("load biography");
        OneToOneBiography bio = author.getBiography();
        assertThat(bio).isNotNull();
        assertThat(bio.getInformation()).isEqualToIgnoringCase("Sunghyouk Bae");

        em.remove(author);
        em.flush();

        assertThat(em.find(OneToOneAuthor.class, author.getId())).isNull();
    }

    @Test
    public void unidirectionalManyToOne() throws Exception {

        Horse horse = new Horse();
        horse.setName("Palefrenier");
        horse.setWeight(250.07f);

        Cavalier cavalier = new Cavalier();
        cavalier.setName("Caroline");
        cavalier.setHorse(horse);
        horse.setWeight(77.07f);

        em.persist(horse);
        em.persist(cavalier);
        em.flush();
        em.clear();

        cavalier = em.find(Cavalier.class, cavalier.getId());
        assertThat(cavalier).isNotNull();

        horse = cavalier.getHorse();
        assertThat(horse).isNotNull();

        cavalier = em.find(Cavalier.class, cavalier.getId());
        assertThat(cavalier).isNotNull();

        Horse horse2 = cavalier.getHorse();
        assertThat(horse2).isNotNull();
        assertThat(horse2.getWeight()).isEqualTo(horse.getWeight());

        em.remove(cavalier);
        em.remove(horse);
        em.flush();

        assertThat(em.find(Cavalier.class, cavalier.getId())).isNull();
    }

    @Test
    public void unidirectionalOneToOne() throws Exception {

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Mercedes");

        Wheel wheel = new Wheel();
        wheel.setVehicle(vehicle);

        em.persist(vehicle);
        em.persist(wheel);
        em.flush();
        em.clear();

        log.debug("Weel id=[{}]", wheel.getId());

        wheel = em.find(Wheel.class, wheel.getId());
        assertThat(wheel).isNotNull();

        vehicle = wheel.getVehicle();
        assertThat(vehicle).isNotNull();

        em.remove(wheel);
        em.remove(vehicle);
        em.flush();

        assertThat(em.find(Wheel.class, wheel.getId())).isNull();
    }

    @Test
    public void bidirectionalManyToOne() throws Exception {

        Husband husband = new Husband();
        husband.setName("Alex");

        Wife wife = new Wife();
        wife.setName("Bea");

        husband.setWife(wife);
        wife.setHusband(husband);
        em.persist(husband);
        em.persist(wife);
        em.flush();
        em.clear();

        log.debug("load husband");
        husband = em.find(Husband.class, husband.getId());
        assertThat(husband).isNotNull();
        assertThat(husband.getWife()).isNotNull();
        em.clear();

        log.debug("load wife");
        wife = em.find(Wife.class, wife.getId());
        assertThat(wife).isNotNull();

        husband = wife.getHusband();
        assertThat(husband).isNotNull();

        Wife bea2 = new Wife();
        em.persist(bea2);
        bea2.setName("Still Bea");

        husband.setWife(bea2);
        wife.setHusband(null);
        bea2.setHusband(husband);

        em.flush();
        em.clear();


        husband = em.find(Husband.class, husband.getId());
        assertThat(husband).isNotNull();
        assertThat(husband.getWife()).isNotNull();
        assertThat(husband.getWife().getHusband()).isEqualTo(husband);

        em.clear();

        wife = em.find(Wife.class, wife.getId());
        assertThat(wife).isNotNull();
        assertThat(wife.getHusband()).isNull();
        em.remove(wife);

        bea2 = em.find(Wife.class, bea2.getId());
        assertThat(bea2).isNotNull();

        husband = bea2.getHusband();
        assertThat(husband).isNotNull();

        bea2.setHusband(null);
        husband.setWife(null);
        em.remove(husband);
        em.remove(wife);
        em.flush();

        assertThat(em.find(Wife.class, wife.getId())).isNull();
        assertThat(em.find(Husband.class, husband.getId())).isNull();
    }

}
