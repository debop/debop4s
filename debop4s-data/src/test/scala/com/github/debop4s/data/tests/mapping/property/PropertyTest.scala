package com.github.debop4s.data.tests.mapping.property

import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.jpa.repository.JpaDao
import com.github.debop4s.data.model.LongEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import javax.persistence.{Lob, Entity}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by debop on 2014. 3. 9.
 */
@Transactional
class PropertyTest extends AbstractJpaTest {

  @Autowired val dao: JpaDao = null

  @Test
  def property() {
    val p = new PropertyEntity()
    p.name = "name"
    p.data = "동해물과 백두산이 마르고 닳도록 " * 1000

    dao.persist(p)
    dao.flush()
    dao.clear()

    val p2 = dao.findOne(classOf[PropertyEntity], p.id)
    assert(p2 != null)
    assert(p2 == p)
    assert(p2.data.length > 100)

    dao.delete(p2)
    assert(dao.findOne(classOf[PropertyEntity], p.id) == null)
  }
}


@Entity
class PropertyEntity extends LongEntity {

  var name: String = _

  @Lob
  var data: String = _

  @inline
  override def hashCode(): Int = Hashs.compute(name)
}