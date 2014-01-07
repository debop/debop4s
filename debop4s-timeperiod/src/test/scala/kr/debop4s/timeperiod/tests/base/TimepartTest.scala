package kr.debop4s.timeperiod.tests.base

import kr.debop4s.time._
import kr.debop4s.timeperiod.Timepart
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import org.junit.Test

/**
 * kr.debop4s.timeperiod.tests.base.TimepartTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 7. 오후 4:02
 */
class TimepartTest extends AbstractTimePeriodTest {

    @Test
    def constructorTest() {
        val now: DateTime = Times.now
        val time: Timepart = Timepart(now)

        log.debug(s"now=[$now], time=[$time]")

        assert(time.hour == now.getHourOfDay)
        assert(time.minute == now.getMinuteOfHour)
        assert(time.second == now.getSecondOfMinute)
        assert(time.millis == now.getMillisOfSecond)

        assert(time.millisOfDay == now.getMillisOfDay)
    }

}
