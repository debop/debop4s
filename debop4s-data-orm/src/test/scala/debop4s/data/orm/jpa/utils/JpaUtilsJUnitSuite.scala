package debop4s.data.orm.jpa.utils

import javax.persistence.{EntityManager, PersistenceContext}

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.mapping.ScalaEmployee
import org.junit.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class JpaUtilsJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null

  @Test
  @Transactional
  def testCurrentConnection(): Unit = {
    em should not be null

    val connection = JpaUtils.currentConnection(em)
    connection should not be null
    connection.isReadOnly shouldEqual false
  }

  @Test
  @Transactional
  def withReadOnlyEntityManager(): Unit = {

    val emp = createEmployee()

    em.persist(emp)
    em.flush()

    val loaded = JpaUtils.withReadOnly(em) {
      em.find(classOf[ScalaEmployee], emp.getId)
    }
    loaded should not be null
    loaded shouldEqual emp
  }

  @Test
  @Transactional
  def withStateless(): Unit = {
    val emp = createEmployee()

    val id = JpaUtils.withStateless(em) { stateless =>
      stateless.insert(emp)
    }

    val loaded = JpaUtils.withStatelessReadOnly(em) { stateless =>
      stateless.get(classOf[ScalaEmployee], id)
    }
    loaded should not be null
    loaded shouldEqual emp
  }
}
