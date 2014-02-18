package kr.debop4s.timeperiod.tests.samples

import kr.debop4s.timeperiod._
import kr.debop4s.timeperiod.utils.Times
import kr.debop4s.timeperiod.{TimeBlock, TimePeriodChain}
import org.joda.time.DateTime

/**
 * kr.debop4s.timeperiod.tests.samples.SchoolDay
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:34
 */
class SchoolDay(private var _moment: DateTime) extends TimePeriodChain {

    def this() {
        this(Times.today + 8.hour)
    }

    private var moment = _moment
    val lesson1 = new Lesson(moment)
    moment = moment.plus(lesson1.getDuration)

    val break1 = new ShortBreak(moment)
    moment = moment.plus(break1.getDuration)

    val lesson2 = new Lesson(moment)
    moment = moment.plus(lesson2.getDuration)

    val break2 = new LargeBreak(moment)
    moment = moment.plus(break2.getDuration)

    val lesson3 = new Lesson(moment)
    moment = moment.plus(lesson3.getDuration)

    val break3 = new ShortBreak(moment)
    moment = moment.plus(break3.getDuration)

    val lesson4 = new Lesson(moment)
    moment = moment.plus(lesson4.getDuration)

    super.addAll(lesson1, break1, lesson2, break2, lesson3, break3, lesson4)
}


class Lesson(private val moment: DateTime) extends TimeBlock(moment, moment + LessonDuration) {
    log.trace(s"Create Lesson. moment=$moment")
}

class LargeBreak(private val moment: DateTime) extends TimeBlock(moment, moment + LargeBreakDuration) {
    log.trace(s"Create LargeBreak. moment=$moment")
}

class ShortBreak(private val moment: DateTime) extends TimeBlock(moment, moment + ShortBreakDuration) {
    log.trace(s"Create ShortBreak. moment=$moment")
}