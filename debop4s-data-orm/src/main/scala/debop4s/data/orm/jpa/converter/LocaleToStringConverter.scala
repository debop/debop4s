package debop4s.data.orm.jpa.converter

import java.util.Locale
import javax.persistence.{AttributeConverter, Converter}

import debop4s.core.utils.Strings
import org.slf4j.LoggerFactory

/**
 * `Locale` 정보를 DB에 문자열로 저장할 수 있게 하는 JPA 2.1 Converter 입니다.
 * @author sunghyouk.bae@gmail.com
 */
@Converter
class LocaleToStringConverter extends AttributeConverter[Locale, String] {

  private val log = LoggerFactory.getLogger(getClass)

  override def convertToDatabaseColumn(attribute: Locale): String = {
    val result = if (attribute != null) attribute.toLanguageTag else null
    log.debug(s"convert locale to string. attribute=$attribute, result=$result")
    result
  }
  override def convertToEntityAttribute(dbData: String): Locale = {
    val result = if (Strings.isNotWhitespace(dbData)) Locale.forLanguageTag(dbData) else null
    log.debug(s"convert string to locale. dbData=$dbData, result=$result")
    result
  }
}
