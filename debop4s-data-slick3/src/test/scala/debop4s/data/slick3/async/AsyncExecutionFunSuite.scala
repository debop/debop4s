package debop4s.data.slick3.async

import debop4s.data.slick3._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.associations._
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._
import debop4s.data.slick3.customtypes.EncryptedString
import org.joda.time.DateTime
import scala.async.Async._

/**
 * AsyncExecutionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class AsyncExecutionFunSuite extends AbstractSlickFunSuite {

  val EMP_COUNT = 1000
  val rnd = scala.util.Random
  lazy val schema = employees.schema

  before {
    { schema.drop.asTry >> schema.create }.exec
  }
  after {
    schema.drop.exec
  }

  test("employee 병렬 저장") {
    val saveActions = (0 until EMP_COUNT) map { x =>
      val empNo = x.toString
      val hireDate = DateTime.now
      employees += Employee(s"name=$empNo", s"no-$empNo", EncryptedString(s"pwd-$empNo"), hireDate)
    }
    saveActions.exec

    commit { employees.count() } shouldEqual EMP_COUNT
    employees.exec foreach { emp => LOG.debug(emp.toString) }
  }
}
