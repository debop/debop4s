package debop4s.data.orm.mapping.usertype.jodatime

import javax.persistence._

import debop4s.core.ToStringHelper
import debop4s.core.conversions.jodatime._
import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.IntEntity
import debop4s.timeperiod.{ITimePeriod, TimeRange}
import org.hibernate.annotations.Columns
import org.hibernate.{annotations => hba}
import org.joda.time.{DateTime, DateTimeZone}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Transactional
class JodaTimeJUnitSuite extends AbstractJpaJUnitSuite {

  @Autowired val em: EntityManager = null

  @Test
  @Rollback(false)
  def testDateTimeAsString(): Unit = {

    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))

    val entity = new DateTimeAsStringEntity()
    entity.isoFormatHMS = seoul
    entity.isoFormatString = seoul

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimestampAndTimeZoneEntity], entity.getId)

    val newyork = seoul.asLocal(DateTimeZone.forID("EST"))
    val entity2 = new DateTimeAsStringEntity()
    entity2.isoFormatHMS = newyork
    entity2.isoFormatString = newyork

    em.persist(entity2)
    em.flush()
    em.clear()

    val loaded2 = em.find(classOf[TimestampAndTimeZoneEntity], entity2.getId)
  }

  @Test
  @Rollback(false)
  def testMillisAndTimeZone(): Unit = {
    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))
    val entity = new MillisAndTimeZoneEntity()
    entity.millisAndZone = seoul
    entity.timeStr = seoul
    entity.millisAndZoneAndStr = seoul

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimestampAndTimeZoneEntity], entity.getId)

    val newyork = seoul.asLocal(DateTimeZone.forID("EST"))
    val entity2 = new MillisAndTimeZoneEntity()
    entity2.millisAndZone = newyork
    entity2.timeStr = newyork
    entity2.millisAndZoneAndStr = newyork

    em.persist(entity2)
    em.flush()
    em.clear()

    val loaded2 = em.find(classOf[TimestampAndTimeZoneEntity], entity2.getId)
  }

  @Test
  @Rollback(false)
  def testMillisAndTimeZoneAndString(): Unit = {
    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))
    val entity = new MillisAndTimeZoneEntity()
    entity.millisAndZone = seoul
    entity.timeStr = seoul
    entity.millisAndZoneAndStr = seoul

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimestampAndTimeZoneEntity], entity.getId)

    val newyork = seoul.asLocal(DateTimeZone.forID("EST"))
    val entity2 = new MillisAndTimeZoneEntity()
    entity2.millisAndZone = newyork
    entity2.timeStr = newyork
    entity2.millisAndZoneAndStr = newyork

    em.persist(entity2)
    em.flush()
    em.clear()

    val loaded2 = em.find(classOf[TimestampAndTimeZoneEntity], entity2.getId)
  }

  @Test
  @Rollback(false)
  def testTimestampAndTimeZone(): Unit = {

    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))
    val entity = new TimestampAndTimeZoneEntity()
    entity.timeAndZone = seoul
    entity.timeStr = seoul
    entity.timeAndZoneAndStr = seoul

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimestampAndTimeZoneEntity], entity.getId)

    val newyork = seoul.asLocal(DateTimeZone.forID("EST"))
    val entity2 = new TimestampAndTimeZoneEntity()
    entity2.timeAndZone = newyork
    entity2.timeStr = newyork
    entity2.timeAndZoneAndStr = newyork

    em.persist(entity2)
    em.flush()
    em.clear()

    val loaded2 = em.find(classOf[TimestampAndTimeZoneEntity], entity2.getId)
  }


  @Test
  @Rollback(false)
  def testTimeRangeAsMillisAdnTimeZone(): Unit = {
    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))

    val entity = new TimeRangeAsMillisAndTimeZoneEntity()
    entity.range = TimeRange(seoul, seoul.plusDays(1))

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneEntity], entity.getId)

    loaded should not be null
    loaded.range.hasPeriod shouldEqual true

    val emptyEntity = new TimeRangeAsMillisAndTimeZoneEntity()
    emptyEntity.range = null
    em.persist(emptyEntity)
    em.flush()
    em.clear()
    val emptyLoaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneEntity], emptyEntity.getId)
    emptyLoaded should not be null
    // null 은 아니다
    emptyLoaded.range.hasPeriod shouldEqual false

    val startEntity = new TimeRangeAsMillisAndTimeZoneEntity()
    startEntity.range = TimeRange(seoul, null.asInstanceOf[DateTime])
    em.persist(startEntity)
    em.flush()
    em.clear()
    val startLoaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneEntity], startEntity.getId)
    startLoaded should not be null
    log.debug(s"startLoaded range=${ startLoaded.range }")
    startLoaded.range.hasStart shouldEqual true
    startLoaded.range.hasEnd shouldEqual false

    val endEntity = new TimeRangeAsMillisAndTimeZoneEntity()
    endEntity.range = TimeRange(null.asInstanceOf[DateTime], seoul)
    em.persist(endEntity)
    em.flush()
    em.clear()
    val endLoaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneEntity], endEntity.getId)
    endLoaded should not be null
    log.debug(s"startLoaded range=${ endLoaded.range }")
    endLoaded.range.hasStart shouldEqual false
    endLoaded.range.hasEnd shouldEqual true
  }

  @Test
  @Rollback(false)
  def testTimeRangeAsMillisAdnTimeZoneAndString(): Unit = {
    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))

    val entity = new TimeRangeAsMillisAndTimeZoneAndStringEntity()
    entity.range = TimeRange(seoul, seoul.plusDays(1))

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneAndStringEntity], entity.getId)

    loaded should not be null
    loaded.range.hasPeriod shouldEqual true

    val emptyEntity = new TimeRangeAsMillisAndTimeZoneAndStringEntity()
    emptyEntity.range = null
    em.persist(emptyEntity)
    em.flush()
    em.clear()
    val emptyLoaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneAndStringEntity], emptyEntity.getId)
    emptyLoaded should not be null
    // null 은 아니다
    emptyLoaded.range.hasPeriod shouldEqual false

    val startEntity = new TimeRangeAsMillisAndTimeZoneAndStringEntity()
    startEntity.range = TimeRange(seoul, null.asInstanceOf[DateTime])
    em.persist(startEntity)
    em.flush()
    em.clear()
    val startLoaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneAndStringEntity], startEntity.getId)
    startLoaded should not be null
    log.debug(s"startLoaded range=${ startLoaded.range }")
    startLoaded.range.hasStart shouldEqual true
    startLoaded.range.hasEnd shouldEqual false

    val endEntity = new TimeRangeAsMillisAndTimeZoneAndStringEntity()
    endEntity.range = TimeRange(null.asInstanceOf[DateTime], seoul)
    em.persist(endEntity)
    em.flush()
    em.clear()
    val endLoaded = em.find(classOf[TimeRangeAsMillisAndTimeZoneAndStringEntity], endEntity.getId)
    endLoaded should not be null
    log.debug(s"startLoaded range=${ endLoaded.range }")
    endLoaded.range.hasStart shouldEqual false
    endLoaded.range.hasEnd shouldEqual true
  }

  @Test
  @Rollback(false)
  def testTimeRangeAsTimestampAdnTimeZone(): Unit = {
    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))

    val entity = new TimeRangeTimestampAndTimeZoneEntity()
    entity.range = TimeRange(seoul, seoul.plusDays(1))

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimeRangeTimestampAndTimeZoneEntity], entity.getId)

    loaded should not be null
    loaded.range.hasPeriod shouldEqual true

    val emptyEntity = new TimeRangeTimestampAndTimeZoneEntity()
    emptyEntity.range = null
    em.persist(emptyEntity)
    em.flush()
    em.clear()
    val emptyLoaded = em.find(classOf[TimeRangeTimestampAndTimeZoneEntity], emptyEntity.getId)
    emptyLoaded should not be null
    // null 은 아니다
    emptyLoaded.range.hasPeriod shouldEqual false

    val startEntity = new TimeRangeTimestampAndTimeZoneEntity()
    startEntity.range = TimeRange(seoul, null.asInstanceOf[DateTime])
    em.persist(startEntity)
    em.flush()
    em.clear()
    val startLoaded = em.find(classOf[TimeRangeTimestampAndTimeZoneEntity], startEntity.getId)
    startLoaded should not be null
    log.debug(s"startLoaded range=${ startLoaded.range }")
    startLoaded.range.hasStart shouldEqual true
    startLoaded.range.hasEnd shouldEqual false

    val endEntity = new TimeRangeTimestampAndTimeZoneEntity()
    endEntity.range = TimeRange(null.asInstanceOf[DateTime], seoul)
    em.persist(endEntity)
    em.flush()
    em.clear()
    val endLoaded = em.find(classOf[TimeRangeTimestampAndTimeZoneEntity], endEntity.getId)
    endLoaded should not be null
    log.debug(s"startLoaded range=${ endLoaded.range }")
    endLoaded.range.hasStart shouldEqual false
    endLoaded.range.hasEnd shouldEqual true
  }

  @Test
  @Rollback(false)
  def testTimeRangeAsTimestampAdnTimeZoneAndString(): Unit = {
    val seoul = new DateTime(2014, 10, 14, 17, 45, 56, 333, DateTimeZone.forID("Asia/Seoul"))

    val entity = new TimeRangeTimestampAndTimeZoneAndStringEntity()
    entity.range = TimeRange(seoul, seoul.plusDays(1))

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[TimeRangeTimestampAndTimeZoneAndStringEntity], entity.getId)

    loaded should not be null
    loaded.range.hasPeriod shouldEqual true

    val emptyEntity = new TimeRangeTimestampAndTimeZoneAndStringEntity()
    emptyEntity.range = null
    em.persist(emptyEntity)
    em.flush()
    em.clear()
    val emptyLoaded = em.find(classOf[TimeRangeTimestampAndTimeZoneAndStringEntity], emptyEntity.getId)
    emptyLoaded should not be null
    // null 은 아니다
    emptyLoaded.range.hasPeriod shouldEqual false

    val startEntity = new TimeRangeTimestampAndTimeZoneAndStringEntity()
    startEntity.range = TimeRange(seoul, null.asInstanceOf[DateTime])
    em.persist(startEntity)
    em.flush()
    em.clear()
    val startLoaded = em.find(classOf[TimeRangeTimestampAndTimeZoneAndStringEntity], startEntity.getId)
    startLoaded should not be null
    log.debug(s"startLoaded range=${ startLoaded.range }")
    startLoaded.range.hasStart shouldEqual true
    startLoaded.range.hasEnd shouldEqual false

    val endEntity = new TimeRangeTimestampAndTimeZoneAndStringEntity()
    endEntity.range = TimeRange(null.asInstanceOf[DateTime], seoul)
    em.persist(endEntity)
    em.flush()
    em.clear()
    val endLoaded = em.find(classOf[TimeRangeTimestampAndTimeZoneAndStringEntity], endEntity.getId)
    endLoaded should not be null
    log.debug(s"startLoaded range=${ endLoaded.range }")
    endLoaded.range.hasStart shouldEqual false
    endLoaded.range.hasEnd shouldEqual true
  }

  @Test
  @Rollback(false)
  def testTimeZoneAsIdAndOffsetEntity(): Unit = {
    val entity = new TimeZoneAsIdAndOffsetEntity()
    entity.timezone = DateTimeZone.forID("Asia/Seoul")
    em.persist(entity)
    em.flush()
    em.clear()
    var loaded = em.find(classOf[TimeZoneAsIdAndOffsetEntity], entity.getId)
    loaded should not be null
    loaded.timezone should not be null
    loaded.timezone.getID shouldEqual "Asia/Seoul"
  }
}

@Entity
class DateTimeAsStringEntity extends IntEntity {

  // YYYY-MM-DD'T'hh:mm:ss
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.DateTimeAsIsoFormatHMS")
  var isoFormatHMS: DateTime = _

  // YYYY-MM-DD'T'hh:mm:ss.SSS+00:00
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.DateTimeAsIsoFormatString")
  var isoFormatString: DateTime = _
}

@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class MillisAndTimeZoneEntity extends IntEntity {

  @Columns(columns = Array(new Column(name = "tzMillis"), new Column(name = "tzZoneId", length = 32)))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.MillisAndTimeZone")
  var millisAndZone: DateTime = _

  @Column(name = "timeAsStr", length = 30)
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.DateTimeAsIsoFormatString")
  var timeStr: DateTime = _


  @Columns(columns = Array(new Column(name = "tzsMillis"), new Column(name = "tzsZoneId", length = 32), new Column(name = "tzsStr", length = 32)))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.MillisAndTimeZoneAndString")
  var millisAndZoneAndStr: DateTime = _

  override def hashCode(): Int = Hashs.compute(millisAndZone, timeStr, millisAndZoneAndStr)

  override def buildStringHelper(): ToStringHelper =
    super.buildStringHelper()
    .add("millisAndZone", millisAndZone)
    .add("timeStr", timeStr)
    .add("millisAndZoneAndStr", millisAndZoneAndStr)
}

@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class TimestampAndTimeZoneEntity extends IntEntity {

  @Columns(columns = Array(new Column(name = "tzTime"), new Column(name = "tzZoneId", length = 32)))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZone")
  var timeAndZone: DateTime = _

  @Column(name = "timeAsStr", length = 30)
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.DateTimeAsIsoFormatString")
  var timeStr: DateTime = _


  @Columns(columns = Array(new Column(name = "tzsTime"), new Column(name = "tzsZoneId", length = 32), new Column(name = "tzsStr", length = 32)))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZoneAndString")
  var timeAndZoneAndStr: DateTime = _

  override def hashCode(): Int = Hashs.compute(timeAndZone, timeStr, timeAndZoneAndStr)

  override def buildStringHelper(): ToStringHelper =
    super.buildStringHelper()
    .add("timeAndZone", timeAndZone)
    .add("timeStr", timeStr)
    .add("timeAndZoneAndStr", timeAndZoneAndStr)
}


@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class TimeRangeAsMillisAndTimeZoneEntity extends IntEntity {

  @Columns(columns = Array(new Column(name = "startTimestamp"),
    new Column(name = "startTimeZoneId"),
    new Column(name = "endTimestamp"),
    new Column(name = "endTimeZoneId")))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeRangeAsMillisAndTimeZone")
  var range: ITimePeriod = _
}

@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class TimeRangeAsMillisAndTimeZoneAndStringEntity extends IntEntity {

  @Columns(columns = Array(new Column(name = "startTimestamp"),
    new Column(name = "startTimeZoneId"),
    new Column(name = "startTimeText"),
    new Column(name = "endTimestamp"),
    new Column(name = "endTimeZoneId"),
    new Column(name = "endTimeText")))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeRangeAsMillisAndTimeZoneAndString")
  var range: ITimePeriod = _
}

@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class TimeRangeTimestampAndTimeZoneEntity extends IntEntity {

  @Columns(columns = Array(new Column(name = "startTimestamp"),
    new Column(name = "startTimeZoneId"),
    new Column(name = "endTimestamp"),
    new Column(name = "endTimeZoneId")))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeRangeAsTimestampAndTimeZone")
  var range: ITimePeriod = _
}

@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class TimeRangeTimestampAndTimeZoneAndStringEntity extends IntEntity {

  @Columns(columns = Array(new Column(name = "startTimestamp"),
    new Column(name = "startTimeZoneId"),
    new Column(name = "startTimeText"),
    new Column(name = "endTimestamp"),
    new Column(name = "endTimeZoneId"),
    new Column(name = "endTimeText")))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeRangeAsTimestampAndTimeZoneAndString")
  var range: ITimePeriod = _
}

@Entity
@hba.DynamicInsert
@hba.DynamicUpdate
class TimeZoneAsIdAndOffsetEntity extends IntEntity {

  @Columns(columns = Array(new Column(name = "zoneId"), new Column(name = "zoneOffset")))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimeZoneAsIdAndOffset")
  var timezone: DateTimeZone = _
}