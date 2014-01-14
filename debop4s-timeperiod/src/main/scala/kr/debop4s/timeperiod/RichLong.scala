package kr.debop4s.timeperiod

import java.sql.Timestamp
import org.joda.time.{Duration, DateTime}

/**
 * kr.debop4s.time.RichLong
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:21
 */
class RichLong(val self: Long) extends AnyVal {

    def toDateTime = new DateTime(self)
    def toDuration = new Duration(self)

    def toTimestamp = new Timestamp(self)

}
