package debop4s.data.orm.jtests.mapping.queries;

import debop4s.data.orm.jtests.hibernate.HibernateTestBase;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.queries.SimpleQueriesTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 8. 오전 12:07
 */
@Slf4j
@Transactional
public class SimpleQueriesTest extends HibernateTestBase {

    @Test
    public void simpleQueries() throws Exception {

        String hypothesiFullName = Hypothesis.class.getName();

        Session session = dao.getSession();
        assertQuery(session, 4, session.createQuery("from Hypothesis"));
        assertQuery(session, 4, session.createQuery("from " + hypothesiFullName));
        assertQuery(session, 1, session.createQuery("from Helicopter"));
        // assertQuery(session, 5, session.createQuery("from java.lang.Object"));
    }

    @Test
    public void testConstantParameterQueries() throws Exception {
        final Session session = dao.getSession();

        assertQuery(session, 1, session.createQuery("from Hypothesis h where h.description = 'stuff works'"));
    }

    @Test
    public void testParametricQueries() throws Exception {
        final Session session = dao.getSession();

        Query query = session
                .createQuery("from Hypothesis h where h.description = :myParam")
                .setString("myParam", "stuff works");
        assertQuery(session, 1, query);
    }

    private void assertQuery(final Session session, final int expectedSize, final Query testedQuery) {
        assertThat(testedQuery.list()).hasSize(expectedSize).as("Query failed");
        session.clear();
    }

    @Before
    public void setUp() throws Exception {

        log.info("예제용 데이터 추가");

        Hypothesis socrates = new Hypothesis();
        socrates.setId("13");
        socrates.setDescription("There are more than two dimensions over the shadows we see out of the cave");
        socrates.setPosition(1);
        dao.saveOrUpdate(socrates);

        Hypothesis peano = new Hypothesis();
        peano.setId("14");
        peano.setDescription("Peano's curve and then Hilbert's space filling curve proof the connection from mono-dimensional to bi-dimensional space");
        peano.setPosition(2);
        dao.saveOrUpdate(peano);

        Hypothesis sanne = new Hypothesis();
        sanne.setId("15");
        sanne.setDescription("Hilbert's proof of connection to 2 dimensions can be induced to reason on N dimensions");
        sanne.setPosition(3);
        dao.saveOrUpdate(sanne);

        Hypothesis shortOne = new Hypothesis();
        shortOne.setId("16");
        shortOne.setDescription("stuff works");
        shortOne.setPosition(4);
        dao.saveOrUpdate(shortOne);

        Helicopter helicopter = new Helicopter();
        helicopter.setName("No creative clue ");
        dao.saveOrUpdate(helicopter);

        dao.flush();
    }

}
