package debop4s.data.slick.concurrent


import debop4s.core.concurrent.Asyncs
import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.associations.model.Employee
import debop4s.data.slick.associations.schema.AssociationDatabase._
import debop4s.data.slick.associations.schema.AssociationDatabase.driver.simple._
import debop4s.data.slick.customtypes.EncryptedString
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
 * AsyncExecutionFunSuite
 * @author sunghyouk.bae@gmail.com at 15. 3. 23.
 */
class AsyncExecutionFunSuite extends AbstractSlickFunSuite {

  val EMP_COUNT = 1000
  lazy val ddl = employees.ddl

  before {
    withTransaction { implicit session =>
      Try { ddl.drop }
      ddl.create
    }
  }
  after {
    withTransaction { implicit session =>
      Try { ddl.drop }
    }
  }

  test("Save and Load Employees by Async") {
    val saveActions = ( 0 until EMP_COUNT ) map { x =>
      LOG.trace(s"직원을 생성합니다. x=$x")

      val empNo = x.toString
      val hireDate = DateTime.now
      Future {
        saveEmployee(Employee(None, s"emp-$empNo", empNo, EncryptedString(s"pwd-$empNo"), hireDate))
      }
    }
    Asyncs.readyAll(saveActions)

    withReadOnly { implicit session =>
      employees.count shouldEqual EMP_COUNT
    }
  }

  private def saveEmployee(emp: Employee): Employee = {
    withTransaction { implicit session =>
      employees.save(emp)
    }
  }

}
