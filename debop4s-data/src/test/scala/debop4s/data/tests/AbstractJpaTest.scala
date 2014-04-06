package debop4s.data.tests

import debop4s.data.tests.spring.JpaMySqlConfiguration
import javax.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}

/**
 * debop4s.data.tests.AbstractJpaTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 26. 오전 10:29
 */
@ContextConfiguration(classes = Array(classOf[JpaMySqlConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
abstract class AbstractJpaTest extends AbstractDataTest {

  @Autowired protected val emf: EntityManagerFactory = null

  new TestContextManager(this.getClass).prepareTestInstance(this)

}
