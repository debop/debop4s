package debop4s.data.orm.jpa

import javax.persistence._

import debop4s.core.ToStringHelper
import debop4s.core.utils.Hashs
import debop4s.data.orm.model.LongEntity
import org.hibernate.{annotations => hba}

import _root_.scala.beans.BeanProperty


/**
 * JpaScalaEntity
 * Created by debop on 2014. 1. 29.
 */
@Entity
@hba.Cache(region = "scala.jpa", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class ScalaJpaEntity extends LongEntity {

  def this(name: String) {
    this()
    this.name = name
  }

  @Column(name = "entityName", nullable = false, length = 32)
  @BeanProperty
  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)

  override
  def buildStringHelper(): ToStringHelper =
    super.buildStringHelper()
    .add("name", name)
}
