package debop4s.data.tests.mapping.associations

import debop4s.core.{ToStringHelper, ValueObject}
import debop4s.core.utils.Hashs
import debop4s.data.model.HibernateEntity
import debop4s.data.tests.AbstractJpaTest
import java.{ util, lang }
import javax.persistence._
import org.junit.Test

/**
 * OneToManyMapTest
 * Created by debop on 2014. 3. 5.
 */
@org.springframework.transaction.annotation.Transactional
class OneToManyMapTest extends AbstractJpaTest {

  @PersistenceContext val em: EntityManager = null

  @Test
  def mapTest() {
    val car = new Car()

    val carOption1 = new CarOption("option1", 1)
    val carOption2 = new CarOption("option2", 1)

    car.carOptions.put("option1", carOption1)
    car.carOptions.put("option2", carOption2)

    car.options.put("stringOption1", "Value1")
    car.options.put("stringOption2", "Value2")

    em.persist(car)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[Car], car.id)
    assert(loaded != null)
    assert(loaded.carOptions.size == 2)
    assert(loaded.options.size == 2)

    em.remove(loaded)
    em.flush()
    assert(em.find(classOf[Car], car.id) == null)
  }
}

@Entity(name = "OneToMany_Car")
class Car extends HibernateEntity[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _

  @CollectionTable(name = "OneToMany_Car_Option_Map", joinColumns = Array(new JoinColumn(name = "carId")))
  @MapKeyClass(classOf[String])
  @ElementCollection(targetClass = classOf[String], fetch = FetchType.EAGER)
  var options: util.Map[String, String] = new util.HashMap[String, String]()

  @CollectionTable(name = "OneToMany_Car_Option_Table", joinColumns = Array(new JoinColumn(name = "carId")))
  @MapKeyClass(classOf[String])
  @ElementCollection(targetClass = classOf[CarOption], fetch = FetchType.EAGER)
  var carOptions: util.Map[String, CarOption] = new util.HashMap[String, CarOption]()

  override def hashCode(): Int = Hashs.compute(name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("name", name)
}

@Embeddable
class CarOption extends ValueObject {

  def this(name: String, value: Int) {
    this()
    this.name = name
    this.value = value
  }

  var name: String = _
  var value: Int = _

  override def hashCode(): Int = Hashs.compute(name)
}
