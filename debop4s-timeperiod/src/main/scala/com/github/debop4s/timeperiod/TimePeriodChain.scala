package com.github.debop4s.timeperiod

import com.github.debop4s.core.Guard
import com.github.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}


/**
 * com.github.debop4s.timeperiod.TimePeriodChain
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 31. 오후 3:43
 */
@SerialVersionUID(1838724440389574448L)
trait ITimePeriodChain extends ITimePeriodContainer {

    override def head: ITimePeriod =
        if (size > 0) periods.head else null

    override def last: ITimePeriod =
        if (size > 0) periods.last else null

    override def set(index: Int, elem: ITimePeriod): ITimePeriod = {
        remove(index)
        add(index, elem)
        elem
    }

    override def add(period: ITimePeriod) {
        Times.assertMutable(period)

        val alast = last
        if (alast != null) {
            assertSpaceAfter(alast.end, period.duration)
            period.setup(alast.end, alast.end + period.duration)
        }
        log.trace(s"Period chain 끝에 period=[$period]를 추가합니다.")
        periods += period
    }


    /**
     * [[com.github.debop4s.timeperiod.ITimePeriod]]의 Chain의 index 번째에 item을 삽입합니다. 선행 Period와 후행 Period의 기간 값이 조정됩니다.
     *
     * @param index 삽입할 순서
     * @param item  삽입할 요소
     */
    def add(index: Int, item: ITimePeriod) {
        Times.assertMutable(item)
        log.trace(s"Chain의 인덱스[$index]에 새로운 요소[$item]를 삽입합니다...")

        val itemDuration = item.duration
        var prevItem: ITimePeriod = null
        var nextItem: ITimePeriod = null

        if (size > 0) {
            log.trace("시간적 삽입 공간이 존재하는지 검사합니다...")
            if (index > 0) {
                prevItem = get(index - 1)
                assertSpaceAfter(end, itemDuration)
            }
            if (index < size - 1) {
                nextItem = get(index)
                assertSpaceBefore(start, itemDuration)
            }
        }

        periods.insert(index, item)

        if (prevItem != null) {
            log.trace("선행 period에 기초하여 삽입한 period와 후행 period들의 시간을 조정합니다...")
            item.setup(prevItem.end, prevItem.end.plus(itemDuration))
            (index + 1 until size).foreach(i => {
                val p: ITimePeriod = get(i)
                val startTime: DateTime = p.start + itemDuration
                p.setup(startTime, startTime + p.duration)
            })
        }

        if (nextItem != null) {
            log.trace("후행 period에 기초하여 삽입한 period와 선행 period들의 시간을 조정합니다...")
            var nextStart: DateTime = nextItem.start.minus(itemDuration)
            item.setup(nextStart, nextStart + itemDuration)

            (0 until index - 1).foreach(i => {
                val p: ITimePeriod = get(i)
                nextStart = p.start - itemDuration
                p.setup(nextStart, nextStart + p.duration)
            })
        }
    }

    /** 지정한 요소를 제거하고, 후속 ITimePeriod 들의 기간을 재조정합니다. (앞으로 당깁니다) */
    override def remove(o: Any): Boolean = {
        assert(o != null)
        Guard.shouldBe(o.isInstanceOf[ITimePeriod], s"o is not ITimePeriod type. class=[${o.getClass}]")

        if (size <= 0) return false
        val item = o.asInstanceOf[ITimePeriod]

        log.trace(s"요소 [$item]를 컬렉션에서 제거합니다...")

        val itemDuration = item.duration
        val index: Int = indexOf(item)
        var next: ITimePeriod = null
        if (itemDuration.getMillis > 0 && index >= 0 && index < size - 1) next = get(index)

        var removed = false
        if (periods contains item) {
            periods -= item
            removed = true
        }

        if (removed && next != null) {
            log.trace(s"요소[$item]를 제거하고, chain의 후속 periods 들의 기간을 조정합니다...")

            for (x <- index until size) {
                val start = periods(x).start.minus(itemDuration)
                val duration = periods(x).duration
                periods(x).setup(start, start + duration)
            }
        }
        log.trace(s"요소[$item]를 제거했습니다. removed=[$removed]")
        removed
    }

    /** 지정한 요소를 제거하고, 후속 ITimePeriod 들의 기간을 재조정합니다. (앞으로 당깁니다) */
    override def remove(index: Int): ITimePeriod =
        periods.remove(index)

    protected def assertSpaceBefore(moment: DateTime, duration: Duration) {
        var hasSpace = moment != MinPeriodTime
        if (hasSpace) {
            val remaining = new Duration(MinPeriodTime, moment)
            hasSpace = duration.compareTo(remaining) <= 0
        }
        Guard.shouldBe(hasSpace, s"duration[$duration] is out of range.")
    }

    protected def assertSpaceAfter(moment: DateTime, duration: Duration) {
        var hasSpace = moment != MaxPeriodTime
        if (hasSpace) {
            val remaining = new Duration(moment, MaxPeriodTime)
            hasSpace = duration.compareTo(remaining) <= 0
        }
        Guard.shouldBe(hasSpace, s"duration[$duration] is out of range.")
    }
}


@SerialVersionUID(-5838724440389574448L)
class TimePeriodChain extends TimePeriodContainer with ITimePeriodChain {

    def this(elems: ITimePeriod*) {
        this()
        addAll(elems)
    }

    def this(elems: Iterable[ITimePeriod]) {
        this()
        addAll(elems)
    }

    override def start: DateTime =
        if (head != null) head.start else MinPeriodTime

    override def end: DateTime =
        if (last != null) last.end else MaxPeriodTime


    override def start_=(x: DateTime) {
        move(new Duration(start, x))
    }

    override def end_=(x: DateTime) {
        move(new Duration(end, x))
    }
}
