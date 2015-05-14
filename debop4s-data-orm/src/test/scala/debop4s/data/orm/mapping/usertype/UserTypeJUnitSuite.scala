package debop4s.data.orm.mapping.usertype

import java.nio.charset.Charset
import javax.persistence._

import debop4s.core.conversions.jodatime._
import debop4s.core.utils.{Hashs, Strings}
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.mapping.ScalaEmployee
import debop4s.data.orm.model.{IntEntity, LongEntity}
import debop4s.timeperiod.utils.{Durations, Times}
import debop4s.timeperiod.{ITimePeriod, TimeRange}
import org.hibernate.annotations.{CacheConcurrencyStrategy, Columns}
import org.hibernate.{annotations => hba}
import org.joda.time.{DateTime, DateTimeZone}
import org.junit.Test
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Transactional
class UserTypeJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  val PLAIN_TEXT: String = "동해물과 백두산이 마르고 닳도록 "

  @Test
  @Rollback(false)
  def encryptedStrigUserType(): Unit = {
    val entity = new EncryptedEntity()

    val plainText = "고기사주세요"
    entity.name = "debop"
    entity.passwd = plainText
    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[EncryptedEntity], entity.getId)

    loaded.passwd shouldEqual plainText

    loaded.name = "배성혁"
    em.persist(loaded)
    em.flush()
    em.clear()

    val loaded2 = em.find(classOf[EncryptedEntity], entity.getId)

    loaded2.passwd shouldEqual plainText
    loaded.name shouldEqual "배성혁"
  }

  @Test
  @Rollback(false)
  def hashedStringUserType(): Unit = {
    val entity = new HashedEntity()
    entity.name = "배성혁"
    entity.passwd = "rhrltkwntpdy"

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[HashedEntity], entity.getId)
    loaded should not be null
    loaded.name shouldEqual "배성혁"

    val query = em.createQuery("select he from HashedEntity he where he.passwd=:passwd")
    query.setParameter("passwd", entity.passwd)
    val list = query.getResultList
    list.size should be > 0
  }

  @Test
  @Rollback(false)
  def jodaDateTimeUserType() {
    val entity = new JodaDateTimeEntity()

    entity.start = Times.today
    entity.end = entity.start.plusDays(1)

    entity.startTZ = Times.now
    entity.endTZ = entity.startTZ.plusDays(1)

    entity.range1 = new TimeRange(entity.start, entity.end)
    entity.range2 = entity.range1.copy(Durations.Day)

    entity.timeAsString = entity.startTZ

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[JodaDateTimeEntity], entity.getId)

    assert(loaded == entity)
    assert(loaded.start == entity.start)
    assert(loaded.end == entity.end)

    // NOTE: MySQL은 milliseconds 를 저장하지 않고, H2 는 Milliseconds 까지 저장합니다.
    assert(Times.trimToMillis(loaded.startTZ) == Times.trimToMillis(entity.startTZ))
    assert(Times.trimToMillis(loaded.endTZ) == Times.trimToMillis(entity.endTZ))

    assert(loaded.range1 == entity.range1)
    assert(loaded.range2 == entity.range2)

    assert(loaded.timeAsString == entity.timeAsString)

    val startTZ = Times.now.asLocal(DateTimeZone.forID("EST"))
    val endTZ = startTZ.plusDays(1)
    val entity2 = new JodaDateTimeEntity()

    entity2.start = startTZ
    entity2.startTZ = startTZ
    entity2.endTZ = endTZ
    entity2.timeAsString = startTZ
    em.persist(entity2)
    em.flush()

    val loaded2 = em.find(classOf[JodaDateTimeEntity], entity2.getId)

    assert(loaded2.startTZ == startTZ)
    assert(loaded2.endTZ == endTZ)
    assert(loaded2.timeAsString == startTZ)

    log.debug(s"start=$startTZ, end=$endTZ")

    em.remove(loaded)
    em.flush()
    em.clear()

    assert(em.find(classOf[JodaDateTimeEntity], entity.getId) == null)
  }

  @Test
  def compressedDataEntity() {
    val entity = new CompressedDataEntity()
    entity.stringData = PLAIN_TEXT * 1000
    entity.binaryData = entity.stringData.getBytes(Charset.forName("UTF-8"))

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[CompressedDataEntity], entity.getId)
    assert(loaded.stringData == entity.stringData)
    assert(Strings.getUtf8String(loaded.binaryData) == Strings.getUtf8String(entity.binaryData))

    em.remove(loaded)
    em.flush()
    em.clear()
    assert(em.find(classOf[CompressedDataEntity], entity.getId) == null)
  }

  @Test
  def jsonUserType() {
    val emp = new ScalaEmployee()
    emp.name = "Sunghyouk Bae"
    emp.empNo = "21011"
    em.persist(emp)

    val entity = new JsonEntity()
    entity.employee = emp

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[JsonEntity], entity.getId)
    assert(loaded == entity)
    assert(loaded.employee == entity.employee)

    em.remove(loaded)
    em.flush()
    em.clear()
    assert(em.find(classOf[JsonEntity], entity.getId) == null)
  }

  @Test
  @Rollback(false)
  def testKoreanChosung(): Unit = {
    val korean = new KoreanChosungEntity()
    korean.name = "배성혁"
    korean.description = "한글과 초성만을 나타내는 컬럼을 두었습니다."
    em.persist(korean)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[KoreanChosungEntity], korean.getId)
    loaded should not be null
    loaded.name = "배성혁"
  }
}

@Entity
@org.hibernate.annotations.Cache(region = "usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class JodaDateTimeEntity extends LongEntity {

  @Column(name = "jodaStart")
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
  var start: DateTime = _

  @Column(name = "jodaEnd")
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
  var end: DateTime = _

  @Columns(columns = Array(new Column(name = "startTime"), new Column(name = "startTimeZone", length = 32)))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZone")
  var startTZ: DateTime = _

  @Columns(columns = Array(new Column(name = "endTime"), new Column(name = "endTimeZone", length = 32)))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZone")
  var endTZ: DateTime = _

  @Columns(columns = Array(new Column(name = "rangeStart1"), new Column(name = "rangeEnd1")))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.TimeRangeUserType")
  var range1: ITimePeriod = _

  @Columns(columns = Array(new Column(name = "rangeStart2"), new Column(name = "rangeEnd2")))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.TimeRangeUserType")
  var range2: ITimePeriod = _

  @Column(name = "timeAsString", length = 32)
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.jodatime.DateTimeAsIsoFormatString")
  var timeAsString: DateTime = _

  override def hashCode(): Int = Hashs.compute(start, end, startTZ, endTZ, range1, range2)
}

@Entity
@Access(AccessType.FIELD)
@hba.Cache(region = "usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class CompressedDataEntity extends LongEntity {

  @Lob
  @Column(name = "CompressedString")
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.compress.GZipStringUserType")
  var stringData: String = _

  @Lob
  @Column(name = "CompressedBytes")
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.compress.GZipBinaryUserType")
  var binaryData: Array[Byte] = _

  override def hashCode(): Int = Hashs.compute(stringData, binaryData)
}

@Entity
@Access(AccessType.FIELD)
@hba.Cache(region = "usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class JsonEntity extends LongEntity {

  @hba.Columns(columns = Array(new Column(name = "className"), new Column(name = "jsonText", length = 2000)))
  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.JacksonUserType")
  var employee: ScalaEmployee = _

  override def hashCode(): Int = Hashs.compute(employee)
}

@Entity
@Access(AccessType.FIELD)
// @hba.Cache(region = "usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
//@hba.DynamicInsert
//@hba.DynamicUpdate
class EncryptedEntity extends LongEntity {

  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.cryptography.RC2EncryptorUserType")
  var passwd: String = _

  var name: String = _
}

/**
 * Hash 암호화를 수행하는 엔티티는 꼭 DynamicUpdate 를 수행해야 합니다.
 */
@Entity
@Access(AccessType.FIELD)
@hba.DynamicInsert
@hba.DynamicUpdate
class HashedEntity extends IntEntity {

  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.cryptography.SHA512StringUserType")
  var passwd: String = _

  var name: String = _
}

@Entity
@Access(AccessType.FIELD)
@hba.DynamicInsert
@hba.DynamicUpdate
class KoreanChosungEntity extends IntEntity {

  @hba.Type(`type` = "debop4s.data.orm.hibernate.usertype.KoreanChosungUserType")
  @Columns(columns = Array(new Column(name = "name", nullable = false), new Column(name = "nameChosung", nullable = false)))
  var name: String = _

  var description: String = _
}