package debop4s.data.orm.jpa.config

import javax.persistence.{EntityManager, PersistenceContext}

import debop4s.data.orm.jpa.ScalaJpaEntity
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.transaction.annotation.Transactional

/**
 * ScalaJpaConfigurationTest
 * Created by debop on 2014. 1. 29.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[ScalaJpaConfiguration]), loader = classOf[AnnotationConfigContextLoader])
@Transactional
class ScalaJpaConfigurationTest extends JUnitSuite {

  @PersistenceContext val em: EntityManager = null

  // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  @Test
  def configurationTest() {
    assert(em != null)
  }

  @Test
  def crud() {
    val entity = new ScalaJpaEntity()
    entity.name = "Sunghyouk Bae"
    em.persist(entity)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[ScalaJpaEntity], entity.getId)
    assert(loaded != null)
    assert(loaded == entity)
    assert(loaded.isPersisted)
  }
}
