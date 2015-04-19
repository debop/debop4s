package debop4s.data.orm.stateless

import javax.transaction.Transactional

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.jpa._
import debop4s.data.orm.mapping.ScalaEmployee
import org.hibernate.StatelessSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback

import scala.collection.JavaConverters._

/**
 * debop4s.data.orm.jpa.StatelessSessionExtensions 를 테스트 합니다.
 */
class StatelessSessionExtensionsJUnitSuite extends AbstractJpaJUnitSuite {

  @Autowired val stateless: StatelessSession = null

  @Test
  // NOTE: @Transactional 는 Spring 환경하에서 current thread에 entity manager 를 생성하게 하기 위해 필요하다.
  @Transactional
  @Rollback(false)
  def usingExtensions(): Unit = {

    // debop4s.data.orm.jpa._ 에 있는 StatelessSessionExtensions 클래스를 사용합니다.
    stateless.withTransaction {
      (0 until 10).foreach { i =>
        val emp = new ScalaEmployee()
        emp.empNo = s"empNo-$i"
        emp.name = s"emp-name-$i"

        stateless.insert(emp)
      }
    }

    // debop4s.data.orm.jpa._ 에 있는 StatelessSessionExtensions 클래스를 사용합니다.
    val emps = stateless.withReadOnly {
      val crit = stateless.createCriteria(classOf[ScalaEmployee])
      crit.list().asInstanceOf[java.util.List[ScalaEmployee]]
    }
    emps should not be null
    emps.size should be > 0
    emps.asScala foreach { x => log.debug(s"emp=$x") }
  }


  @Test
  @Transactional
  def usingDao(): Unit = {

    // HINT: EmployeeDao 는 Spring Bean이 아니므로 entity manager를 사용하기 위해서 @Transactional을 정의해줘야 합니다.
    val dao = new EmployeeDao()

    val emp = new ScalaEmployee()
    emp.empNo = "1234567"
    emp.name = "abcdefg"

    val empId = dao.save(emp)
    val loaded = dao.get(empId)

    loaded should not be null
    loaded.empNo shouldEqual emp.empNo
    loaded.name shouldEqual emp.name
  }

  final class EmployeeDao {

    def save(emp: ScalaEmployee): java.lang.Long = {
      stateless.withTransaction {
        stateless.insert(emp).asInstanceOf[java.lang.Long]
      }
    }

    def get(empId: java.lang.Long): ScalaEmployee = {
      stateless.withReadOnly {
        stateless.get(classOf[ScalaEmployee], empId).asInstanceOf[ScalaEmployee]
      }
    }
  }
}
