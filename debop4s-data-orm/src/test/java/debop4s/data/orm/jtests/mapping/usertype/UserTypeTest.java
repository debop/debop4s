package debop4s.data.orm.jtests.mapping.usertype;

import debop4s.core.utils.Strings;
import debop4s.data.orm.jtests.jpa.JpaTestBase;
import debop4s.data.orm.jtests.mapping.Employee;
import debop4s.data.orm.model.DateTimeRange;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
@Transactional
public class UserTypeTest extends JpaTestBase {

    @PersistenceContext EntityManager em;

    private static final String PLAIN_TEXT = "동해물과 백두산이 마르고 닳도록 ";

    @Test
    public void compressedDataEntityTest() {
        CompressedDataEntity entity = new CompressedDataEntity();
        final String text = Strings.replicate(PLAIN_TEXT, 100);
        entity.setStringData(text);
        entity.setBinaryData(Strings.getUtf8Bytes(text));

        em.persist(entity);
        em.flush();
        em.clear();

        CompressedDataEntity loaded = em.find(CompressedDataEntity.class, entity.getId());
        assertThat(loaded.getStringData()).isEqualTo(text);
        assertThat(Strings.getUtf8String(loaded.getBinaryData())).isEqualTo(text);

        em.remove(loaded);
        em.flush();
        assertThat(em.find(CompressedDataEntity.class, entity.getId())).isNull();
    }

    @Test
    public void jodaDateTimeUserTypeTest() {
        JodaDateTimeEntity entity = new JodaDateTimeEntity();

        entity.setStart(DateTime.now().withTimeAtStartOfDay());
        entity.setEnd(entity.getStart().plusDays(1));

        entity.setRange1(new DateTimeRange(entity.getStart(), entity.getEnd()));
        entity.setRange2(new DateTimeRange(entity.getStart().plusDays(1), entity.getEnd().plusDays(1)));

        entity.setStartTZ(DateTime.now());
        entity.setEndTZ(DateTime.now().plusDays(1));

        em.persist(entity);
        em.flush();

        em.clear();

        JodaDateTimeEntity loaded = em.find(JodaDateTimeEntity.class, entity.getId());

        assertThat(loaded).isEqualTo(entity);
        assertThat(loaded.getStart()).isEqualTo(entity.getStart());
        assertThat(loaded.getEnd()).isEqualTo(entity.getEnd());
        assertThat(loaded.getRange1()).isEqualTo(entity.getRange1());
        assertThat(loaded.getRange2()).isEqualTo(entity.getRange2());
        assertThat(loaded.getStartTZ().getSecondOfDay()).isEqualTo(entity.getStartTZ().getSecondOfDay());
        assertThat(loaded.getEndTZ().getSecondOfDay()).isEqualTo(entity.getEndTZ().getSecondOfDay());

        em.remove(loaded);
        em.flush();
        assertThat(em.find(JodaDateTimeEntity.class, entity.getId())).isNull();
    }

    @Test
    public void jsonUserTypeTest() {

        Employee emp = new Employee();
        emp.setName("Sunghyouk Bae");
        emp.setEmpNo("20111");

        em.persist(emp);

        JsonEntity entity = new JsonEntity();
        entity.setEmployee(emp);

        em.persist(entity);
        em.flush();
        em.clear();

        JsonEntity loaded = em.find(JsonEntity.class, entity.getId());

        assertThat(loaded).isEqualTo(entity);
        assertThat(loaded.getEmployee()).isEqualTo(entity.getEmployee());

        em.remove(loaded);
        em.flush();
        assertThat(em.find(JsonEntity.class, entity.getId())).isNull();
    }

}
