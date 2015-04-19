package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.ITimePeriod;
import debop4s.timeperiod.TimeRange;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.timeperiod.test.tools.TimesFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 21. 오후 6:06
 */
@Slf4j
public class TimesTest extends TimePeriodTestBase {

    @Test
    public void asStringTest() {
        ITimePeriod period = new TimeRange(testDate, testNow);
        String periodString = Times.asString(period);

        log.debug("periodString=[{}]", periodString);
        assertThat(periodString).isNotEmpty();
    }

    @Test
    public void toDateTimeTest() {
        String dateString = testDate.toString();
        log.debug("dateString=[{}]", dateString);

        DateTime parsedTime = Times.toDateTime(dateString);

        assertThat(parsedTime.isEqual(testDate)).isTrue();

        parsedTime = Times.toDateTime("", testNow);
        assertThat(parsedTime).isEqualTo(testNow);
    }

    @Test
    public void parseString() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime dateTime = formatter.parseDateTime("20131010");
        assertThat(dateTime).isEqualTo(Times.asDate(2013, 10, 10));
    }
}
