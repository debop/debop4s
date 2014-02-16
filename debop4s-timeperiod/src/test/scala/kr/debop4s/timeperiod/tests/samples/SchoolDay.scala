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
class SchoolDay(private val moment: DateTime) extends TimePeriodChain {

  def this() {
    this(Times.today + 8.hour)
  }

  val lesson1 = new Lesson(moment)
  val break1 = new ShortBreak(moment)
  val lesson2 = new Lesson(moment)
  val break2 = new LargeBreak(moment)
  val lesson3 = new Lesson(moment)
  val break3 = new ShortBreak(moment)
  val lesson4 = new Lesson(moment)

  super.addAll(lesson1, break1, lesson2, break2, lesson3, break3, lesson4)
}


class Lesson(private val moment: DateTime) extends TimeBlock(moment, moment + LessonDuration) {}

class LargeBreak(private val moment: DateTime) extends TimeBlock(moment, moment + LargeBreakDuration) {}

class ShortBreak(private val moment: DateTime) extends TimeBlock(moment, moment + ShortBreakDuration) {}