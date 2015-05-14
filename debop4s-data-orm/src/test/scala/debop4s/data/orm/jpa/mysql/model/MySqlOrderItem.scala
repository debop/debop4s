package debop4s.data.orm.jpa.mysql.model

import javax.persistence._

import debop4s.core.ToStringHelper
import debop4s.core.utils.Hashs
import debop4s.data.orm.model.HibernateEntityBase
import org.hibernate.{annotations => hba}

/**
 * MySqlOrderItem
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
@Entity
// @hba.Cache(region = "mysql", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class MySqlOrderItem extends HibernateEntityBase[Integer] {

  @Id
  @GeneratedValue
  @Column(name = "orderItemId")
  var id: Integer = _

  def getId = id

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderId")
  var order: MySqlOrder = _

  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)

  override protected def buildStringHelper(): ToStringHelper =
    super.buildStringHelper()
    .add("name", name)
}

