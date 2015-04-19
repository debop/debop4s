package debop4s.data.orm.jpa.converter

import java.util.Locale
import javax.persistence.{Convert, Entity, EntityManager, PersistenceContext}

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.IntEntity
import org.joda.time.DateTime
import org.junit.Test
import org.springframework.transaction.annotation.Transactional

/**
 * ConverterJUnitSuite
 * @author sunghyouk.bae@gmail.com
 */
@Transactional
class ConverterJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  def testJpaConverter(): Unit = {
    val entity = new ConverterEntity()
    entity.name = "debop"
    entity.locale = Locale.US
    entity.dateStr = DateTime.now
    entity.timestamp = entity.dateStr

    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[ConverterEntity], entity.getId)

    loaded should not be null
    loaded.locale shouldEqual entity.locale
    loaded.dateStr.getMillis shouldEqual entity.dateStr.getMillis
    loaded.timestamp.getMillis / 1000 shouldEqual entity.timestamp.getMillis / 1000
  }
}

@Entity
class ConverterEntity extends IntEntity {

  var name: String = _

  @Convert(converter = classOf[LocaleToStringConverter])
  var locale: Locale = _

  @Convert(converter = classOf[DateTimeToStringConverter])
  var dateStr: DateTime = _

  @Convert(converter = classOf[DateTimeToTimestampConverter])
  var timestamp: DateTime = _

}
