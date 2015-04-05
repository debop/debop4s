package debop4s.data.slick3.active

import debop4s.data.slick3._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.active.ActiveDatabase._
import debop4s.data.slick3.active.ActiveDatabase.driver.api._

/**
 * ActiveDatabaseFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ActiveDatabaseFunSuite extends AbstractSlickFunSuite {

  override protected def beforeAll() = {
    super.beforeAll()

    createSchema()
  }

  override protected def afterAll() = {
    dropSchema()

    super.afterAll()
  }

  test("Supplier 저장하기") {
    val initialCount = db.exec(suppliers.length.result)

    var persisted: Supplier = null

    val supplier = Supplier(name = "Acme, Inc")
    supplier.id should not be defined

    db.exec(suppliers += supplier)

  }

  test("Beer 저장하기") {

  }

}
