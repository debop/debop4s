package debop4s.data.orm.mapping.simple

import java.util.Date
import javax.persistence._

import debop4s.core.ToStringHelper
import debop4s.core.io.Serializers
import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.jpa.repository.JpaDao
import debop4s.data.orm.model.LongEntity
import org.hibernate.{annotations => ha}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class SimpleEntityJUnitSuite extends AbstractJpaJUnitSuite {

  @Autowired val dao: JpaDao = null

  @Test
  @Transactional
  def lifecycle() {

    val entity = new ScalaLifecycleEntity()
    entity.name = "이름"
    dao.save(entity)

    debug(s"entity=$entity")
    val loaded = dao.findOne(classOf[ScalaLifecycleEntity], entity.getId)

    assert(loaded != null)
    assert(loaded == entity)

    dao.detach(entity)
    entity.name = "변경된 이름"

    dao.save(entity)
    debug(s"entity=$entity")

    val loaded2 = dao.findOne(classOf[ScalaLifecycleEntity], entity.getId)

    debug(s"loaded2=$loaded2")
    assert(loaded2 != null)
    assert(loaded2 == entity)
    assert(loaded2.createdAt != null)

    dao.delete(loaded2)

    debug(s"entity=$entity")
    assert(dao.findOne(classOf[ScalaLifecycleEntity], entity.getId) == null)
  }

  @Test
  @Transactional
  def transientObject() {
    val transient = new ScalaSimpleEntity()
    transient.name = "transient"

    val transient2 = Serializers.copyObject(transient)
    transient2.description = "description"
    assert(transient2 == transient)

    val saved = Serializers.copyObject(transient)
    dao.save(saved)

    log.debug(s"saved=$saved")

    assert(saved != transient)

    val loaded = dao.findOne(classOf[ScalaSimpleEntity], saved.getId)
    assert(loaded != null)
    assert(loaded == saved)
    assert(loaded != transient)

    val saved2 = Serializers.copyObject(transient)
    dao.save(saved2)
    log.debug(s"saved2=$saved2")

    val loaded2 = dao.findOne(classOf[ScalaSimpleEntity], saved2.getId)
    assert(loaded2 != null)
    assert(loaded2 == saved2)
    assert(loaded2 != transient)
    assert(loaded2 != loaded)

    dao.delete(loaded2)

    assert(dao.findOne(classOf[ScalaSimpleEntity], loaded2.getId) == null)
  }
}

@Entity
@Access(AccessType.FIELD)
// @org.hibernate.annotations.Cache(region = "simple", usage = CacheConcurrencyStrategy.READ_WRITE)
@ha.DynamicInsert
@ha.DynamicUpdate
class ScalaLifecycleEntity extends LongEntity {

  @org.hibernate.annotations.Index(name = "ix_lifecycleentity_name", columnNames = Array("name"))
  // @Index(name = "ix_lifecycleentity_name", columnList = "name")
  var name: String = _

  // NOTE: Higernate @Generated 는 JPA 에서는 작동하지 않는다. JPA에서는 @PrePersist @PreUpdate 를 사용해야 합니다.
  // @ha.Generated(ha.GenerationTime.INSERT)
  @Column(name = "createdAt", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  var createdAt: Date = _

  // @ha.Generated(ha.GenerationTime.ALWAYS)
  @Column(name = "updatedAt", insertable = false)
  @Temporal(TemporalType.TIMESTAMP)
  var updatedAt: Date = _

  @PrePersist
  def onPrePersist() {
    if (createdAt == null) createdAt = new Date()
  }

  @PreUpdate
  def onPreUpdate() {
    updatedAt = new Date()
  }

  override def hashCode(): Int = Hashs.compute(name)

  override def buildStringHelper(): ToStringHelper =
    super.buildStringHelper()
    .add("name", name)
    .add("createdAt", createdAt)
    .add("updatedAt", updatedAt)
}

@Entity
@Access(AccessType.FIELD)
// @org.hibernate.annotations.Cache(region = "simple", usage = CacheConcurrencyStrategy.READ_WRITE)
@ha.DynamicInsert
@ha.DynamicUpdate
class ScalaSimpleEntity extends LongEntity {

  var name: String = _
  var description: String = _

  override def hashCode(): Int = Hashs.compute(name)

  override def buildStringHelper(): ToStringHelper =
    super.buildStringHelper()
    .add("name", name)
    .add("description", description)
}