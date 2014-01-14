package kr.debop4s.timeperiod.tests.base

import kr.debop4s.time._
import kr.debop4s.timeperiod.Timepart
import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.utils.{Durations, Times}
import org.joda.time.{Duration, DateTime}
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.timeperiod.tests.base.TimepartTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 7. 오후 4:02
 */
class TimepartTest extends AbstractTimePeriodTest {

    lazy val log = LoggerFactory.getLogger(getClass)

    test("timepart constructor") {
        val now: DateTime = Times.now
        val time: Timepart = Timepart(now)

        log.debug(s"now=[$now], time=[$time], zero=[${Times.zero}]")

        assert(time.hour == now.hour.get())
        assert(time.minute == now.minute.get())
        assert(time.second == now.second.get())
        assert(time.millis == now.millis.get())

        assert(time.totalMillis == now.getMillisOfDay)
    }

    test("zero millis") {
        val today: DateTime = Times.today
        val time: Timepart = Times.timepart(today)

        assert(time.millis === 0)
        assert(time.hour === 0)
        assert(time.minute === 0)
        assert(time.second === 0)
        assert(time.millis === 0)
        assert(time.millis === 0)
        assert(time.totalHours === 0)
        assert(time.totalMinutes === 0)
        assert(time.totalSeconds === 0)
        assert(time.totalMillis === 0)
        assert(time.totalMillis == 0)
    }

    test("non zero millis") {
        val time: Timepart = Timepart(18, 23, 56, 344)
        log.debug(s"time=[$time]")
        assert(time.hour === 18)
        assert(time.minute === 23)
        assert(time.second === 56)
        assert(time.millis === 344)
    }

    test("create with empty parameter") {
        val time: Timepart = Timepart()
        log.debug(s"time=[$time]")
        assert(time.millis === 0)
        assert(time.hour === 0)
        assert(time.minute === 0)
        assert(time.second === 0)
        assert(time.millis === 0)
        assert(time.millis === 0)
        assert(time.totalHours === 0)
        assert(time.totalMinutes === 0)
        assert(time.totalSeconds === 0)
        assert(time.totalMillis === 0)
    }

    test("create with duration") {
        val test: Duration = Durations.hours(18, 23, 56, 344)
        val time: Timepart = Timepart(Some(test))
        log.debug(s"time=[$time]")
        assert(time.hour === 18)
        assert(time.minute === 23)
        assert(time.second === 56)
        assert(time.millis === 344)

        assert(time.totalMillis === test.getMillis)
    }

    test("getDateTime") {
        val now: DateTime = Times.now
        val duration: Duration = Durations.hours(18, 23, 56, 344)
        val time: Timepart = Timepart(Some(duration))

        log.debug(s"time=[$time]")
        assert(time.getDateTime(now) == now.withTimeAtStartOfDay() + duration)
    }

    test("getDateTime with Empty Timepart") {
        val today: DateTime = Times.today
        val time: Timepart = Timepart()
        log.debug(s"time=[$time]")
        assert(time.getDateTime(today) == today)
        assert(time.getDateTime(today).getMillisOfDay == 0)
    }

}
