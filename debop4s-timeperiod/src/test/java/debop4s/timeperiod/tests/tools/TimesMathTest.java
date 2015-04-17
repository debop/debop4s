package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Durations;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;
import scala.Tuple2;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class TimesMathTest extends TimePeriodTestBase {

    public final DateTime min = new DateTime(2000, 10, 2, 13, 45, 53, 754);
    public final DateTime max = new DateTime(2002, 9, 3, 7, 14, 22, 234);

    @Test
    public void minTest() {
        assertThat(Times.min(min, max)).isEqualTo(min);
        assertThat(Times.min(min, min)).isEqualTo(min);
        assertThat(Times.min(max, max)).isEqualTo(max);

        assertThat(Times.min(min, null)).isEqualTo(min);
        assertThat(Times.min(null, min)).isEqualTo(min);
        assertThat(Times.min((DateTime) null, null)).isNull();
    }

    @Test
    public void maxTest() {
        assertThat(Times.max(min, max)).isEqualTo(max);
        assertThat(Times.max(min, min)).isEqualTo(min);
        assertThat(Times.max(max, max)).isEqualTo(max);

        assertThat(Times.max(max, null)).isEqualTo(max);
        assertThat(Times.max(null, max)).isEqualTo(max);
        assertThat(Times.max((DateTime) null, null)).isNull();
    }

    @Test
    public void adjustPeriodTest() {

        Tuple2<DateTime, DateTime> pair = Times.adjustPeriod(max, min);
        assertThat(pair._1()).isEqualTo(min);
        assertThat(pair._2()).isEqualTo(max);

        pair = Times.adjustPeriod(min, max);
        assertThat(pair._1()).isEqualTo(min);
        assertThat(pair._2()).isEqualTo(max);
    }

    @Test
    public void adjustPeriodByDurationTest() {
        DateTime start = min;
        Duration duration = Durations.Day();

        Tuple2<DateTime, Duration> pair = Times.adjustPeriod(start, duration);
        assertThat(pair._1()).isEqualTo(min);
        assertThat(pair._2()).isEqualTo(Durations.Day());

        pair = Times.adjustPeriod(start, Durations.negate(duration));
        assertThat(pair._1()).isEqualTo(min.minus(Durations.Day()));
        assertThat(pair._2()).isEqualTo(Durations.Day());
    }

}

