package kr.debop4s.timeperiod

import kr.debop4s.core.Guard
import kr.debop4s.timeperiod.utils.Times
import org.joda.time.{Duration, DateTime}
import scala.collection.JavaConversions._

/**
 * kr.debop4s.timeperiod.TimePeriodChain
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 31. 오후 3:43
 */
@SerialVersionUID(1838724440389574448L)
trait ITimePeriodChain extends ITimePeriodContainer {

    def getFirst: ITimePeriod =
        if (size > 0) getPeriods.get(0) else null

    def getLast: ITimePeriod =
        if (size > 0) getPeriods.get(getPeriods.size - 1) else null

    override def set(index: Int, elem: ITimePeriod): ITimePeriod = {
        remove(index)
        add(index, elem)
        elem
    }

    override def add(period: ITimePeriod): Boolean = {
        Times.assertMutable(period)

        val last = getLast
        if (last != null) {
            assertSpaceAfter(last.getEnd, period.getDuration)
            period.setup(last.getEnd, last.getEnd.plus(period.getDuration))
        }
        log.trace(s"Period chain 끝에 period=[$period]를 추가합니다.")
        getPeriods.add(period)
    }

    def addAll(items: java.lang.Iterable[_ <: ITimePeriod]) {
        items.foreach(item => add(item))
    }

    /**
     * [[kr.debop4s.timeperiod.ITimePeriod]]의 Chain의 index 번째에 item을 삽입합니다. 선행 Period와 후행 Period의 기간 값이 조정됩니다.
     *
     * @param index 삽입할 순서
     * @param item  삽입할 요소
     */
    override def add(index: Int, item: ITimePeriod) {
        Times.assertMutable(item)
        log.trace(s"Chain의 인덱스[$index]에 새로운 요소[$item]를 삽입합니다...")

        val itemDuration: Duration = item.getDuration
        var prevItem: ITimePeriod = null
        var nextItem: ITimePeriod = null

        if (size > 0) {
            log.trace("시간적 삽입 공간이 존재하는지 검사합니다...")
            if (index > 0) {
                prevItem = get(index - 1)
                assertSpaceAfter(getEnd, itemDuration)
            }
            if (index < size - 1) {
                nextItem = get(index)
                assertSpaceBefore(getStart, itemDuration)
            }
        }

        getPeriods.add(index, item)

        if (prevItem != null) {
            log.trace("선행 period에 기초하여 삽입한 period와 후행 period들의 시간을 조정합니다...")
            item.setup(prevItem.getEnd, prevItem.getEnd.plus(itemDuration))
            (index + 1 until size).foreach(i => {
                val p: ITimePeriod = get(i)
                val startTime: DateTime = p.getStart.plus(itemDuration)
                p.setup(startTime, startTime.plus(p.getDuration))
            })
        }

        if (nextItem != null) {
            log.trace("후행 period에 기초하여 삽입한 period와 선행 period들의 시간을 조정합니다...")
            var nextStart: DateTime = nextItem.getStart.minus(itemDuration)
            item.setup(nextStart, nextStart.plus(itemDuration))

            (0 until index - 1).foreach(i => {
                val p: ITimePeriod = get(i)
                nextStart = p.getStart.minus(itemDuration)
                p.setup(nextStart, nextStart.plus(p.getDuration))
            })
        }
    }

    /** 지정한 요소를 제거하고, 후속 ITimePeriod 들의 기간을 재조정합니다. (앞으로 당깁니다) */
    override def remove(o: Any): Boolean = {
        assert(o != null)
        Guard.shouldBe(o.isInstanceOf[ITimePeriod], s"o is not ITimePeriod type. class=[${o.getClass}]")

        if (size <= 0) return false
        val item: ITimePeriod = o.asInstanceOf[ITimePeriod]
        log.trace(s"요소 [$item]를 컬렉션에서 제거합니다...")
        val itemDuration: Duration = item.getDuration
        val index: Int = indexOf(item)
        var next: ITimePeriod = null
        if (itemDuration.getMillis > 0 && index >= 0 && index < size - 1) next = get(index)
        val removed: Boolean = getPeriods.remove(item)
        if (removed && next != null) {
            log.trace(s"요소[$item]를 제거하고, chain의 후속 periods 들의 기간을 조정합니다...")

            for (x <- index until size) {
                val start = periods(x).getStart.minus(itemDuration)
                val duration = periods(x).getDuration
                periods(x).setup(start, start.plus(duration))
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

    def this(seq: ITimePeriod*) {
        this()
        getPeriods.addAll(seq)
    }

    def this(collection: Iterable[_ <: ITimePeriod]) {
        this()
        getPeriods.addAll(collection)
    }

    override def getStart: DateTime =
        if (getFirst != null) getFirst.getStart else MinPeriodTime

    override def getEnd: DateTime =
        if (getLast != null) getLast.getEnd else MaxPeriodTime

    override def setStart(x: DateTime) {
        move(new Duration(getStart, x))
    }

    override def setEnd(x: DateTime) {
        move(new Duration(getEnd, x))
    }
}
