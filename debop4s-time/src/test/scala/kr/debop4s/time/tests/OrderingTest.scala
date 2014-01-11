package kr.debop4s.time.tests

import kr.debop4s.time._
import org.joda.time.{Duration, LocalDate, DateTime}
import org.junit.Test
import org.scalatest.junit.JUnitSuite

/**
 * kr.debop4s.time.tests.OrderingTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 7. 오후 3:00
 */
class OrderingTest extends JUnitSuite {

    @Test
    def sortDateTime() {
        val now = DateTime.now()
        val l = List(now, now + 3.second, now + 10.second, now + 1.second, now - 2.second)

        assert(l.sorted == List(now - 2.second, now, now + 1.second, now + 3.second, now + 10.second))
        assert(l.max == (now + 10.second))
    }

    @Test
    def sortLocalDate() {
        val today = LocalDate.now()
        val l = List(today + 1.day, today + 3.day, today + 10.day, today + 2.day)

        assert(l.sorted == List(today + 1.day, today + 2.day, today + 3.day, today + 10.day))
        assert(l.max == (today + 10.day))
    }

    @Test
    def sortDuration() {
        val list: List[Duration] = List(1.second, 5.seconds, 2.second, 4.second).map(_.toDuration)
        assert(list.sorted == List(1.second, 2.seconds, 4.second, 5.second).map(_.toDuration))
        assert(list.max == 5.second.toDuration)
    }

}
