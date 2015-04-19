package debop4s.data.orm.jpa.converter

import java.util.Locale
import javax.persistence._
import javax.transaction.Transactional

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.IntEntity
import org.joda.time.DateTime
import org.junit.Test

/**
 * JPA 2.1 Converter 에 대한 테스트 ( Hibernate UserType의 기본 기능 )
 * @author sunghyouk.bae@gmail.com 14. 11. 8.
 */
@Transactional
class ConverterFunSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  def testLocaleConverter(): Unit = {
    var le = new LocaledEntity()
    le.name = "Sunghyouk Bae"
    le.locale = Locale.US

    em.persist(le)
    em.flush()
    em.clear()

    var loaded = em.find(classOf[LocaledEntity], le.getId)
    loaded should not be null
    loaded.locale shouldEqual Locale.US

    le = new LocaledEntity()
    le.name = "배성혁"
    le.locale = Locale.KOREA

    em.persist(le)
    em.flush()
    em.clear()

    loaded = em.find(classOf[LocaledEntity], le.getId)
    loaded should not be null
    loaded.locale shouldEqual Locale.KOREA
  }

  @Test
  def testDateTimeConverter(): Unit = {
    var entity = new LocaledEntity()
    entity.name = "배성혁"
    entity.time = DateTime.now
    entity.locale = Locale.getDefault

    em.persist(entity)
    em.flush()
    em.clear()

    var loaded = em.find(classOf[LocaledEntity], entity.getId)
    loaded should not be null
    loaded.time.compareTo(entity.time) shouldEqual 0
  }
}


@Entity
class LocaledEntity extends IntEntity {

  var name: String = _

  @Convert(converter = classOf[LocaleToStringConverter])
  var locale: Locale = _

  @Convert(converter = classOf[DateTimeToStringConverter])
  var time: DateTime = _

}