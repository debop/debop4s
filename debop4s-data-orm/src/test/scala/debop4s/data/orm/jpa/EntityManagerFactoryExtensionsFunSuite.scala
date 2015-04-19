package debop4s.data.orm.jpa

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.mapping.ScalaEmployee
import org.junit.Test

class EntityManagerFactoryExtensionsFunSuite extends AbstractJpaJUnitSuite {

  @Test
  def withNewEntityManagerTest() {

    val emp = createEmployee()

    emf.withNewEntityManager { em =>
      em.persist(emp)
      em.flush()
    }

    val loaded = emf.withNewEntityManagerReadOnly { em =>
      em.findOne(classOf[ScalaEmployee], emp.getId)
    }
    loaded should not be null
    loaded shouldEqual emp
  }

}
