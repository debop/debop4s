package debop4s.data.orm.jpa.mysql.model

import java.util
import javax.persistence._

import debop4s.core.utils.Hashs
import debop4s.data.orm.model.HibernateEntityBase
import org.hibernate.{annotations => hba}

/**
 * MySqlOrder
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
@Entity
// @hba.Cache(region = "mysql", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
class MySqlOrder extends HibernateEntityBase[Integer] {

  @Id
  @GeneratedValue
  var id: Integer = _

  def getId = id

  var no: String = _

  @OneToMany(mappedBy = "order", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @hba.LazyCollection(hba.LazyCollectionOption.EXTRA)
  val items: util.List[MySqlOrderItem] = new util.ArrayList[MySqlOrderItem]

  @inline
  override def hashCode(): Int = Hashs.compute(no)

  @inline
  override protected def buildStringHelper =
    super.buildStringHelper
    .add("no", no)
}