package debop4s.data.tests.jpa.config

import debop4s.core.utils.{ToStringHelper, Hashs}
import debop4s.data.model.LongEntity
import javax.persistence._

/**
 * ScalaJpaEntity 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
@Entity
@NamedQuery(name = "ScalaJapEntity.findByName", query = "select x from ScalaJpaEntity x where x.name = ?1")
@Access(AccessType.FIELD)
class ScalaJpaEntity extends LongEntity {

  @Column(name = "entityName", nullable = false, length = 32)
  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
      .add("name", name)
}
