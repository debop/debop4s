package debop4s.timeperiod.tests.hierarchy

import debop4s.core.jodatime._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime
import org.scalatest.{FunSuite, Matchers}

/**
 * debop4s.timeperiod.tests.hierarchy.HierarchyTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 2. 오전 11:12
 */
class HierarchyTest extends FunSuite with Matchers {

    test("protected value accessing") {
        val range = new Range(Times.now, Times.now.plusDays(1), false)
        range.start = Times.today

        range.start should not be eq(Times.now)
        range.start shouldBe Times.today
        assert(range.end > range.start)
        range.end should be > range.start
    }
}

abstract class Period(private[this] var _start: DateTime,
                      private[this] var _end: DateTime,
                      private[this] var _readonly: Boolean) {

    def start = _start

    protected def start_=(v: DateTime) = {
        _start = v
    }

    def getStart = _start

    protected def setStart(v: DateTime) = {
        _start = v
    }

    def end = _end

    protected def end_=(v: DateTime) = {
        _end = v
    }

    def readonly = _readonly

    protected def readonly_=(v: Boolean) = {
        _readonly = v
    }
}

class Range(_start: DateTime, _end: DateTime, _readonly: Boolean = false) extends Period(_start, _end, _readonly) {

    override def start_=(v: DateTime) = {
        super.start_=(v)
    }

    override def setStart(v: DateTime) = {
        super.start_$eq(v)
    }

    override def end_=(v: DateTime) = {
        super.end_=(v)
    }

    override def readonly_=(v: Boolean) = {
        super.readonly_=(v)
    }
}


