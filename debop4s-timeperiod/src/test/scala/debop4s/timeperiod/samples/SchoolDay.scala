package debop4s.timeperiod.samples

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.utils.Times
import debop4s.timeperiod.{TimeBlock, TimePeriodChain}
import org.joda.time.DateTime

import scala.beans.BeanProperty

/**
 * kr.hconnect.timeperiod.tests.samples.SchoolDay
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 8. 오후 11:34
 */
class SchoolDay(private[this] var _moment: DateTime) extends TimePeriodChain {

  def this() = this(Times.today.plusHours(8))

  private var moment = _moment

  @BeanProperty val lesson1 = new Lesson(moment)
  moment += lesson1.duration

  @BeanProperty val break1 = new ShortBreak(moment)
  moment += break1.duration

  @BeanProperty val lesson2 = new Lesson(moment)
  moment += lesson2.duration

  @BeanProperty val break2 = new LargeBreak(moment)
  moment += break2.duration

  @BeanProperty val lesson3 = new Lesson(moment)
  moment += lesson3.duration

  @BeanProperty val break3 = new ShortBreak(moment)
  moment += break3.duration

  @BeanProperty val lesson4 = new Lesson(moment)
  moment += lesson4.duration

  super.addAll(lesson1, break1, lesson2, break2, lesson3, break3, lesson4)
}

case class Lesson(private val moment: DateTime) extends TimeBlock(moment, moment + LessonDuration, false) {}

case class LargeBreak(private val moment: DateTime) extends TimeBlock(moment, moment + LargeBreakDuration, false) {}

case class ShortBreak(private val moment: DateTime) extends TimeBlock(moment, moment + ShortBreakDuration, false) {}