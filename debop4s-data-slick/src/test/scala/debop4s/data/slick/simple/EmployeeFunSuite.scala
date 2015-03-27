package debop4s.data.slick.simple

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.associations.model._
import debop4s.data.slick.associations.schema.AssociationDatabase._
import debop4s.data.slick.associations.schema.AssociationDatabase.driver.simple._
import debop4s.data.slick.customtypes.EncryptedString
import org.joda.time.DateTime

import scala.util.Try

/**
 * EmployeeFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 27.
 */
class EmployeeFunSuite extends AbstractSlickFunSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()

    withTransaction { implicit session =>
      Try { Employees.ddl.drop }
      Employees.ddl.create
    }
  }

  override def afterAll(): Unit = {
    withTransaction { implicit session =>
      Try { Employees.ddl.drop }
    }
    super.afterAll()
  }

  test("Employee 정보 저장 및 로드") {
    LOG.debug(s"Employee Schema:\n${ Employees.ddl.createStatements.mkString("\n") }")

    withSession { implicit session =>
      val initialCount = Employees.count

      // 8818cb127baefb8b1....
      val emp = Employee(None, "Sunghyouk Bae", "21011", EncryptedString("debop"), new DateTime(2013, 6, 1, 0, 0))
      emp.id should not be defined
      emp.isPersisted shouldEqual false

      val persistedEmp = Employees.save(emp)
      persistedEmp.isPersisted shouldEqual true
      persistedEmp.password.text shouldEqual "debop"

      Employees.list foreach { emp => LOG.debug(s"  $emp") }

      // H2:
      // select x2.x3 from (select count(1) as x3
      //                      from (select x4."emp_no" as x5, x4."hire_date" as x6, x4."emp_id" as x7, x4."emp_name" as x8, x4."emp_passwd" as x9
      //                              from "ass_employees" x4 where x4."emp_passwd" = 'bb48ad020e1c2027') x10) x2
      Employees.filter(_.password === EncryptedString("debop")).length.run should be > 0

      // select x2.x3 from (select count(1) as x3
      //                      from (select x4."emp_no" as x5, x4."hire_date" as x6, x4."emp_id" as x7, x4."emp_name" as x8, x4."emp_passwd" as x9
      //                              from "ass_employees" x4) x10) x2
      Employees.count shouldEqual ( initialCount + 1 )
    }
  }

}
