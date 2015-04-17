package debop4s.timeperiod.timezone

import java.util.{List => JList, Map => JMap, Set => JSet}

import org.joda.time.DateTimeZone

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * TimeZones
 * @author sunghyouk.bae@gmail.com
 */
object TimeZones {

  lazy val zoneIds: mutable.Set[String] = DateTimeZone.getAvailableIDs.asScala

  lazy val zoneAndOffsets: Map[String, Int] = {
    zoneIds.map { id =>
      (id, DateTimeZone.forID(id).getOffset(0))
    }.toMap
  }

  lazy val offsets = zoneAndOffsets.map { x => x._2 }.toList.distinct.sorted

  /** 해당 offset 값을 가지는 TimeZone을 가져온다 */
  def getTimeZoneIds(offset: Int): JSet[String] = {
    zoneAndOffsets
    .filter { case (id, o) => o == offset }
    .map { x => x._1 }
    .toSet
    .asJava
  }

  /**
   * 해당 TimeZone과 같은 offset 을 가지는 (같은 시간대인) TimeZone Id들의 정보를 가져온다.
   * @param timezoneId  TimeZone Id
   * @return 같은 시간대의 TimeZone Id
   */
  def getSameOffsetTimeZoneIds(timezoneId: String): JSet[String] = {
    val timezone = DateTimeZone.forID(timezoneId)
    getTimeZoneIds(timezone.getOffset(0))
  }
}
