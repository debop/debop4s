package debop4s.data.orm.config

import javax.persistence.{EntityManager, EntityManagerFactory, PersistenceContext}

import debop4s.core.Logging
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.transaction.annotation.Transactional


@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[DataConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class DataConfigurationJUnitSuite extends JUnitSuite with Matchers with Logging with BeforeAndAfterAll {

  @Autowired val emf: EntityManagerFactory = null
  @PersistenceContext val em: EntityManager = null

  override def beforeAll() {
    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  @Test
  def testConfiguration(): Unit = {
    emf should not be null
  }

  @Test
  @Transactional
  def testPersistenceContext(): Unit = {
    emf should not be null
    em should not be null
  }
}
