package debop4s.data.slick3.associations

import debop4s.data.slick3.{AbstractSlickFunSuite, _}
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._
import debop4s.data.slick3.customtypes.EncryptedString
import org.joda.time.DateTime

/**
 * EmployeeFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class EmployeeFunSuite extends AbstractSlickFunSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    employees.schema.create.exec
  }

  override def afterAll(): Unit = {
    employees.schema.drop.exec
    super.afterAll()
  }

  test("save and load Employee") {
    val initialCount = commit { employees.count }

    val emp = Employee("Sunghyouk Bae", "11111", EncryptedString("debop"), new DateTime(2015, 4, 9, 0, 0))
    emp.id should not be defined
    // emp.isPersisted shouldBe false

    val persistedEmp = commit { employees.save(emp) }
    persistedEmp.id shouldBe defined
    persistedEmp.isPersisted shouldBe true
    persistedEmp.password.text shouldEqual "debop"

    val emps = readonly { employees.result }
    emps foreach { emp => LOG.debug(s"\t$emp") }

    // EncryptedString 처럼 Mapping 하는 것도 가능하다.
    employees.filter(_.password === EncryptedString("debop")).length.exec should be > 0

    commit { employees.count } shouldBe (initialCount + 1)
  }

}
