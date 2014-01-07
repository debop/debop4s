package kr.debop4s.timeperiod.tests.base

import kr.debop4s.time._
import kr.debop4s.timeperiod.Timepart
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.utils.{Durations, Times}
import org.fest.assertions.Assertions
import org.joda.time.{Duration, DateTime}
import org.junit.Test

/**
 * kr.debop4s.timeperiod.tests.base.TimepartTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 7. 오후 4:02
 */
class TimepartTest extends AbstractTimePeriodTest {

    @Test
    def timeConstructorTest() {
        val now: DateTime = Times.now
        val time: Timepart = Timepart(now)

        log.debug(s"now=[$now], time=[$time]")

        assert(time.hour == now.hour.get())
        assert(time.minute == now.minute.get())
        assert(time.second == now.second.get())
        assert(time.millis == now.millis.get())

        assert(time.totalMillis == now.getMillisOfDay)
    }

    @Test
    def emptyDateTimeConstructor() {
        val today: DateTime = Times.today
        val time: Timepart = Times.timepart(today)

        Assertions.assertThat(time.millis).isEqualTo(0)
        Assertions.assertThat(time.hour).isEqualTo(0)
        Assertions.assertThat(time.minute).isEqualTo(0)
        Assertions.assertThat(time.second).isEqualTo(0)
        Assertions.assertThat(time.millis).isEqualTo(0)
        Assertions.assertThat(time.millis).isEqualTo(0)
        Assertions.assertThat(time.totalHours).isEqualTo(0)
        Assertions.assertThat(time.totalMinutes).isEqualTo(0)
        Assertions.assertThat(time.totalSeconds).isEqualTo(0)
        Assertions.assertThat(time.totalMillis).isEqualTo(0)
    }
    @Test
    def constructorTest() {
        val time: Timepart = Timepart(18, 23, 56, 344)
        Assertions.assertThat(time.hour).isEqualTo(18)
        Assertions.assertThat(time.minute).isEqualTo(23)
        Assertions.assertThat(time.second).isEqualTo(56)
        Assertions.assertThat(time.millis).isEqualTo(344)
    }
    @Test
    def emptyConstructorTest() {
        val time: Timepart = Timepart()
        Assertions.assertThat(time.millis).isEqualTo(0)
        Assertions.assertThat(time.hour).isEqualTo(0)
        Assertions.assertThat(time.minute).isEqualTo(0)
        Assertions.assertThat(time.second).isEqualTo(0)
        Assertions.assertThat(time.millis).isEqualTo(0)
        Assertions.assertThat(time.millis).isEqualTo(0)
        Assertions.assertThat(time.totalHours).isEqualTo(0)
        Assertions.assertThat(time.totalMinutes).isEqualTo(0)
        Assertions.assertThat(time.totalSeconds).isEqualTo(0)
        Assertions.assertThat(time.totalMillis).isEqualTo(0)
    }
    @Test
    def durationTest() {
        val test: Duration = Durations.hours(18, 23, 56, 344)
        val time: Timepart = Timepart(test)
        Assertions.assertThat(time.hour).isEqualTo(18)
        Assertions.assertThat(time.minute).isEqualTo(23)
        Assertions.assertThat(time.second).isEqualTo(56)
        Assertions.assertThat(time.millis).isEqualTo(344)

        Assertions.assertThat(time.totalMillis).isEqualTo(test.getMillis)
    }
    @Test
    def getDateTimeTest() {
        val now: DateTime = Times.now
        val test: Duration = Durations.hours(18, 23, 56, 344)
        val time: Timepart = Timepart(test)
        assert(time.getDateTime(now) == (now.withTimeAtStartOfDay() + test))
    }
    @Test
    def getEmptyDateTimeTest() {
        val today: DateTime = Times.today
        val time: Timepart = Timepart()

        assert(time.getDateTime(today) == today)
        assert(time.getDateTime(today).getMillisOfDay == 0)
    }

}
