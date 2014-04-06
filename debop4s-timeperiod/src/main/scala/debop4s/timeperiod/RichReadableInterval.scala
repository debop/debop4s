package debop4s.timeperiod

import org.joda.time.{Duration, Chronology, DateTime, ReadableInterval}
import scala.annotation.tailrec

/**
 * com.github.time.RichReadableInterval
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:02
 */
class RichReadableInterval(val self: ReadableInterval) extends AnyVal {

  def chronology: Chronology = self.getChronology

  def start: DateTime = self.getStart

  def end: DateTime = self.getEnd

  def duration: Duration = self.toDuration

  def millis: Long = self.toDuration.getMillis

  def days: List[DateTime] = {
    val from = start
    val to = end

    @tailrec
    def recur(acc: List[DateTime], curr: DateTime, target: DateTime): List[DateTime] = {
      if (curr.withTimeAtStartOfDay() == target.withTimeAtStartOfDay()) acc
      else recur(acc :+ curr, curr.plusDays(1), target)
    }

    recur(List(), from, to)
  }
}
