package debop4s.timeperiod.tests.tools;

import debop4s.timeperiod.tests.TimePeriodTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static debop4s.timeperiod.utils.Times.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * kr.hconnect.timeperiod.test.tools.TimesDateTimeTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 23. 오후 11:32
 */
@Slf4j
public class TimesDateTimeTest extends TimePeriodTestBase {

    @Test
    public void getDateTest() {
        assertThat(getDate(testDate)).isEqualTo(testDate.withTimeAtStartOfDay());
        assertThat(getDate(testNow)).isEqualTo(testNow.withTimeAtStartOfDay());
    }

    @Test
    public void setDateTest() {
        assertThat(getDate(setDate(testDate, testNow))).isEqualTo(testNow.withTimeAtStartOfDay());
        assertThat(getDate(setDate(testNow, testDate))).isEqualTo(testDate.withTimeAtStartOfDay());
    }

    @Test
    public void hasTimeOfDayTest() {
        assertThat(hasTime(testDate)).isTrue();
        assertThat(hasTime(testNow)).isTrue();
        assertThat(hasTime(getDate(testNow))).isFalse();

        assertThat(hasTime(setTime(testNow, 1))).isTrue();
        assertThat(hasTime(setTime(testNow, 0, 1))).isTrue();

        assertThat(hasTime(setTime(testNow, 0, 0, 0, 0))).isFalse();
    }

    @Test
    public void setTimeOfDayTest() {
        assertThat(hasTime(setTime(testDate, testNow))).isTrue();
        assertThat(setTime(testDate, testNow).getMillisOfDay()).isEqualTo(testNow.getMillisOfDay());
        assertThat(setTime(testNow, testDate).getMillisOfDay()).isEqualTo(testDate.getMillisOfDay());
    }
}
