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

  before {
    commit {
      employees.schema.drop.asTry
      employees.schema.create
    }
  }
  after {
    commit { employees.schema.drop }
  }

  test("save and load employees") {

    val emp = Employee("Sunghyouk Bae", "11111", EncryptedString("debop"), new DateTime(2015, 4, 9, 0, 0))
    emp.id should not be defined

    commit {
      for {
        initialCount <- employees.count()
        persistedEmp <- employees.save(emp)
        emps <- employees.result
        _ <- employees.filter(_.password === EncryptedString("debop")).length.result.map(_ should be > 0)
        _ <- employees.count().map(_ shouldEqual (initialCount + 1))
      } yield ()
    }
  }

}
