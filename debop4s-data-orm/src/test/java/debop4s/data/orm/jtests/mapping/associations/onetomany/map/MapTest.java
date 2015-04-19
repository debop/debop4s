package debop4s.data.orm.jtests.mapping.associations.onetomany.map;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
@Transactional
public class MapTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void mapTest() {
        Car car = new Car();

        CarOption option1 = new CarOption("option1", 1);
        CarOption option2 = new CarOption("option2", 1);

        car.getCarOptions().put("option1", option1);
        car.getCarOptions().put("option2", option2);

        car.getOptions().put("stringOption1", "Value1");
        car.getOptions().put("stringOption2", "Value2");

        em.persist(car);
        em.flush();
        em.clear(); // test 시에만 씁니다.

        Car loaded = em.find(Car.class, car.getId());

        assertThat(loaded).isNotNull();
        assertThat(loaded.getCarOptions().size()).isEqualTo(2);
        assertThat(loaded.getOptions()).hasSize(2);

        em.remove(loaded);
        em.flush();
        assertThat(em.find(Car.class, car.getId())).isNull();
    }
}
