package debop4s.data.orm.jpa

import javax.persistence.{Access, AccessType, Column, Entity}

import debop4s.core.ToStringHelper
import debop4s.core.utils.Hashs
import debop4s.data.orm.model.LongEntity
import org.hibernate.{annotations => hba}

/**
 * ScalaJpaEntityReadOnly
 * @author sunghyouk.bae@gmail.com
 */
@Entity
@hba.Cache(region = "scala.jpa", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class ScalaJpaEntity2 extends LongEntity {

  def this(name: String) {
    this()
    this.name = name
  }

  @Column(name = "entityName", nullable = false, length = 32)
  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)

  override
  def buildStringHelper(): ToStringHelper =
    super.buildStringHelper()
    .add("name", name)
}