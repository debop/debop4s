package debop4s.data.slick3.active

import debop4s.data.slick3.{AbstractSlickFunSuite, _}
import debop4s.data.slick3.active.ActiveDatabase._
import debop4s.data.slick3.active.ActiveDatabase.driver.api._

/**
 * ActiveDatabaseFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ActiveDatabaseFunSuite extends AbstractSlickFunSuite {

  override protected def beforeAll() = {
    super.beforeAll()
    createSchema().commit
  }

  override protected def afterAll() = {
    dropSchema().commit
    super.afterAll()
  }

  test("Supplier 저장하기") {
    val initialCount = db.exec(suppliers.length.result)

    val supplier = Supplier(name = "Acme, Inc")
    supplier.id should not be defined

    val persisted = supplier.save.commit
    persisted.id shouldBe defined

    persisted.copy(name = "Updated Name").save

    suppliers.count.commit shouldEqual (initialCount + 1)
    persisted.delete().commit
    suppliers.count.commit shouldEqual initialCount

    suppliers.deleteAll().commit
  }

  //  test("Supplier 저장하기 2") {
  //
  //    val (initialCount, persisted) = commit {
  //      for {
  //        count <- suppliers.length.result
  //        p <- Supplier(name = "Acme, Inc").save()
  //      } yield (count, p)
  //    }
  //
  //    persisted.id shouldBe defined
  //
  //    persisted.copy(name = "Updated Name").save
  //
  //    suppliers.count.commit shouldEqual (initialCount + 1)
  //    persisted.delete().commit
  //    suppliers.count.commit shouldEqual initialCount
  //
  //    suppliers.deleteAll().commit
  //  }

  test("Beer 저장하기") {
    val supplier = suppliers.save(Supplier(name = "Acme, Inc.")).commit
    supplier.id shouldBe defined

    LOG.debug(s"Saved Supplier=$supplier")

    supplier.id.foreach { sid =>
      val beer1 = Beer(name = "OB", supplierId = sid, price = 3.2).save.commit
      beer1.supplier.commit.get shouldEqual supplier

      val beer2 = Beer(name = "Kass", supplierId = sid, price = 8.8).save.commit
      beer2.supplier.commit.get shouldEqual supplier

      beer1.friendBeers.commit.size shouldEqual 1
      beer2.friendBeers.commit.size shouldEqual 1
    }
  }
}
