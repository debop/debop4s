package debop4s.data.tests.jpa.config

import javax.persistence.{ EntityManager, EntityManagerFactory, PersistenceContext }

import debop4s.data.jpa.repository.JpaDao
import debop4s.data.tests.AbstractDataTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

/**
 * ScalaJpaTest 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
@Transactional
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[ScalaJpaConfiguration]))
class ScalaJpaTest extends AbstractDataTest {

  @Autowired val emf: EntityManagerFactory = null
  @PersistenceContext val em: EntityManager = null
  @Autowired val dao: JpaDao = null

  @Test
  def setupTest() {
    assert(emf != null)
    assert(em != null)
    assert(dao != null)
  }

  @Test
  def crud() {
    val entity = new ScalaJpaEntity
    entity.name = "Sunghyouk Bae"

    dao.persist(entity)
    dao.flush()
    dao.clear()

    val loaded = dao.findOne(classOf[ScalaJpaEntity], entity.id)
    assert(loaded != null)
    assert(loaded == entity)
    assert(loaded.isPersisted)
    assert(loaded.id > 0)
    println(loaded.toString)
  }

}
