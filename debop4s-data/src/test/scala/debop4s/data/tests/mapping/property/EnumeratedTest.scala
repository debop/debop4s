package debop4s.data.tests.mapping.property

import debop4s.core.utils.Hashs
import debop4s.data.jpa.repository.JpaDao
import debop4s.data.model.LongEntity
import debop4s.data.tests.AbstractJpaTest
import debop4s.data.tests.mapping.property.OrdinalEnum.OrdinalEnum
import debop4s.data.tests.mapping.property.StringEnum.StringEnum
import javax.persistence.Entity
import org.hibernate.annotations.{DynamicUpdate, DynamicInsert}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by debop on 2014. 3. 9.
 */
@Transactional
class EnumeratedTest extends AbstractJpaTest {

    @Autowired val dao: JpaDao = null

    @Test
    def enumeratedProperty() {
        val entity = new EnumeratedEntity()
        entity.intValue = OrdinalEnum.Second
        entity.stringValue = StringEnum.Integer

        dao.persist(entity)
        dao.flush()
        dao.clear()

        val loaded = dao.findOne(classOf[EnumeratedEntity], entity.id)
        assert(loaded == entity)

        dao.delete(loaded)
        assert(dao.findOne(classOf[EnumeratedEntity], entity.id) == null)
    }

}

@Entity
@DynamicInsert
@DynamicUpdate
class EnumeratedEntity extends LongEntity {

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

    @inline
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
