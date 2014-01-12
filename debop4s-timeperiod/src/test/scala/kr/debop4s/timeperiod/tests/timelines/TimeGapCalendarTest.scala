package kr.debop4s.timeperiod.tests.timelines

import kr.debop4s.timeperiod.tests.AbstractTimePeriodTest
import kr.debop4s.timeperiod.timeline.TimeGapCalculator
import kr.debop4s.timeperiod.utils.Times
import kr.debop4s.timeperiod.{TimePeriodCollection, TimeRange}

/**
 * kr.debop4s.timeperiod.tests.timelines.TimeGapCalendarTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 12. 오후 7:53
 */
class TimeGapCalendarTest extends AbstractTimePeriodTest {

    def fixture = new {
        val limits = TimeRange(Times.asDate(2011, 3, 1), Times.asDate(2011, 3, 5))
        val calculator = TimeGapCalculator()
    }

    test("no periods") {
        val f = fixture
        val gaps = f.calculator.getGaps(TimePeriodCollection(), f.limits)
        gaps.size shouldBe 1
        gaps(0).isSamePeriod(f.limits) shouldBe true
    }

    test("period equals limits when excludePeriods has limits") {
        val f = fixture
        val excludePeriods = TimePeriodCollection(f.limits)
        val gaps = f.calculator.getGaps(excludePeriods, f.limits)
        gaps.size shouldBe 0
    }

    test("period is larger than limits") {
        val f = fixture
        val excludePeriods = TimePeriodCollection(TimeRange(Times.asDate(2011, 2, 1), Times.asDate(2011, 4, 1)))

        val gaps = f.calculator.getGaps(excludePeriods, f.limits)
        gaps.size shouldBe 0
    }

    test("period is outside with limits") {
        val f = fixture
        val excludePeriods = TimePeriodCollection(TimeRange(Times.asDate(2011, 2, 1), Times.asDate(2011, 2, 5)),
            TimeRange(Times.asDate(2011, 4, 1), Times.asDate(2011, 4, 5)))
        val gaps = f.calculator.getGaps(excludePeriods, f.limits)

        gaps.size shouldBe 1
        gaps(0).isSamePeriod(f.limits) shouldBe true
    }
}
