package com.github.debop4s.data.tests.mapping.property

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.jpa.repository.JpaDao
import com.github.debop4s.data.model.{LongEntity, HibernateLocaleEntity, LocaleValue}
import com.github.debop4s.data.tests.AbstractJpaTest
import java.util
import java.util.Locale
import javax.persistence.Entity
import javax.persistence._
import org.hibernate.annotations._
import org.hibernate.{annotations => ha}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by debop on 2014. 3. 9.
 */
@Transactional
class LocaledEntityTest extends AbstractJpaTest {

  @Autowired val dao: JpaDao = null

  @Test
  def localedEntity() {
    val entity = new SampleLocaleEntity()
    entity.title = "제목-Original"
    entity.description = "설명-Original"
    entity.addLocaleValue(Locale.KOREAN, new SampleLocaleValue("제목", "설명"))
    entity.addLocaleValue(Locale.ENGLISH, new SampleLocaleValue("Title", "Description"))

    dao.persist(entity)
    dao.flush()
    dao.clear()

    val loaded = dao.findOne(classOf[SampleLocaleEntity], entity.id)
    assert(loaded != null)
    assert(loaded.localeMap.size() == 2)

    assert(loaded.getLocaleValue(Locale.KOREAN).title == "제목")
    assert(loaded.getLocaleValue(Locale.KOREAN).description == "설명")
    assert(loaded.getLocaleValue(Locale.ENGLISH).title == "Title")
    assert(loaded.getLocaleValue(Locale.ENGLISH).description == "Description")

    // 지원하지 않는 Locale 정보가 있다면, entity의 기본값을 사용합니다.
    assert(loaded.getLocaleValue(Locale.CHINESE).title == "제목-Original")
    assert(loaded.getLocaleValue(Locale.CHINESE).description == "설명-Original")

    dao.delete(loaded)
    dao.flush()

    assert(dao.findOne(classOf[SampleLocaleEntity], entity.id) == null)
  }

}

@Embeddable
class SampleLocaleValue extends ValueObject with LocaleValue {

  def this(title: String, description: String) {
    this()
    this.title = title
    this.description = description
  }

  var title: String = _
  var description: String = _

  override def hashCode(): Int = Hashs.compute(title)
}

@Entity
@DynamicInsert
@DynamicUpdate
class SampleLocaleEntity extends LongEntity with HibernateLocaleEntity[SampleLocaleValue] {

  var title: String = _
  var description: String = _

  @CollectionTable(name = "SampleLocaleEntityLocale", joinColumns = Array(new JoinColumn(name = "entityId")))
  @MapKeyClass(classOf[Locale])
  @ElementCollection(targetClass = classOf[SampleLocaleValue], fetch = FetchType.EAGER)
  @ha.Cascade(Array(ha.CascadeType.ALL))
  @LazyCollection(LazyCollectionOption.EXTRA)
  override val localeMap: util.Map[Locale, SampleLocaleValue] = new util.HashMap[Locale, SampleLocaleValue]()

  override protected def createDefaultLocaleVal: SampleLocaleValue = {
    new SampleLocaleValue(title, description)
  }

  @inline
  override def hashCode(): Int = Hashs.compute(title)
}