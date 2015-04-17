package debop4s.timeperiod.tests.base;

import debop4s.timeperiod.Timepart;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Durations;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
public class TimepartTest extends TimePeriodTestBase {

    private static final long serialVersionUID = -5272329190162193573L;

    @Test
    public void timeConstructorTest() {
        DateTime now = DateTime.now();
        Timepart time = new Timepart(now);

        log.debug("now=[{}], time=[{}]", now, time);

        assertThat(time.hour()).isEqualTo(now.getHourOfDay());
        assertThat(time.minute()).isEqualTo(now.getMinuteOfHour());
        assertThat(time.second()).isEqualTo(now.getSecondOfMinute());
        assertThat(time.millis()).isEqualTo(now.getMillisOfSecond());

        assertThat(time.totalMillis()).isEqualTo(now.getMillisOfDay());
    }

    @Test
    public void emptyDateTimeConstructor() {
        DateTime today = Times.today();
        Timepart time = Times.timepart(today);

        assertThat(time.totalMillis()).isEqualTo(0);

        assertThat(time.hour()).isEqualTo(0);
        assertThat(time.minute()).isEqualTo(0);
        assertThat(time.second()).isEqualTo(0);
        assertThat(time.millis()).isEqualTo(0);
        assertThat(time.millis()).isEqualTo(0);

        assertThat(time.totalHours()).isEqualTo(0);
        assertThat(time.totalMinutes()).isEqualTo(0);
        assertThat(time.totalSeconds()).isEqualTo(0);
        assertThat(time.totalMillis()).isEqualTo(0);
    }

    @Test
    public void constructorTest() {
        Timepart time = new Timepart(18, 23, 56, 344);

        assertThat(time.hour()).isEqualTo(18);
        assertThat(time.minute()).isEqualTo(23);
        assertThat(time.second()).isEqualTo(56);
        assertThat(time.millis()).isEqualTo(344);
    }

    @Test
    public void emptyConstructorTest() {
        Timepart time = new Timepart();

        assertThat(time.hour()).isEqualTo(0);
        assertThat(time.minute()).isEqualTo(0);
        assertThat(time.second()).isEqualTo(0);
        assertThat(time.millis()).isEqualTo(0);
        assertThat(time.millis()).isEqualTo(0);

        assertThat(time.totalHours()).isEqualTo(0);
        assertThat(time.totalMinutes()).isEqualTo(0);
        assertThat(time.totalSeconds()).isEqualTo(0);
        assertThat(time.totalMillis()).isEqualTo(0);
    }

    @Test
    public void durationTest() {
        Duration test = Durations.hours(18, 23, 56, 344);
        Timepart time = new Timepart(test);

        assertThat(time.hour()).isEqualTo(18);
        assertThat(time.minute()).isEqualTo(23);
        assertThat(time.second()).isEqualTo(56);
        assertThat(time.millis()).isEqualTo(344);

        assertThat(time.totalMillis()).isEqualTo(test.getMillis());
    }

    @Test
    public void getDateTimeTest() {
        DateTime now = Times.now();
        Duration test = Durations.hours(18, 23, 56, 344);
        Timepart time = new Timepart(test);

        assertThat(time.getDateTime(now)).isEqualTo(now.withTimeAtStartOfDay().plus(test));
    }

    @Test
    public void getEmptyDateTimeTest() {
        DateTime today = Times.today();
        Timepart time = new Timepart();

        assertThat(time.getDateTime(today)).isEqualTo(today);
        assertThat(time.getDateTime(today).getMillisOfDay()).isEqualTo(0);
    }

}
