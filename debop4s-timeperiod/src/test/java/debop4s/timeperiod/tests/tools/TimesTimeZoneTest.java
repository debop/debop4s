package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.tools.TimesTimeZoneTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 9. 27. 오후 9:02
 */
@Slf4j
public class TimesTimeZoneTest extends TimePeriodTestBase {

    @BeforeClass
    public static void beforeClass() {
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    @Test
    public void withTimeZone() throws Exception {
        DateTimeZone tz = DateTimeZone.UTC;
        DateTime localTime = DateTime.now();

        DateTime utcTime = localTime.withZone(tz);

        log.debug("localTime=[{}]", localTime);
        log.debug("utcTime  =[{}]", utcTime);
    }

    @Test
    public void timezoneTest() throws Exception {
        DateTime dt = DateTime.now();
        String zoneId = dt.getZone().getID();
        DateTime utc = dt.withZone(DateTimeZone.UTC);

        DateTime local = new DateTime(utc, DateTimeZone.forID(zoneId));

        log.debug("time zone id=[{}]", zoneId);

        assertThat(local).isEqualTo(dt);
    }

    @Test
    public void availableDateTimeZoneTest() throws Exception {

        DateTime utcNow = DateTime.now(DateTimeZone.UTC);

        for (String id : DateTimeZone.getAvailableIDs()) {
            DateTimeZone tz = DateTimeZone.forID(id);
            DateTime tzNow = utcNow.toDateTime(tz);

            int offset = tz.getOffset(tzNow);
            log.debug("ID=[{}], TimeZone=[{}], Current=[{}], Now=[{}], offset=[{}]", id, tz, tzNow, utcNow, offset);
        }

        DateTime tokyo = utcNow.withZone(DateTimeZone.forID("Asia/Tokyo"));
        log.debug("now=[{}], Tokyo=[{}]", utcNow, tokyo);
    }

    @Test
    public void localDateTimeTest() throws Exception {
        DateTime utc = DateTime.now(DateTimeZone.UTC);
        LocalDateTime local = utc.toLocalDateTime();

        assertThat(local.toDateTime(DateTimeZone.UTC)).isEqualTo(utc);
    }

    @Test
    public void dateTimeZoneForOffsetMillis() throws Exception {
        DateTime utcNow = DateTime.now(DateTimeZone.UTC);
        LocalDateTime localNow = utcNow.toLocalDateTime();

        for (String id : DateTimeZone.getAvailableIDs()) {
            DateTimeZone tz = DateTimeZone.forID(id);
            DateTime tzTime = utcNow.toDateTime(tz);

            // TimeZone 별 offset 값
            int offset = tz.getOffset(0);
            log.debug("offset=[{}], TimeZone=[{}]", offset, tz);

            DateTimeZone localZone = DateTimeZone.forOffsetMillis(offset);
            DateTime localTimeZoneTime = utcNow.toDateTime(localZone);
            assertThat(localTimeZoneTime.getMillis()).isEqualTo(tzTime.getMillis());
        }
    }

    @Test
    public void timesGetTimezoneOffsetTest() throws Exception {
        DateTime utcNow = DateTime.now(DateTimeZone.UTC);

        for (String id : DateTimeZone.getAvailableIDs()) {
            DateTimeZone tz = DateTimeZone.forID(id);
            DateTime localNow = utcNow.toDateTime(tz);

            int offset = Times.timeZoneOffset(id);
            // offset=[32400000], TimeZone=[Asia/Seoul]
            log.debug("offset=[{}], TimeZone=[{}]", offset, tz);

            DateTimeZone localZone = Times.timeZoneForOffsetMillis(offset);

            // id=[ROK], offset=[32400000], localZone=[+09:00]
            log.debug("id=[{}], offset=[{}], localZone=[{}]", id, offset, localZone.getID());
            DateTime localTimeZoneTime = utcNow.toDateTime(localZone);

            assertThat(localTimeZoneTime.getMillis()).isEqualTo(localNow.getMillis());
        }
    }


}
