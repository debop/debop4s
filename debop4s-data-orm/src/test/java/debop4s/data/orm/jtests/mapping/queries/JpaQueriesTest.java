package debop4s.data.orm.jtests.mapping.queries;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.queries.JpaQueriesTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 6:13
 */
@Slf4j
@Transactional
public class JpaQueriesTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void simpleQueries() throws Exception {

        String hypothesisName = Hypothesis.class.getName();

        assertQuery(em, 4, em.createQuery("select h from Hypothesis h"));
        assertQuery(em, 4, em.createQuery("select h from " + hypothesisName + " h"));
        assertQuery(em, 1, em.createQuery("select h from Helicopter h"));
        // assertQuery(em, 5, em.createQuery("select o from java.lang.Object o"));
    }

    @Test
    public void testConstantParameterQueries() throws Exception {
        assertQuery(em, 1, em.createQuery("select h from Hypothesis h where h.description = 'stuff works'"));
    }

    @Test
    public void testParametricQueries() throws Exception {
        Query query = em
                .createQuery("select h from Hypothesis h where h.description = :myParam")
                .setParameter("myParam", "stuff works");
        assertQuery(em, 1, query);
    }

    private void assertQuery(final EntityManager em, final int expectedSize, final Query testedQuery) {
        assertThat(testedQuery.getResultList()).as("Query failed").hasSize(expectedSize);
        em.clear();
    }

    @Before
    public void setUp() throws Exception {

        log.info("예제용 데이터 추가");

        em.createQuery("delete from Hypothesis").executeUpdate();
        em.createQuery("delete from Helicopter").executeUpdate();
        em.flush();

        Hypothesis socrates = new Hypothesis();
        socrates.setId("13");
        socrates.setDescription("There are more than two dimensions over the shadows we see out of the cave");
        socrates.setPosition(1);
        em.persist(socrates);

        Hypothesis peano = new Hypothesis();
        peano.setId("14");
        peano.setDescription("Peano's curve and then Hilbert's space filling curve proof the connection from mono-dimensional to bi-dimensional space");
        peano.setPosition(2);
        em.persist(peano);

        Hypothesis sanne = new Hypothesis();
        sanne.setId("15");
        sanne.setDescription("Hilbert's proof of connection to 2 dimensions can be induced to reason on N dimensions");
        sanne.setPosition(3);
        em.persist(sanne);

        Hypothesis shortOne = new Hypothesis();
        shortOne.setId("16");
        shortOne.setDescription("stuff works");
        shortOne.setPosition(4);
        em.persist(shortOne);

        Helicopter helicopter = new Helicopter();
        helicopter.setName("No creative clue ");
        em.persist(helicopter);

        em.flush();
        em.clear();
    }
}
