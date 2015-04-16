package debop4s.core.jodatime

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.jodatime._
import org.joda.time._

class OrderingFunSuite extends AbstractCoreFunSuite {

  test("sort DateTime") {
    val now = DateTime.now()
    val l = Seq(now, now + 3.second, now + 10.second, now + 1.second, now - 2.second)

    l.sorted shouldEqual Seq(now - 2.second, now, now + 1.second, now + 3.second, now + 10.second)
    l.max shouldEqual (now + 10.second)
  }

  test("sort LocalDate") {
    val today = LocalDate.now()
    val l = Seq(today + 1.day, today + 3.day, today + 10.day, today + 2.day)

    l.sorted shouldEqual Seq(today + 1.day, today + 2.day, today + 3.day, today + 10.day)
    l.max shouldEqual (today + 10.day)
  }

  test("sort Duration") {
    val list = Seq(1.second, 5.seconds, 2.second, 4.second).map(_.toDuration)
    list.sorted shouldEqual Seq(1.second, 2.seconds, 4.second, 5.second).map(_.toDuration)
    list.max shouldEqual 5.second.toDuration
  }

}
