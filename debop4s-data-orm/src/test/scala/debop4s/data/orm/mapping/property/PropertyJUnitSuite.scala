package debop4s.data.orm.mapping.property

import javax.persistence.{Access, AccessType, Entity, Lob}
import javax.transaction.Transactional

import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.jpa.repository.JpaDao
import debop4s.data.orm.model.LongEntity
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class PropertyJUnitSuite extends AbstractJpaJUnitSuite {

  @Autowired val dao: JpaDao = null

  @Test
  @Transactional
  def property() {
    var p = new PropertyEntity()
    p.name = "name"
    p.data = "동해물과 백두산이 마르고 닳도록 " * 1000

    p = dao.save(p)
    dao.flush()
    log.debug(s"p=$p")

    val p2 = dao.findOne(classOf[PropertyEntity], p.getId)
    assert(p2 != null)
    assert(p2 == p)
    assert(p2.data.length > 100)

    dao.delete(p2)

    assert(dao.findOne(classOf[PropertyEntity], p.getId) == null)
  }
}


@Entity
@Access(AccessType.FIELD)
class PropertyEntity extends LongEntity {

  var name: String = _

  @Lob
  var data: String = _

  override def hashCode(): Int = Hashs.compute(name)
}