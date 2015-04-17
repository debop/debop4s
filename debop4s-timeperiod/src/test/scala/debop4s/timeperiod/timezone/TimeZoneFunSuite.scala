package debop4s.timeperiod.timezone

import java.sql.Timestamp
import java.util.{Calendar, TimeZone}

import debop4s.core.conversions.jodatime._
import debop4s.timeperiod.tests.AbstractTimeFunSuite
import debop4s.timeperiod.utils.Times
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * TimeZoneFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class TimeZoneFunSuite extends AbstractTimeFunSuite {

  test("Local Time vs UTC") {
    val localTime = DateTime.now(DateTimeZone.getDefault)
    val utcTime = localTime.toDateTime(DateTimeZone.UTC)
    val isoTime = localTime.toDateTimeISO

    log.debug(s"localTime=$localTime (${ localTime.getMillis }), " +
              s"utcTime=$utcTime(${ utcTime.getMillis }), " +
              s"isoTime=$isoTime(${ isoTime.getMillis })")

    localTime should not be utcTime
    localTime.getMillis shouldEqual utcTime.getMillis
    localTime.toDateTimeISO should not be utcTime.toDateTimeISO
  }

  test("UTC, Seoul, Newyork") {
    val utc = Times.asDateTime(2014, 10, 14, 16, 10, 20, 444).toDateTime(DateTimeZone.UTC)
    val seoul = utc.toDateTime(DateTimeZone.forID("Asia/Seoul"))
    val newyork = utc.toDateTime(DateTimeZone.forID("EST")) // America/New_York

    println(s"utc=$utc, ${ utc.getMillis }")
    println(s"default=$seoul, ${ seoul.getMillis }")
    println(s"newyork=$newyork, ${ newyork.getMillis }")


    val timestamp = new Timestamp(seoul.getMillis)

    val seoul2 = new DateTime(timestamp)
    val utc2 = seoul2.toDateTime(DateTimeZone.UTC)

    seoul2 shouldEqual seoul
    utc2 shouldEqual utc
  }

  test("DateTime 과 Timestamp 값 비교") {

    val localDt = new DateTime(2014, 10, 14, 14, 30, 40)
    val utcDt = localDt.asUtc()
    val estDt = localDt.asLocal(DateTimeZone.forID("EST"))

    val localMillis = localDt.getMillis
    val utcMillis = utcDt.getMillis
    val estMillis = estDt.getMillis

    val localTs = new Timestamp(localMillis)
    val utcTs = new Timestamp(utcMillis)
    val estTs = new Timestamp(estMillis)

    // DateTime 끼리는 다르다 ( TimeZone 정보도 가지고 있기 때문에 )
    localDt should not be utcDt
    localDt should not be estDt
    utcDt should not be estDt

    // Millis 와 Timestamp 값은 Long 값만을 가지므로, UTC Time 이라고 보면 된다.
    localMillis shouldEqual utcMillis
    localMillis shouldEqual estMillis

    localTs shouldEqual utcTs
    localTs shouldEqual estTs

    val localDt2 = new DateTime(localTs)
    localDt2 shouldEqual localDt
  }

  // Date 수형은 결국 현재 Locale 로 표현하므로, local date 와 utc date 의 Timestamp 값이 같다면, equal 이 true 이다.
  test("Date 의 Timestamp 변경") {

    val dt = new DateTime(2014, 10, 14, 14, 30, 40)
    val dtUtc = dt.asUtc()
    val localDt = dt.toDate

    val utc = TimeZone.getTimeZone("UTC")
    val calendar = Calendar.getInstance(utc)
    calendar.setTimeInMillis(dtUtc.getMillis)
    val utcDt = calendar.getTime

    val localTime = localDt.getTime
    val utcTime = utcDt.getTime

    log.debug(s"GMT localDt=${ localDt.toGMTString }, utcDt=${ utcDt.toGMTString }")
    log.debug(s"LOCAL localDt=${ localDt.toLocaleString }, utcDt=${ utcDt.toLocaleString }")
    log.debug(s"localDt=${ localDt.toString }, utcDt=${ utcDt.toString }")

    localDt shouldEqual utcDt
    localDt.toGMTString shouldEqual utcDt.toGMTString
    localTime shouldEqual utcTime
  }

  test("avaiable TimeZone") {
    // 최대 길이는 : America/Argentina/ComodRivadavia (32)
    val maxTimeZone = DateTimeZone.getAvailableIDs.asScala.toSeq.maxBy(id => id.length)
    println(s"maxTimeZone=$maxTimeZone, length=${ maxTimeZone.length }")

    val timeZoneIds = DateTimeZone.getAvailableIDs.asScala.toSeq
    val timeZoneIdOffsets = new mutable.HashMap[String, Long]()

    timeZoneIds.foreach { id =>
      val timezone = DateTimeZone.forID(id)
      val offset = timezone.getOffset(0)
      timeZoneIdOffsets.put(id, offset)


      log.debug(s"Timezone id=$id, offset=$offset millis, shortName=${ timezone.getShortName(0) }")
    }
  }

}
