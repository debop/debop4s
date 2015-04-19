package debop4s.data.orm.stateless

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.jpa.repository.JpaDao
import debop4s.data.orm.mapping.ScalaEmployee
import org.hibernate.StatelessSession
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

/**
 * JPA 환경하에서 Stateless Session을 사용할 수 있도록 합니다.
 * NOTE: JPA용 Stateless Session은 Transaction 환경 하에서 실행해야 합니다.
 *
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
class JpaStatelessSessionFactoryJUnitSuite extends AbstractJpaJUnitSuite {

  // Jpa Configuration 의 StatelessSessionFactoryBean 에서 생성합니다.
  @Autowired val stateless: StatelessSession = null
  @Autowired val jpa: JpaDao = null

  @Test
  @Transactional(readOnly = true)
  def testAutowiredStatelessStession(): Unit = {
    stateless should not be null
    info(s"current stateless session = ${ stateless.toString }")
  }

  @Test
  @Transactional(readOnly = true)
  def testStatelessSession(): Unit = {
    stateless should not be null

    val crit = stateless.createCriteria(classOf[ScalaEmployee])
    val cursor = crit.scroll()
    while (cursor.next) {
      log.debug(s"ScalaEmployee=${ cursor.get(0) }")
    }
    cursor.close()
  }

  @Test
  @Transactional
  def testInsertWithStateless(): Unit = {
    jpa should not be null
    stateless should not be null

    // val stateless1 = stateless
    (0 until 100).foreach { i =>
      val emp = new ScalaEmployee()
      emp.empNo = s"empNo-$i"
      emp.name = s"emp-name-$i"

      stateless.insert(emp)
    }

    // 직접 읽기 전용으로 만든다. @Transactional 과는 상관없다.
    val conn = stateless.connection()
    val readOnly = conn.isReadOnly
    try {
      val crit = stateless.createCriteria(classOf[ScalaEmployee])
      val emps = crit.list().asInstanceOf[java.util.List[ScalaEmployee]]
      emps should not be null
      emps.size should be > 0
      emps.asScala foreach { x => log.debug(s"employee=$x") }
    } finally {
      conn.setReadOnly(readOnly)
    }
  }

}
