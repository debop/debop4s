package debop4s.data.orm.jpa.converter

import javax.persistence.{AttributeConverter, Converter}

import debop4s.core.utils.Strings
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * JPA 2.1 에서 DateTime 수형을 DB에 문자열로 변환하여 저장하도록 해주는 Converter 입니다.
 * @author sunghyouk.bae@gmail.com
 */
@Converter
class DateTimeToStringConverter extends AttributeConverter[DateTime, String] {
  private val log = LoggerFactory.getLogger(getClass)

  override def convertToDatabaseColumn(attribute: DateTime): String = {
    val result = if (attribute != null) attribute.toString() else null: String
    log.debug(s"convert database to timestamp. attribute=$attribute, result=$result")
    result
  }
  override def convertToEntityAttribute(dbData: String): DateTime = {
    val result = if (Strings.isNotWhitespace(dbData)) DateTime.parse(dbData) else null: DateTime
    log.debug(s"convert dbData=$dbData, result=$result")
    result
  }
}
