package debop4s.core.jodatime

import org.joda.time.{Chronology, DateTime, Duration, ReadableInterval}

import scala.annotation.tailrec


class JodaRichReadableInterval(val self: ReadableInterval) extends AnyVal {

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
