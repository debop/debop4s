package debop4s.data.orm.mapping

import java.lang.{Long => jLong}
import javax.persistence._

import debop4s.core.utils.Hashs
import debop4s.data.orm.model.{HibernateEntityBase, UpdatedTimestampEntity}
import org.hibernate.annotations.{CacheConcurrencyStrategy, DynamicInsert, DynamicUpdate, Type}
import org.joda.time.DateTime

/**
 * ScalaEmployee
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
@Entity
@org.hibernate.annotations.Cache(region = "common", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@SequenceGenerator(name = "scala_employee_seq", sequenceName = "scala_employee_seq")
@Access(AccessType.FIELD)
@SerialVersionUID(6936596398947403315L)
class ScalaEmployee extends HibernateEntityBase[jLong] with UpdatedTimestampEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "scala_employee_seq")
  @Column(name = "employeeId")
  protected var id: jLong = _

  def getId: jLong = id

  @Column(name = "empNo", nullable = false, length = 32)
  var empNo: String = _

  @Column(name = "employeeName", nullable = false, length = 32)
  var name: String = _

  @Column(name = "employeeEmail", length = 64)
  var email: String = _

  @Type(`type` = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
  var birthDay: DateTime = _

  @Embedded
  var address: Address = Address()

  @Type(`type` = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
  var updatedTimestamp: DateTime = _

  def getUpdatedTimestamp: DateTime = updatedTimestamp

  @PrePersist
  def updateUpdatedTimestamp() {
    updatedTimestamp = DateTime.now()
  }

  override def hashCode(): Int = Hashs.compute(empNo, name)

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("empNo", empNo)
    .add("name", name)
    .add("email", email)
}
