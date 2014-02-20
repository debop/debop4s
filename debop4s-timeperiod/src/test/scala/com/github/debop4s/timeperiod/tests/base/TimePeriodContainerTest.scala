package com.github.debop4s.timeperiod.tests.base

import com.github.debop4s.timeperiod.tests.AbstractTimePeriodTest
import com.github.debop4s.timeperiod.utils.Times._
import com.github.debop4s.timeperiod.{TimeRange, TimePeriodContainer}

/**
 * com.github.debop4s.timeperiod.tests.base.TimePeriodContainerTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 19. 오후 4:11
 */
class TimePeriodContainerTest extends AbstractTimePeriodTest {

    test("constructor") {

        val period1 = TimeRange(asDate(2011, 4, 15), asDate(2011, 4, 20))
        val period2 = TimeRange(asDate(2011, 4, 22), asDate(2011, 4, 25))

        val container = TimePeriodContainer(period1, period2)
        log.trace(s"container=$container")
        container.size should equal(2)

        // 값 중복을 허용하지 않습니다^^
        val container2 = TimePeriodContainer(period1, period2, container)
        assert(container2.length == 2)
    }

}