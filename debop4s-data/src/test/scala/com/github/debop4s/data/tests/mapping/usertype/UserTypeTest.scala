package com.github.debop4s.data.tests.mapping.usertype

import com.github.debop4s.core.utils.{Strings, Hashs}
import com.github.debop4s.data.model.LongEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import com.github.debop4s.data.tests.mapping.Employee
import com.github.debop4s.timeperiod._
import com.github.debop4s.timeperiod.utils.{Durations, Times}
import java.nio.charset.Charset
import javax.persistence._
import org.hibernate.annotations.{CacheConcurrencyStrategy, Columns}
import org.hibernate.{annotations => hba}
import org.joda.time.DateTime
import org.junit.Test
import org.springframework.transaction.annotation.Transactional

/**
 * Created by debop on 2014. 3. 9.
 */
@Transactional
class UserTypeTest extends AbstractJpaTest {

    @PersistenceContext val em: EntityManager = null

    val PLAIN_TEXT: String = "동해물과 백두산이 마르고 닳도록 "

    @Test
    def jodaDateTimeUserType() {
        val entity = new JodaDateTimeEntity()

        entity.start = Times.today
        entity.end = entity.start + 1.day

        entity.startTZ = Times.now
        entity.endTZ = entity.startTZ + 1.day

        entity.range1 = new TimeRange(entity.start, entity.end)
        entity.range2 = entity.range1.copy(Durations.Day)

        em.persist(entity)
        em.flush()
        em.clear()

        val loaded = em.find(classOf[JodaDateTimeEntity], entity.id)

        assert(loaded == entity)
        assert(loaded.start == entity.start)
        assert(loaded.end == entity.end)
        assert(loaded.startTZ == entity.startTZ)
        assert(loaded.endTZ == entity.endTZ)
        assert(loaded.range1 == entity.range1)
        assert(loaded.range2 == entity.range2)

        em.remove(loaded)
        em.flush()
        assert(em.find(classOf[JodaDateTimeEntity], loaded.id) == null)
    }

    @Test
    def compressedDataEntity() {
        val entity = new CompressedDataEntity()
        entity.stringData = PLAIN_TEXT * 1000
        entity.binaryData = entity.stringData.getBytes(Charset.forName("UTF-8"))

        em.persist(entity)
        em.flush()
        em.clear()

        val loaded = em.find(classOf[CompressedDataEntity], entity.id)
        assert(loaded.stringData == entity.stringData)
        assert(Strings.getUtf8String(loaded.binaryData) == Strings.getUtf8String(entity.binaryData))

        em.remove(loaded)
        em.flush()
        assert(em.find(classOf[CompressedDataEntity], entity.id) == null)
    }

    @Test
    def jsonUserType() {
        val emp = new Employee()
        emp.name = "Sunghyouk Bae"
        emp.empNo = "21011"
        em.persist(emp)

        val entity = new JsonEntity()
        entity.employee = emp

        em.persist(entity)
        em.flush()
        em.clear()

        val loaded = em.find(classOf[JsonEntity], entity.id)
        assert(loaded == entity)
        assert(loaded.employee == entity.employee)

        em.remove(loaded)
        em.flush()
        assert(em.find(classOf[JsonEntity], entity.id) == null)
    }
}

@Entity
@org.hibernate.annotations.Cache(region = "usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class JodaDateTimeEntity extends LongEntity {

    @Column(name = "jodaStart")
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.JodaDateTimeUserType")
    var start: DateTime = _

    @Column(name = "jodaEnd")
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.JodaDateTimeUserType")
    var end: DateTime = _

    @Columns(columns = Array(new Column(name = "startTime"), new Column(name = "startTimeZone", length = 32)))
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.JodaDateTimeTZUserType")
    var startTZ: DateTime = _

    @Columns(columns = Array(new Column(name = "endTime"), new Column(name = "endTimeZone", length = 32)))
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.JodaDateTimeTZUserType")
    var endTZ: DateTime = _

    @Columns(columns = Array(new Column(name = "rangeStart1"), new Column(name = "rangeEnd1")))
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.TimeRangeUserType")
    var range1: ITimePeriod = _

    @Columns(columns = Array(new Column(name = "rangeStart2"), new Column(name = "rangeEnd2")))
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.TimeRangeUserType")
    var range2: ITimePeriod = _

    @inline
    override def hashCode(): Int = Hashs.compute(start, end, startTZ, endTZ, range1, range2)
}

@Entity
@org.hibernate.annotations.Cache(region = "usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class CompressedDataEntity extends LongEntity {

    @Lob
    @Column(name = "CompressedString")
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.compress.GZipStringUserType")
    var stringData: String = _

    @Lob
    @Column(name = "CompressedBytes")
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.compress.GZipBinaryUserType")
    var binaryData: Array[Byte] = _

    @inline
    override def hashCode(): Int = Hashs.compute(stringData, binaryData)
}

@Entity
@org.hibernate.annotations.Cache(region = "usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class JsonEntity extends LongEntity {

    @hba.Columns(columns = Array(new Column(name = "className"), new Column(name = "jsonText", length = 2000)))
    @hba.Type(`type` = "com.github.debop4s.data.hibernate.usertype.JacksonUserType")
    var employee: Employee = _

    @inline
    override def hashCode(): Int = Hashs.compute(employee)
}
