package debop4s.data.orm.mapping.property

import javax.persistence.{Access, AccessType, Entity}

import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.jpa.repository.JpaDao
import debop4s.data.orm.mapping.property.OrdinalEnum.OrdinalEnum
import debop4s.data.orm.mapping.property.StringEnum.StringEnum
import debop4s.data.orm.model.IntEntity
import org.hibernate.annotations.{DynamicInsert, DynamicUpdate}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Transactional
class EnumeratedJUnitSuite extends AbstractJpaJUnitSuite {

  @Autowired val dao: JpaDao = null

  @Test
  @Rollback(false)
  def enumeratedProperty() {
    val entity = new EnumeratedEntity()
    entity.intValue = OrdinalEnum.Second
    entity.stringValue = StringEnum.Integer

    val saved = dao.save(entity)

    log.debug(s"entity=$entity")
    log.debug(s"saved=$saved")

    val loaded = dao.findOne(classOf[EnumeratedEntity], saved.getId)
    loaded shouldEqual saved

    dao.delete(classOf[EnumeratedEntity], saved.getId)
    dao.delete(saved)

    dao.findOne(classOf[EnumeratedEntity], saved.getId) should be(null)
  }

}

@Entity
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class EnumeratedEntity extends IntEntity {

  // NOTE: Scala 에서는 java의 enum 수형이 없기 때문에, 이렇게 Integer나 String으로 변환하는 작업을 해야 합니다.
  // @Enumerated(EnumType.ORDINAL)
  private var _intValue: Integer = _

  def intValue: OrdinalEnum = OrdinalEnum(_intValue)

  def intValue_=(x: OrdinalEnum) = {
    _intValue = x.id
  }

  // NOTE: Scala 에서는 java의 enum 수형이 없기 때문에, 이렇게 Integer나 String으로 변환하는 작업을 해야 합니다.
  // @Enumerated(EnumType.STRING)
  private var _stringValue: String = _

  def stringValue: StringEnum = StringEnum.withName(_stringValue)

  def stringValue_=(x: StringEnum) = {
    _stringValue = x.toString
  }

  override def hashCode(): Int = Hashs.compute(intValue, stringValue)

}

// Java의 enum 을 scala 로 표현한다.
object OrdinalEnum extends Enumeration {
  type OrdinalEnum = Value

  val First = Value("First")
  val Second = Value("Second")
  val Third = Value("Third")
}

object StringEnum extends Enumeration {
  type StringEnum = Value

  val String = Value("String")
  val Integer = Value("Integer")
  val Decimal = Value("Decimal")
}
