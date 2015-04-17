package debop4s.timeperiod.timezone

import debop4s.timeperiod.tests.AbstractTimeFunSuite
import org.joda.time.DateTimeZone

import scala.collection.JavaConverters._

/**
 * TimeZonesFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class TimeZonesFunSuite extends AbstractTimeFunSuite {

  test("avaialbe timezones") {
    TimeZones.zoneAndOffsets.foreach { case (id, offset) =>
      log.debug(s"TimeZone id=$id, offset=$offset")
    }
  }

  test("같은 TimeZone 찾기") {
    val seoul = "Asia/Seoul"
    val timezoneIds = TimeZones.getSameOffsetTimeZoneIds(seoul)

    timezoneIds.contains(seoul) shouldEqual true
    timezoneIds.asScala.foreach { id => log.debug(s"TimeZone=$id") }

    val newyork = "US/Eastern" // America/New_York
    val timezoneIds2 = TimeZones.getSameOffsetTimeZoneIds(newyork)

    timezoneIds2.contains(newyork) shouldEqual true
    timezoneIds2.asScala.foreach { id => log.debug(s"TimeZone=$id") }
  }

  test("특정 Offset 값을 가지는 TimeZoneId 들") {
    val seoul = "Asia/Seoul"
    val timezoneIds = TimeZones.getTimeZoneIds(DateTimeZone.forID(seoul).getOffset(0))
    timezoneIds.asScala.foreach { id => log.debug(s"TimeZone=$id") }
  }

  test("offset 마다 TimeZoneId 들") {
    TimeZones.offsets.foreach { offset =>
      log.debug(s"offset=$offset")
      val timezoneIds = TimeZones.getTimeZoneIds(offset)
      timezoneIds.asScala.foreach { id => log.debug(s"\tTimeZone=$id") }
    }
  }

}
