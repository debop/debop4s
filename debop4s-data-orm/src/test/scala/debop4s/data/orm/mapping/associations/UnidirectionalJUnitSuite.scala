package debop4s.data.orm.mapping.associations

import java.{lang, util}
import javax.persistence._

import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.model.HibernateEntityBase
import org.junit.Test

import scala.collection.JavaConverters._

@org.springframework.transaction.annotation.Transactional
class UnidirectionalJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  def unidirectionalCollection() {
    val sf = new Snowflake()
    sf.description = "Snowflake 1"
    em.persist(sf)

    val sf2 = new Snowflake()
    sf.description = "Snowflake 2"
    em.persist(sf2)

    val cloud = new Cloud()
    cloud.length = 23.0
    cloud.producedSnowflakes.add(sf)
    cloud.producedSnowflakes.add(sf2)
    em.persist(cloud)
    em.flush()
    em.clear()

    var cloud2 = em.find(classOf[Cloud], cloud.id)
    assert(cloud2 != null)
    assert(cloud2.producedSnowflakes != null)
    assert(cloud2.producedSnowflakes.size == 2)

    val removedSf = cloud2.producedSnowflakes.iterator().next()
    val sf3 = new Snowflake()
    sf3.description = "Snowflake 3"
    em.persist(sf3)

    cloud2.producedSnowflakes.remove(removedSf)
    cloud2.producedSnowflakes.add(sf3)
    em.persist(cloud2)
    em.flush()
    em.clear()

    cloud2 = em.find(classOf[Cloud], cloud.id)
    assert(cloud2 != null)
    assert(cloud2.producedSnowflakes != null)
    assert(cloud2.producedSnowflakes.size == 2)

    assert(cloud2.producedSnowflakes.asScala.forall(sf => sf.description != removedSf.description))
    assert(cloud2.producedSnowflakes.asScala.exists(sf => sf.description == sf3.description))

    cloud2.producedSnowflakes.asScala.foreach(em.remove)
    cloud2.producedSnowflakes.clear()
    em.remove(em.find(classOf[Snowflake], removedSf.id))
    em.flush()
    em.clear()

    cloud2 = em.find(classOf[Cloud], cloud.id)
    assert(cloud2 != null)
    assert(cloud2.producedSnowflakes != null)
    assert(cloud2.producedSnowflakes.size == 0)

    em.remove(cloud2)
    em.flush()

    assert(em.find(classOf[Cloud], cloud.id) == null)
  }
}

@Entity
@Access(AccessType.FIELD)
class Cloud extends HibernateEntityBase[lang.Long] {
  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var kind: String = _
  var length: lang.Double = _

  @OneToMany //(cascade=Array(CascadeType.ALL))
  @JoinTable
  var producedSnowflakes: util.Set[Snowflake] = new util.HashSet[Snowflake]

  override def hashCode(): Int = Hashs.compute(kind, length)
}

@Entity
@Access(AccessType.FIELD)
class Snowflake extends HibernateEntityBase[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _
  var description: String = _

  override def hashCode(): Int = Hashs.compute(name)
}
