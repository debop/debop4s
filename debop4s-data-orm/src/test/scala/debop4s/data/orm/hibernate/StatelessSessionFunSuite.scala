package debop4s.data.orm.hibernate

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.mapping.ScalaEmployee
import org.hibernate.SessionFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._

/**
 * Hibernate SessionFactory 를 직접 이용하여 Stateless Session 을 이용하도록 합니다.
 *
 * @author sunghyouk.bae@gmail.com 2014. 9. 13.
 */
class StatelessSessionFunSuite extends AbstractJpaJUnitSuite {

  @Autowired val sf: SessionFactory = null

  @Test
  def testHibernateStatelessSessionDirectly(): Unit = {
    // debop4s.data.orm.jpa._ 에 있는 StatelessSessionExtensions 클래스를 사용합니다.
    sf.withStateless { stateless =>
      (0 until 10).foreach { i =>
        val emp = new ScalaEmployee()
        emp.empNo = s"empNo-$i"
        emp.name = s"emp-name-$i"

        stateless.insert(emp)
      }
    }

    // debop4s.data.orm.jpa._ 에 있는 StatelessSessionExtensions 클래스를 사용합니다.
    val emps = sf.withStatelessReadOnly { stateless =>
      val crit = stateless.createCriteria(classOf[ScalaEmployee])
      crit.list().asInstanceOf[java.util.List[ScalaEmployee]]
    }.orNull

    emps should not be null
    emps.size should be > 0
    emps.asScala foreach { x => debug(s"emp=$x") }
  }

}
