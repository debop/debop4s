package kr.debop4s.time

import org.joda.time.{DateTime, ReadableInterval}
import scala.annotation.tailrec

/**
 * kr.debop4s.time.RichReadableInterval
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:02
 */
class RichReadableInterval(val self: ReadableInterval) extends AnyVal {

    def chronology = self.getChronology
    def start = self.getStart
    def end = self.getEnd

    def duration = self.toDuration
    def millis = self.toDuration.getMillis

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
