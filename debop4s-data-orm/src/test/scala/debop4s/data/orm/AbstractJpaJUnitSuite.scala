package debop4s.data.orm

import javax.persistence.EntityManagerFactory

import debop4s.core.Logging
import debop4s.data.orm.config.DataConfiguration
import debop4s.data.orm.mapping.ScalaEmployee
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

/**
 * AbstractJpaJUnitSuite
 * Created by debop on 2014. 1. 29.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[DataConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
abstract class AbstractJpaJUnitSuite
  extends JUnitSuite with Matchers with OptionValues with BeforeAndAfterAll with Logging {

  @Autowired val emf: EntityManagerFactory = null

  override def beforeAll() {
    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  protected def createEmployee(): ScalaEmployee = {
    val emp = new ScalaEmployee()
    emp.empNo = "21011"
    emp.name = "debop"
    emp
  }
}
