package debop4s.data.orm.mapping

import javax.persistence.{EntityManager, PersistenceContext}

import debop4s.data.orm.AbstractJpaJUnitSuite
import org.junit.Test

class MappingJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  def configurationSetup() {
    assert(em != null)
  }
}
