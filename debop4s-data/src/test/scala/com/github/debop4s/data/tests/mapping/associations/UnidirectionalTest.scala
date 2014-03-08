package com.github.debop4s.data.tests.mapping.associations

import com.github.debop4s.core.utils.Hashs
import com.github.debop4s.data.model.HibernateEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.lang
import java.util
import javax.persistence._
import org.junit.Test
import scala.collection.JavaConversions._

/**
 * Created by debop on 2014. 3. 8..
 */
@org.springframework.transaction.annotation.Transactional
class UnidirectionalTest extends AbstractJpaTest {

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

    assert(cloud2.producedSnowflakes.forall(sf => sf.description != removedSf.description))
    assert(cloud2.producedSnowflakes.exists(sf => sf.description == sf3.description))

    cloud2.producedSnowflakes.foreach(em.remove)
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
class Cloud extends HibernateEntity[lang.Long] {
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
class Snowflake extends HibernateEntity[lang.Long] {

  @Id
  @GeneratedValue
  var id: lang.Long = _

  def getId = id

  var name: String = _
  var description: String = _

  override def hashCode(): Int = Hashs.compute(name)
}
