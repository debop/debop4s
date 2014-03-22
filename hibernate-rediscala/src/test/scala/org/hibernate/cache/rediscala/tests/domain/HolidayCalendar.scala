package org.hibernate.cache.rediscala.tests.domain

import java.text.SimpleDateFormat
import java.util
import java.util.Date
import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class HolidayCalendar extends Serializable {

    @Id
    @GeneratedValue
    var id: java.lang.Long = _

    @CollectionTable(name = "HolidayMap", joinColumns = Array(new JoinColumn(name = "calendarId")))
    @MapKeyClass(classOf[Date])
    @ElementCollection(targetClass = classOf[String], fetch = FetchType.EAGER)
    var holidays: util.Map[Date, String] = new util.HashMap[Date, String]()

    def init(): HolidayCalendar = {
        val df = new SimpleDateFormat("yyyy-MM-dd")

        holidays.clear()
        holidays.put(df.parse("2014-01-01"), "New year's Day")
        holidays.put(df.parse("2014-05-05"), "Children's Day")
        holidays.put(df.parse("2014-12-25"), "Christmas Day")

        this
    }

}
