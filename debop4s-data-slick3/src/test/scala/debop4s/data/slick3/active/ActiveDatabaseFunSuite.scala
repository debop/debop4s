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

    persisted = suppliers.save(supplier) // supplier.save
    persisted.id shouldBe defined

    persisted.copy(name = "Updated Name").save

    suppliers.count shouldEqual (initialCount + 1)
    persisted.delete
    suppliers.count shouldEqual initialCount

    suppliers.delete.exec
  }

  test("Beer 저장하기") {
    val supplier = suppliers.save(Supplier(name = "Acme, Inc."))
    supplier.id shouldBe defined

    LOG.debug(s"Saved Supplier=$supplier")

    supplier.id.foreach { sid =>
      val beer1 = Beer(name = "OB", supplierId = sid, price = 3.2).save
      beer1.supplier.get shouldEqual supplier

      val beer2 = Beer(name = "Kass", supplierId = sid, price = 8.8).save
      beer2.supplier.get shouldEqual supplier

      beer1.friendBeers.size shouldBe 1
      beer2.friendBeers.size shouldBe 1
    }
  }
}
