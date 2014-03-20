package com.github.debop4s.data.tests.mapping.simple

import com.github.debop4s.core.io.Serializers
import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.jpa.repository.JpaDao
import com.github.debop4s.data.model.LongEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.util.Date
import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.{annotations => ha}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 * Created by debop on 2014. 3. 9.
 */
@Transactional
class SimpleEntityTest extends AbstractJpaTest {

    @Autowired val dao: JpaDao = null

    @Test
    def lifecycle() {
        val entity = new LifecycleEntity()
        entity.name = "이름"
        dao.persist(entity)
        dao.flush()

        dao.em.detach(entity)
        entity.name = "변경된 이름"
        dao.em.merge(entity)
        dao.flush()

        dao.clear()

        val loaded = dao.findOne(classOf[LifecycleEntity], entity.id)
        assert(loaded != null)
        assert(loaded == entity)
        assert(loaded.createdAt != null)
        assert(loaded.updatedAt != null)

        dao.delete(loaded)
        dao.flush()
        dao.clear()

        assert(dao.findOne(classOf[LifecycleEntity], loaded.id) == null)
    }

    @Test
    def transientObject() {
        val transient = new SimpleEntity()
        transient.name = "transient"

        val transient2 = Serializers.copyObject(transient)
        transient2.description = "description"
        assert(transient2 == transient)

        val saved = Serializers.copyObject(transient)
        dao.persist(saved)
        dao.flush()
        dao.clear()

        assert(saved != transient)

        val loaded = dao.findOne(classOf[SimpleEntity], saved.id)
        assert(loaded != null)
        assert(loaded == saved)
        assert(loaded != transient)

        val saved2 = Serializers.copyObject(transient)
        dao.persist(saved2)
        dao.flush()
        dao.clear()

        val loaded2 = dao.findOne(classOf[SimpleEntity], saved2.id)
        assert(loaded2 != null)
        assert(loaded2 == saved2)
        assert(loaded2 != transient)
        assert(loaded2 != loaded)

        dao.delete(loaded2)
        dao.flush()
        dao.clear()

        assert(dao.findOne(classOf[SimpleEntity], saved2.id) == null)


    }
}

@Entity
@org.hibernate.annotations.Cache(region = "simple", usage = CacheConcurrencyStrategy.READ_WRITE)
@ha.DynamicInsert
@ha.DynamicUpdate
class LifecycleEntity extends LongEntity {

    @Index(name = "ix_lifecycleentity_name", columnList = "name")
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
}

@Entity
@org.hibernate.annotations.Cache(region = "simple", usage = CacheConcurrencyStrategy.READ_WRITE)
@ha.DynamicInsert
@ha.DynamicUpdate
class SimpleEntity extends LongEntity {

    var name: String = _
    var description: String = _

    override def hashCode(): Int = Hashs.compute(name)
}