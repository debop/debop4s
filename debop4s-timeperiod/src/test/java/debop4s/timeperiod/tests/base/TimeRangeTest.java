package debop4s.timeperiod.tests.base;

import debop4s.timeperiod.PeriodRelation;
import debop4s.timeperiod.TimeRange;
import debop4s.timeperiod.TimeSpec;
import debop4s.timeperiod.samples.TimeRangePeriodRelationTestData;
import debop4s.timeperiod.tests.TimePeriodTestBase;
import debop4s.timeperiod.utils.Durations;
import debop4s.timeperiod.utils.Times;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
public class TimeRangeTest extends TimePeriodTestBase {

    private final Duration duration = new Duration(Durations.hours(1));
    private final Duration offset = Durations.Second();

    private DateTime start = DateTime.now();
    private DateTime end = start.plus(duration);
    private TimeRangePeriodRelationTestData testData = new TimeRangePeriodRelationTestData(start, end, offset);

    @Test
    public void anytimeTest() throws Exception {
        assertThat(TimeRange.Anytime().getStart()).isEqualTo(TimeSpec.MinPeriodTime);
        assertThat(TimeRange.Anytime().getEnd()).isEqualTo(TimeSpec.MaxPeriodTime);

        assertThat(TimeRange.Anytime().isAnytime()).isTrue();
        assertThat(TimeRange.Anytime().isReadonly()).isTrue();

        assertThat(TimeRange.Anytime().hasPeriod()).isFalse();
        assertThat(TimeRange.Anytime().hasStart()).isFalse();
        assertThat(TimeRange.Anytime().hasEnd()).isFalse();
        assertThat(TimeRange.Anytime().isMoment()).isFalse();
    }

    @Test
    public void defaultContructorTest() throws Exception {
        TimeRange range = new TimeRange();

        assertThat(range).isNotEqualTo(TimeRange.Anytime());
        assertThat(Times.relation(range, TimeRange.Anytime())).isEqualTo(PeriodRelation.ExactMatch);

        assertThat(range.isAnytime()).isTrue();
        assertThat(range.isReadonly()).isFalse();

        assertThat(range.hasPeriod()).isFalse();
        assertThat(range.hasStart()).isFalse();
        assertThat(range.hasEnd()).isFalse();
        assertThat(range.isMoment()).isFalse();
    }

    @Test
    public void momentTest() throws Exception {
        DateTime moment = Times.now();
        TimeRange range = new TimeRange(moment);

        assertThat(range.hasStart()).isTrue();
        assertThat(range.hasEnd()).isTrue();
        assertThat(range.getDuration()).isEqualTo(TimeSpec.MinDuration);

        assertThat(range.isAnytime()).isFalse();
        assertThat(range.isMoment()).isTrue();
        assertThat(range.hasPeriod()).isTrue();
    }

    @Test
    public void momentByPeriod() {
        TimeRange range = new TimeRange(Times.now(), Duration.ZERO);
        assertThat(range.isMoment()).isTrue();
    }

    @Test
    public void nonMomentTest() {
        TimeRange range = new TimeRange(Times.now(), TimeSpec.MinPositiveDuration);
        assertThat(range.isMoment()).isFalse();
        assertThat(range.getDuration()).isEqualTo(TimeSpec.MinPositiveDuration);
    }

    @Test
    public void hasStartTest() {
        // 현재부터 ~
        TimeRange range = new TimeRange(Times.now(), (DateTime) null);
        assertThat(range.hasStart()).isTrue();
        assertThat(range.hasEnd()).isFalse();
    }

    @Test
    public void hasEndTest() {
        //  ~ 현재까지
        TimeRange range = new TimeRange(null, Times.now());
        assertThat(range.hasStart()).isFalse();
        assertThat(range.hasEnd()).isTrue();
    }

    @Test
    public void startEndTest() {
        TimeRange range = new TimeRange(start, end);

        assertThat(range.getStart()).isEqualTo(start);
        assertThat(range.getEnd()).isEqualTo(end);
        assertThat(range.getDuration()).isEqualTo(duration);

        assertThat(range.hasPeriod()).isTrue();
        assertThat(range.isAnytime()).isFalse();
        assertThat(range.isMoment()).isFalse();
        assertThat(range.isReadonly()).isFalse();
    }

    @Test
    public void startEndSwapTest() {
        TimeRange range = new TimeRange(end, start);

        assertThat(range.getStart()).isEqualTo(start);
        assertThat(range.getEnd()).isEqualTo(end);
        assertThat(range.getDuration()).isEqualTo(duration);

        assertThat(range.hasPeriod()).isTrue();
        assertThat(range.isAnytime()).isFalse();
        assertThat(range.isMoment()).isFalse();
        assertThat(range.isReadonly()).isFalse();
    }

    @Test
    public void startAndDurationTest() {
        TimeRange range = new TimeRange(start, duration);

        assertThat(range.getStart()).isEqualTo(start);
        assertThat(range.getEnd()).isEqualTo(end);
        assertThat(range.getDuration()).isEqualTo(duration);

        assertThat(range.hasPeriod()).isTrue();
        assertThat(range.isAnytime()).isFalse();
        assertThat(range.isMoment()).isFalse();
        assertThat(range.isReadonly()).isFalse();
    }

    @Test
    public void startAndNegateDurationTest() {
        TimeRange range = new TimeRange(start, Durations.negate(duration));

        assertThat(range.getStart()).isEqualTo(start.minus(duration));
        assertThat(range.getEnd()).isEqualTo(end.minus(duration));
        assertThat(range.getDuration()).isEqualTo(duration);

        assertThat(range.hasPeriod()).isTrue();
        assertThat(range.isAnytime()).isFalse();
        assertThat(range.isMoment()).isFalse();
        assertThat(range.isReadonly()).isFalse();
    }

    @Test
    public void copyConstructorTest() {
        TimeRange source = new TimeRange(start, start.plusHours(1), true);
        TimeRange copy = new TimeRange(source);

        assertThat(copy.getStart()).isEqualTo(source.getStart());
        assertThat(copy.getEnd()).isEqualTo(source.getEnd());
        assertThat(copy.getDuration()).isEqualTo(source.getDuration());

        assertThat(copy.isReadonly()).isEqualTo(source.isReadonly());

        assertThat(copy.hasPeriod()).isTrue();
        assertThat(copy.isAnytime()).isFalse();
        assertThat(copy.isMoment()).isFalse();
    }

    @Test
    public void startTest() {
        TimeRange range = new TimeRange(start, start.plusHours(1));
        assertThat(range.getStart()).isEqualTo(start);

        DateTime chanedStart = start.plusHours(1);
        range.setStart(chanedStart);
        assertThat(range.getStart()).isEqualTo(chanedStart);
    }

    @Test(expected = AssertionError.class)
    public void startReadonlyTest() {
        TimeRange range = new TimeRange(Times.now(), Durations.hours(1), true);
        range.setStart(range.getStart().minusHours(2));
    }

    @Test(expected = AssertionError.class)
    public void startOutOfRangeTest() {
        TimeRange range = new TimeRange(Times.now(), Durations.hours(1), false);
        range.setStart(range.getStart().plusHours(2));
    }

    @Test
    public void endTest() throws Exception {
        TimeRange range = new TimeRange(end.minusHours(1), end);
        assertThat(range.getEnd()).isEqualTo(end);

        DateTime changedEnd = end.plusHours(1);
        range.setEnd(changedEnd);
        assertThat(range.getEnd()).isEqualTo(changedEnd);
    }

    @Test(expected = AssertionError.class)
    public void endReadonlyTest() {
        TimeRange range = new TimeRange(Times.now(), Durations.hours(1), true);
        range.setEnd(range.getEnd().plusHours(1));
    }

    @Test(expected = AssertionError.class)
    public void endOutOfRangeTest() {
        TimeRange range = new TimeRange(Times.now(), Durations.hours(1), false);
        range.setEnd(range.getEnd().minusHours(2));
    }

    @Test
    public void hasInsideDateTimeTest() {
        TimeRange range = new TimeRange(start, end);

        assertThat(range.getEnd()).isEqualTo(end);

        assertThat(range.hasInside(start.minus(duration))).isFalse();
        assertThat(range.hasInside(start)).isTrue();
        assertThat(range.hasInside(start.plus(duration))).isTrue();

        assertThat(range.hasInside(end.minus(duration))).isTrue();
        assertThat(range.hasInside(end)).isTrue();
        assertThat(range.hasInside(end.plus(duration))).isFalse();
    }

    @Test
    public void hasInsidePeriodTest() {
        TimeRange range = new TimeRange(start, end);

        assertThat(range.getEnd()).isEqualTo(end);

        // before
        TimeRange before1 = new TimeRange(start.minusHours(2), start.minusHours(1));
        TimeRange before2 = new TimeRange(start.minusMillis(1), end);
        TimeRange before3 = new TimeRange(start.minusMillis(1), start);

        assertThat(range.hasInside(before1)).isFalse();
        assertThat(range.hasInside(before2)).isFalse();
        assertThat(range.hasInside(before3)).isFalse();

        // after
        TimeRange after1 = new TimeRange(start.plusHours(1), end.plusHours(1));
        TimeRange after2 = new TimeRange(start, end.plusMillis(1));
        TimeRange after3 = new TimeRange(end, end.plusMillis(1));

        assertThat(range.hasInside(after1)).isFalse();
        assertThat(range.hasInside(after2)).isFalse();
        assertThat(range.hasInside(after3)).isFalse();

        // inside
        assertThat(range.hasInside(range)).isTrue();

        TimeRange inside1 = new TimeRange(start.plusMillis(1), end);
        TimeRange inside2 = new TimeRange(start.plusMillis(1), end.minusMillis(1));
        TimeRange inside3 = new TimeRange(start, end.minusMillis(1));

        assertThat(range.hasInside(inside1)).isTrue();
        assertThat(range.hasInside(inside2)).isTrue();
        assertThat(range.hasInside(inside3)).isTrue();
    }

    @Test
    public void copyTest() {
        TimeRange readonlyTimeRange = new TimeRange(start, end);
        assertThat(readonlyTimeRange.copy()).isEqualTo(readonlyTimeRange);
        assertThat(readonlyTimeRange.copy(Duration.ZERO)).isEqualTo(readonlyTimeRange);

        TimeRange range = new TimeRange(start, end);

        assertThat(range.getStart()).isEqualTo(start);
        assertThat(range.getEnd()).isEqualTo(end);

        TimeRange noMove = range.copy(Durations.Zero());

        assertThat(noMove.getStart()).isEqualTo(range.getStart());
        assertThat(noMove.getEnd()).isEqualTo(range.getEnd());
        assertThat(noMove.getDuration()).isEqualTo(range.getDuration());
        assertThat(noMove).isEqualTo(noMove);

        Duration forwardOffset = Durations.hours(2, 30, 15);
        TimeRange forward = range.copy(forwardOffset);

        assertThat(forward.getStart()).isEqualTo(start.plus(forwardOffset));
        assertThat(forward.getEnd()).isEqualTo(end.plus(forwardOffset));
        assertThat(forward.getDuration()).isEqualTo(duration);

        Duration backwardOffset = Durations.hours(-1, 10, 30);
        TimeRange backward = range.copy(backwardOffset);

        assertThat(backward.getStart()).isEqualTo(start.plus(backwardOffset));
        assertThat(backward.getEnd()).isEqualTo(end.plus(backwardOffset));
        assertThat(backward.getDuration()).isEqualTo(duration);
    }

    @Test
    public void moveTest() {
        TimeRange moveZero = new TimeRange(start, end);
        moveZero.move(Durations.Zero());
        assertThat(moveZero.getStart()).isEqualTo(start);
        assertThat(moveZero.getEnd()).isEqualTo(end);
        assertThat(moveZero.getDuration()).isEqualTo(duration);

        TimeRange forward = new TimeRange(start, end);
        Duration forwardOffset = Durations.hours(2, 30, 15);
        forward.move(forwardOffset);

        assertThat(forward.getStart()).isEqualTo(start.plus(forwardOffset));
        assertThat(forward.getEnd()).isEqualTo(end.plus(forwardOffset));
        assertThat(forward.getDuration()).isEqualTo(duration);

        TimeRange backward = new TimeRange(start, end);
        Duration backwardOffset = Durations.hours(-1, 10, 30);
        backward.move(backwardOffset);

        assertThat(backward.getStart()).isEqualTo(start.plus(backwardOffset));
        assertThat(backward.getEnd()).isEqualTo(end.plus(backwardOffset));
        assertThat(backward.getDuration()).isEqualTo(duration);
    }

    @Test
    public void expandStartToTest() {
        TimeRange range = new TimeRange(start, end);

        range.expandStartTo(start.plusMillis(1));
        assertThat(range.getStart()).isEqualTo(start);

        range.expandStartTo(start.minusMillis(1));
        assertThat(range.getStart()).isEqualTo(start.minusMillis(1));
    }

    @Test
    public void expandEndToTest() {
        TimeRange range = new TimeRange(start, end);

        range.expandEndTo(end.minusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end);

        range.expandEndTo(end.plusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end.plusMillis(1));
    }

    @Test
    public void expandToDateTimeTest() {
        TimeRange range = new TimeRange(start, end);

        // start
        range.expandTo(start.plusMillis(1));
        assertThat(range.getStart()).isEqualTo(start);

        range.expandTo(start.minusMillis(1));
        assertThat(range.getStart()).isEqualTo(start.minusMillis(1));

        // end
        range.expandTo(end.minusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end);

        range.expandTo(end.plusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end.plusMillis(1));
    }

    @Test
    public void expandToPeriodTest() {
        TimeRange range = new TimeRange(start, end);

        // no expansion
        range.expandTo(new TimeRange(start.plusMillis(1), end.minusMillis(1)));
        assertThat(range.getStart()).isEqualTo(start);
        assertThat(range.getEnd()).isEqualTo(end);

        // start
        DateTime changedStart = start.minusMinutes(1);
        range.expandTo(new TimeRange(changedStart, end));
        assertThat(range.getStart()).isEqualTo(changedStart);
        assertThat(range.getEnd()).isEqualTo(end);

        // end
        DateTime changedEnd = end.plusMinutes(1);
        range.expandTo(new TimeRange(changedStart, changedEnd));
        assertThat(range.getStart()).isEqualTo(changedStart);
        assertThat(range.getEnd()).isEqualTo(changedEnd);

        // start/end
        changedStart = changedStart.minusMinutes(1);
        changedEnd = changedEnd.plusMinutes(1);
        range.expandTo(new TimeRange(changedStart, changedEnd));
        assertThat(range.getStart()).isEqualTo(changedStart);
        assertThat(range.getEnd()).isEqualTo(changedEnd);
    }

    @Test
    public void shrinkStartToTest() {
        TimeRange range = new TimeRange(start, end);

        range.shrinkStartTo(start.minusMillis(1));
        assertThat(range.getStart()).isEqualTo(start);

        range.shrinkStartTo(start.plusMillis(1));
        assertThat(range.getStart()).isEqualTo(start.plusMillis(1));
    }

    @Test
    public void shrinkEndToTest() {
        TimeRange range = new TimeRange(start, end);

        range.shrinkEndTo(end.plusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end);

        range.shrinkEndTo(end.minusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end.minusMillis(1));
    }

    @Test
    public void shrinkToDateTimeTest() {
        TimeRange range = new TimeRange(start, end);

        // start
        range.shrinkTo(start.minusMillis(1));
        assertThat(range.getStart()).isEqualTo(start);

        range.shrinkTo(start.plusMillis(1));
        assertThat(range.getStart()).isEqualTo(start.plusMillis(1));

        range = new TimeRange(start, end);

        // end
        range.shrinkTo(end.plusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end);

        range.shrinkTo(end.minusMillis(1));
        assertThat(range.getEnd()).isEqualTo(end.minusMillis(1));
    }

    @Test
    public void shrinkToPeriodTest() {
        TimeRange range = new TimeRange(start, end);

        // no expansion
        range.shrinkTo(new TimeRange(start.minusMillis(1), end.plusMillis(1)));
        assertThat(range.getStart()).isEqualTo(start);
        assertThat(range.getEnd()).isEqualTo(end);

        // start
        DateTime changedStart = start.plusMinutes(1);
        range.shrinkTo(new TimeRange(changedStart, end));
        assertThat(range.getStart()).isEqualTo(changedStart);
        assertThat(range.getEnd()).isEqualTo(end);

        // end
        DateTime changedEnd = end.minusMinutes(1);
        range.shrinkTo(new TimeRange(changedStart, changedEnd));
        assertThat(range.getStart()).isEqualTo(changedStart);
        assertThat(range.getEnd()).isEqualTo(changedEnd);

        // start/end
        changedStart = changedStart.plusMinutes(1);
        changedEnd = changedEnd.minusMinutes(1);
        range.shrinkTo(new TimeRange(changedStart, changedEnd));
        assertThat(range.getStart()).isEqualTo(changedStart);
        assertThat(range.getEnd()).isEqualTo(changedEnd);
    }

    @Test
    public void isSamePeriodTest() {
        TimeRange range1 = new TimeRange(start, end);
        TimeRange range2 = new TimeRange(start, end);

        assertThat(range1.isSamePeriod(range1)).isTrue();
        assertThat(range2.isSamePeriod(range2)).isTrue();

        assertThat(range1.isSamePeriod(range2)).isTrue();
        assertThat(range2.isSamePeriod(range1)).isTrue();

        assertThat(range1.isSamePeriod(TimeRange.Anytime())).isFalse();
        assertThat(range2.isSamePeriod(TimeRange.Anytime())).isFalse();

        range1.move(Durations.Millisecond());
        assertThat(range1.isSamePeriod(range2)).isFalse();
        assertThat(range2.isSamePeriod(range1)).isFalse();

        range1.move(Durations.millis(-1));
        assertThat(range1.isSamePeriod(range2)).isTrue();
        assertThat(range2.isSamePeriod(range1)).isTrue();
    }

    @Test
    public void hasInsideTest() {

        assertThat(testData.reference().hasInside(testData.before())).isFalse();
        assertThat(testData.reference().hasInside(testData.startTouching())).isFalse();
        assertThat(testData.reference().hasInside(testData.startInside())).isFalse();
        assertThat(testData.reference().hasInside(testData.insideStartTouching())).isFalse();

        assertThat(testData.reference().hasInside(testData.enclosingStartTouching())).isTrue();
        assertThat(testData.reference().hasInside(testData.enclosing())).isTrue();
        assertThat(testData.reference().hasInside(testData.enclosingEndTouching())).isTrue();
        assertThat(testData.reference().hasInside(testData.exactMatch())).isTrue();

        assertThat(testData.reference().hasInside(testData.inside())).isFalse();
        assertThat(testData.reference().hasInside(testData.insideEndTouching())).isFalse();
        assertThat(testData.reference().hasInside(testData.endTouching())).isFalse();
        assertThat(testData.reference().hasInside(testData.after())).isFalse();
    }

    @Test
    public void intersectsWithTest() {

        assertThat(testData.reference().intersectsWith(testData.before())).isFalse();
        assertThat(testData.reference().intersectsWith(testData.startTouching())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.startInside())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.insideStartTouching())).isTrue();

        assertThat(testData.reference().intersectsWith(testData.enclosingStartTouching())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.enclosing())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.enclosingEndTouching())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.exactMatch())).isTrue();

        assertThat(testData.reference().intersectsWith(testData.inside())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.insideEndTouching())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.endTouching())).isTrue();
        assertThat(testData.reference().intersectsWith(testData.after())).isFalse();
    }

    @Test
    public void overlapsWithTest() {

        assertThat(testData.reference().overlapsWith(testData.before())).isFalse();
        assertThat(testData.reference().overlapsWith(testData.startTouching())).isFalse();
        assertThat(testData.reference().overlapsWith(testData.startInside())).isTrue();
        assertThat(testData.reference().overlapsWith(testData.insideStartTouching())).isTrue();

        assertThat(testData.reference().overlapsWith(testData.enclosingStartTouching())).isTrue();
        assertThat(testData.reference().overlapsWith(testData.enclosing())).isTrue();
        assertThat(testData.reference().overlapsWith(testData.enclosingEndTouching())).isTrue();
        assertThat(testData.reference().overlapsWith(testData.exactMatch())).isTrue();

        assertThat(testData.reference().overlapsWith(testData.inside())).isTrue();
        assertThat(testData.reference().overlapsWith(testData.insideEndTouching())).isTrue();
        assertThat(testData.reference().overlapsWith(testData.endTouching())).isFalse();
        assertThat(testData.reference().overlapsWith(testData.after())).isFalse();
    }

    @Test
    public void intersectsWithDateTimeTest() {
        TimeRange range = new TimeRange(start, end);

        // before
        assertThat(range.intersectsWith(new TimeRange(start.minusHours(2), start.minusHours(1)))).isFalse();
        assertThat(range.intersectsWith(new TimeRange(start.minusHours(1), start))).isTrue();
        assertThat(range.intersectsWith(new TimeRange(start.minusHours(1), start.plusMillis(1)))).isTrue();

        // after
        assertThat(range.intersectsWith(new TimeRange(end.plusHours(1), end.plusHours(2)))).isFalse();
        assertThat(range.intersectsWith(new TimeRange(end, end.plusMillis(1)))).isTrue();
        assertThat(range.intersectsWith(new TimeRange(end.minusMillis(1), end.plusMillis(1)))).isTrue();

        // intersect
        assertThat(range.intersectsWith(range)).isTrue();
        assertThat(range.intersectsWith(new TimeRange(start.minusMillis(1), end.plusHours(2)))).isTrue();
        assertThat(range.intersectsWith(new TimeRange(start.minusMillis(1), start.plusMillis(1)))).isTrue();
        assertThat(range.intersectsWith(new TimeRange(end.minusMillis(1), end.plusMillis(1)))).isTrue();
    }

    @Test
    public void getIntersectionTest() {
        TimeRange range = new TimeRange(start, end);

        // before
        assertThat(range.intersection(new TimeRange(start.minusHours(2), start.minusHours(1)))).isNull();
        assertThat(range.intersection(new TimeRange(start.minusMillis(1), start))).isEqualTo(new TimeRange(start));
        assertThat(range.intersection(new TimeRange(start.minusHours(1), start.plusMillis(1)))).isEqualTo(new TimeRange(start, start.plusMillis(1)));

        // after
        assertThat(range.intersection(new TimeRange(end.plusHours(1), end.plusHours(2)))).isNull();
        assertThat(range.intersection(new TimeRange(end, end.plusMillis(1)))).isEqualTo(new TimeRange(end));
        assertThat(range.intersection(new TimeRange(end.minusMillis(1), end.plusMillis(1)))).isEqualTo(new TimeRange(end.minusMillis(1), end));

        // intersect
        assertThat(range.intersection(range)).isEqualTo(range);
        assertThat(range.intersection(new TimeRange(start.minusMillis(1), end.plusMillis(1)))).isEqualTo(range);
        assertThat(range.intersection(new TimeRange(start.plusMillis(1), end.minusMillis(1)))).isEqualTo(new TimeRange(start.plusMillis(1), end.minusMillis(1)));
    }

    @Test
    public void getRelationTest() {
        assertThat(testData.reference().relation(testData.before())).isEqualTo(PeriodRelation.Before);
        assertThat(testData.reference().relation(testData.startTouching())).isEqualTo(PeriodRelation.StartTouching);
        assertThat(testData.reference().relation(testData.startInside())).isEqualTo(PeriodRelation.StartInside);
        assertThat(testData.reference().relation(testData.insideStartTouching())).isEqualTo(PeriodRelation.InsideStartTouching);
        assertThat(testData.reference().relation(testData.enclosing())).isEqualTo(PeriodRelation.Enclosing);
        assertThat(testData.reference().relation(testData.exactMatch())).isEqualTo(PeriodRelation.ExactMatch);
        assertThat(testData.reference().relation(testData.inside())).isEqualTo(PeriodRelation.Inside);
        assertThat(testData.reference().relation(testData.insideEndTouching())).isEqualTo(PeriodRelation.InsideEndTouching);
        assertThat(testData.reference().relation(testData.getEndInside())).isEqualTo(PeriodRelation.EndInside);
        assertThat(testData.reference().relation(testData.endTouching())).isEqualTo(PeriodRelation.EndTouching);
        assertThat(testData.reference().relation(testData.after())).isEqualTo(PeriodRelation.After);

        // reference
        assertThat(testData.reference().getStart()).isEqualTo(start);
        assertThat(testData.reference().getEnd()).isEqualTo(end);
        assertThat(testData.reference().isReadonly()).isTrue();

        // after
        assertThat(testData.after().isReadonly()).isTrue();
        assertThat(testData.after().getStart().compareTo(start)).isLessThan(0);
        assertThat(testData.after().getEnd().compareTo(start)).isLessThan(0);

        assertThat(testData.reference().hasInside(testData.after().getStart())).isFalse();
        assertThat(testData.reference().hasInside(testData.after().getEnd())).isFalse();
        assertThat(testData.reference().relation(testData.after())).isEqualTo(PeriodRelation.After);

        // start touching
        assertThat(testData.startTouching().isReadonly()).isTrue();
        assertThat(testData.startTouching().getStart().getMillis()).isLessThan(start.getMillis());
        assertThat(testData.startTouching().getEnd()).isEqualTo(start);

        assertThat(testData.reference().hasInside(testData.startTouching().getStart())).isFalse();
        assertThat(testData.reference().hasInside(testData.startTouching().getEnd())).isTrue();
        assertThat(testData.reference().relation(testData.startTouching())).isEqualTo(PeriodRelation.StartTouching);

        // start inside
        assertThat(testData.startInside().isReadonly()).isTrue();
        assertThat(testData.startInside().getStart().getMillis()).isLessThan(start.getMillis());
        assertThat(testData.startInside().getEnd().getMillis()).isLessThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.startInside().getStart())).isFalse();
        assertThat(testData.reference().hasInside(testData.startInside().getEnd())).isTrue();
        assertThat(testData.reference().relation(testData.startInside())).isEqualTo(PeriodRelation.StartInside);

        // inside start touching
        assertThat(testData.insideStartTouching().isReadonly()).isTrue();
        assertThat(testData.insideStartTouching().getStart().getMillis()).isEqualTo(start.getMillis());
        assertThat(testData.insideStartTouching().getEnd().getMillis()).isGreaterThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.insideStartTouching().getStart())).isTrue();
        assertThat(testData.reference().hasInside(testData.insideStartTouching().getEnd())).isFalse();
        assertThat(testData.reference().relation(testData.insideStartTouching())).isEqualTo(PeriodRelation.InsideStartTouching);

        // enclosing start touching
        assertThat(testData.insideStartTouching().isReadonly()).isTrue();
        assertThat(testData.insideStartTouching().getStart().getMillis()).isEqualTo(start.getMillis());
        assertThat(testData.insideStartTouching().getEnd().getMillis()).isGreaterThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.insideStartTouching().getStart())).isTrue();
        assertThat(testData.reference().hasInside(testData.insideStartTouching().getEnd())).isFalse();
        assertThat(testData.reference().relation(testData.insideStartTouching())).isEqualTo(PeriodRelation.InsideStartTouching);

        // enclosing
        assertThat(testData.enclosing().isReadonly()).isTrue();
        assertThat(testData.enclosing().getStart().getMillis()).isGreaterThan(start.getMillis());
        assertThat(testData.enclosing().getEnd().getMillis()).isLessThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.enclosing().getStart())).isTrue();
        assertThat(testData.reference().hasInside(testData.enclosing().getEnd())).isTrue();
        assertThat(testData.reference().relation(testData.enclosing())).isEqualTo(PeriodRelation.Enclosing);

        // enclosing end touching
        assertThat(testData.enclosingEndTouching().isReadonly()).isTrue();
        assertThat(testData.enclosingEndTouching().getStart().getMillis()).isGreaterThan(start.getMillis());
        assertThat(testData.enclosingEndTouching().getEnd().getMillis()).isEqualTo(end.getMillis());

        assertThat(testData.reference().hasInside(testData.enclosingEndTouching().getStart())).isTrue();
        assertThat(testData.reference().hasInside(testData.enclosingEndTouching().getEnd())).isTrue();
        assertThat(testData.reference().relation(testData.enclosingEndTouching())).isEqualTo(PeriodRelation.EnclosingEndTouching);

        // exact match
        assertThat(testData.exactMatch().isReadonly()).isTrue();
        assertThat(testData.exactMatch().getStart().getMillis()).isEqualTo(start.getMillis());
        assertThat(testData.exactMatch().getEnd().getMillis()).isEqualTo(end.getMillis());

        assertThat(testData.reference().hasInside(testData.exactMatch().getStart())).isTrue();
        assertThat(testData.reference().hasInside(testData.exactMatch().getEnd())).isTrue();
        assertThat(testData.reference().relation(testData.exactMatch())).isEqualTo(PeriodRelation.ExactMatch);

        // inside
        assertThat(testData.inside().isReadonly()).isTrue();
        assertThat(testData.inside().getStart().getMillis()).isLessThan(start.getMillis());
        assertThat(testData.inside().getEnd().getMillis()).isGreaterThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.inside().getStart())).isFalse();
        assertThat(testData.reference().hasInside(testData.inside().getEnd())).isFalse();
        assertThat(testData.reference().relation(testData.inside())).isEqualTo(PeriodRelation.Inside);

        // inside end touching
        assertThat(testData.insideEndTouching().isReadonly()).isTrue();
        assertThat(testData.insideEndTouching().getStart().getMillis()).isLessThan(start.getMillis());
        assertThat(testData.insideEndTouching().getEnd().getMillis()).isEqualTo(end.getMillis());

        assertThat(testData.reference().hasInside(testData.insideEndTouching().getStart())).isFalse();
        assertThat(testData.reference().hasInside(testData.insideEndTouching().getEnd())).isTrue();
        assertThat(testData.reference().relation(testData.insideEndTouching())).isEqualTo(PeriodRelation.InsideEndTouching);

        // end inside
        assertThat(testData.getEndInside().isReadonly()).isTrue();
        assertThat(testData.getEndInside().getStart().getMillis()).isGreaterThan(start.getMillis());
        assertThat(testData.getEndInside().getStart().getMillis()).isLessThan(end.getMillis());
        assertThat(testData.getEndInside().getEnd().getMillis()).isGreaterThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.getEndInside().getStart())).isTrue();
        assertThat(testData.reference().hasInside(testData.getEndInside().getEnd())).isFalse();
        assertThat(testData.reference().relation(testData.getEndInside())).isEqualTo(PeriodRelation.EndInside);

        // end touching
        assertThat(testData.endTouching().isReadonly()).isTrue();
        assertThat(testData.endTouching().getStart().getMillis()).isEqualTo(end.getMillis());
        assertThat(testData.endTouching().getEnd().getMillis()).isGreaterThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.endTouching().getStart())).isTrue();
        assertThat(testData.reference().hasInside(testData.endTouching().getEnd())).isFalse();
        assertThat(testData.reference().relation(testData.endTouching())).isEqualTo(PeriodRelation.EndTouching);

        // before
        assertThat(testData.before().isReadonly()).isTrue();
        assertThat(testData.before().getStart().getMillis()).isGreaterThan(end.getMillis());
        assertThat(testData.before().getEnd().getMillis()).isGreaterThan(end.getMillis());

        assertThat(testData.reference().hasInside(testData.before().getStart())).isFalse();
        assertThat(testData.reference().hasInside(testData.before().getEnd())).isFalse();
        assertThat(testData.reference().relation(testData.before())).isEqualTo(PeriodRelation.Before);
    }

    @Test
    public void resetTest() {
        TimeRange range = new TimeRange(start, end);

        assertThat(range.getStart()).isEqualTo(start);
        assertThat(range.hasStart()).isTrue();
        assertThat(range.getEnd()).isEqualTo(end);
        assertThat(range.hasEnd()).isTrue();

        range.reset();

        assertThat(range.getStart()).isEqualTo(TimeSpec.MinPeriodTime);
        assertThat(range.hasStart()).isFalse();
        assertThat(range.getEnd()).isEqualTo(TimeSpec.MaxPeriodTime);
        assertThat(range.hasEnd()).isFalse();
    }

    @Test
    public void equalsTest() {
        TimeRange range1 = new TimeRange(start, end);
        TimeRange range2 = new TimeRange(start, end);
        TimeRange range3 = new TimeRange(start.plusMillis(-1), end.plusMillis(1));
        TimeRange range4 = new TimeRange(start, end, true);

        assertThat(range1).isEqualTo(range2);
        assertThat(range1).isNotEqualTo(range3);
        assertThat(range2).isEqualTo(range1);
        assertThat(range2).isNotEqualTo(range3);

        assertThat(range1).isNotEqualTo(range4);
    }
}
