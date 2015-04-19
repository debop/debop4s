package debop4s.data.orm.jpa.converter

import java.sql.Timestamp
import javax.persistence.{AttributeConverter, Converter}

import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * JPA 2.1 에서 DateTime을 DB에 Timestamp 수형으로 저장하도록 해주는 Converter 입니다.
 * @author sunghyouk.bae@gmail.com
 */
@Converter
class DateTimeToTimestampConverter extends AttributeConverter[DateTime, Timestamp] {
  private val log = LoggerFactory.getLogger(getClass)

  override def convertToDatabaseColumn(attribute: DateTime): Timestamp = {
    val result = if (attribute != null) new Timestamp(attribute.getMillis) else null
    log.debug(s"convert database to timestamp. attribute=$attribute, result=$result")
    result
  }
  override def convertToEntityAttribute(dbData: Timestamp): DateTime = {
    val result = if (dbData != null) new DateTime(dbData.getTime) else null
    log.debug(s"convert dbData=$dbData, result=$result")
    result
  }
}
