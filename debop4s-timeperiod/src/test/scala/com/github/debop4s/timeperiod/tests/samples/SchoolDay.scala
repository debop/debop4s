package com.github.debop4s.timeperiod.tests.samples

import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.Times
import com.github.debop4s.timeperiod.{TimeBlock, TimePeriodChain}
import org.joda.time.DateTime

/**
 * com.github.debop4s.timeperiod.tests.samples.SchoolDay
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:34
 */
class SchoolDay(private var _moment: DateTime) extends TimePeriodChain {

  def this() {
    this(Times.today + 8.hour)
  }

  private var moment = _moment
  val lesson1 = new Lesson(moment)
  moment += lesson1.duration

  val break1 = new ShortBreak(moment)
  moment += break1.duration

  val lesson2 = new Lesson(moment)
  moment += lesson2.duration

  val break2 = new LargeBreak(moment)
  moment += break2.duration

  val lesson3 = new Lesson(moment)
  moment += lesson3.duration

  val break3 = new ShortBreak(moment)
  moment += break3.duration

  val lesson4 = new Lesson(moment)
  moment += lesson4.duration

  super.addAll(lesson1, break1, lesson2, break2, lesson3, break3, lesson4)
}


class Lesson(private val moment: DateTime) extends TimeBlock(moment, moment + LessonDuration, false) {
  log.trace(s"Create Lesson. moment=$moment")
}

class LargeBreak(private val moment: DateTime) extends TimeBlock(moment, moment + LargeBreakDuration, false) {
  log.trace(s"Create LargeBreak. moment=$moment")
}

class ShortBreak(private val moment: DateTime) extends TimeBlock(moment, moment + ShortBreakDuration, false) {
  log.trace(s"Create ShortBreak. moment=$moment")
}