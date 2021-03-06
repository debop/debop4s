package debop4s.data.slick.active

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.active.ActiveDatabase._

/**
 * ActiveFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
class ActiveFunSuite extends AbstractSlickFunSuite {

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    withSession { implicit session =>
      createSchema
    }
  }

  test("Supplier 저장하기") {
    val initialCount = withReadOnly { implicit session => suppliers.count }

    var persisted: Supplier = null

    withTransaction { implicit session =>
      val supplier = Supplier(name = "Acme, Inc.")
      supplier.id should not be defined

      persisted = supplier.save
      persisted.id shouldBe defined
    }

    withSession { implicit session =>
      suppliers.count shouldEqual ( initialCount + 1 )
      persisted.delete
      suppliers.count shouldEqual initialCount
    }

  }

  test("Beer 저장하기") {
    withSession { implicit session =>
      val supplier = suppliers.save(Supplier(name = "Acme, Inc."))
      supplier.id shouldBe defined

      supplier.id.foreach { sid =>
        val beer = Beer(name = "Abc", supplierId = sid, price = 3.2).save
        beer.supplier.get shouldEqual supplier
      }
    }
  }


}
