package debop4s.data.tests

import javax.persistence.EntityManagerFactory

import debop4s.data.tests.spring.JpaH2Configuration
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * debop4s.data.tests.AbstractJpaTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 26. 오전 10:29
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[JpaH2Configuration]))
abstract class AbstractJpaTest extends AbstractDataTest {

  @Autowired protected val emf: EntityManagerFactory = null

}
