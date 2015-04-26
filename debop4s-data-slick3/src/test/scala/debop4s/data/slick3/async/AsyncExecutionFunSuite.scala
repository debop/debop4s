package debop4s.data.slick3.async

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._
import debop4s.data.slick3.associations._
import debop4s.data.slick3.customtypes.EncryptedString
import org.joda.time.DateTime

/**
 * AsyncExecutionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class AsyncExecutionFunSuite extends AbstractSlickFunSuite {

  val EMP_COUNT = 1000
  val rnd = scala.util.Random
  lazy val schema = employees.schema

  before {
    commit {
      schema.drop.asTry >>
      schema.create
    }
  }
  after {
    commit { schema.drop }
  }

  test("employee 병렬 저장") {
    val saveActions = (1 to EMP_COUNT).par.map { x =>
      val empNo = x.toString
      val hireDate = DateTime.now
      employees += Employee(s"name=$empNo", s"no-$empNo", EncryptedString(s"pwd-$empNo"), hireDate)
    }.seq

    commit { DBIO.seq(saveActions: _*) }


    readonly { employees.count() } shouldEqual EMP_COUNT
    readonly { employees.result } foreach { emp => log.debug(emp.toString) }
  }
}
