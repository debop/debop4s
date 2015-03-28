package debop4s.data.tests.mapping

import javax.persistence.{ EntityManager, PersistenceContext }

import debop4s.data.tests.AbstractDataTest
import debop4s.data.tests.jpa.config.ScalaJpaConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.{ Propagation, Transactional }

/**
 * debop4s.data.tests.mapping.MappingTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 11. 오후 10:54
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[ScalaJpaConfiguration]))
class MappingTest extends AbstractDataTest {

  @PersistenceContext val em: EntityManager = null
  @Autowired val jdbcTemplate: JdbcTemplate = null

  @Test
  def configurationSetup() {
    assert(em != null)
    assert(jdbcTemplate != null)
  }
}
