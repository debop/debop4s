package debop4s.data.slick3.active

import debop4s.data.slick3.active.ActiveDatabase._
import debop4s.data.slick3.active.ActiveDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

import scala.util.Try

/**
 * ActiveDatabaseFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ActiveDatabaseFunSuite extends AbstractSlickFunSuite {

  override protected def beforeAll() = {
    super.beforeAll()
    commit { createSchema() }
  }

  override protected def afterAll() = {
    commit { dropSchema() }
    super.afterAll()
  }

  test("Supplier 저장하기") {

    val (initialCount, persisted) = commit {
      for {
        count <- suppliers.length.result
        p <- Supplier(name = "Acme, Inc").save()
      } yield (count, p)
    }
    persisted.id shouldBe defined

    commit {
      for {
        _ <- persisted.copy(name = "Updated Name").save()
        _ <- suppliers.count.map(_ shouldEqual (initialCount + 1))
        _ <- persisted.delete()
        _ <- suppliers.count.map(_ shouldEqual initialCount)
        _ <- suppliers.deleteAll()
      } yield ()
    }
  }

  test("Beer 저장하기") {

    commit {
      for {
        supplier <- Supplier(name = "Acme, Inc.").save()
        beer1 <- Beer("OB", 3.2, supplier.id.get).save()
        beer2 <- Beer("Kass", 8.8, supplier.id.get).save()

        _ <- beer1.supplier().map { case Some(x) => x shouldEqual supplier }
        _ <- beer2.supplier().map { case Some(x) => x shouldEqual supplier }
        _ <- beer1.friendBeers().map(_.size shouldEqual 1)
        _ <- beer2.friendBeers().map(_.size shouldEqual 1)
      } yield ()
    }
  }

  test("save supplier and beer") {
    val (supplier, beer) = commit {
      for {
        supplier <- Supplier("Acme, Inc").save()
        beer <- Beer("Abc", 3.2, supplier.id.get).save()
        beerSupplier <- beer.supplier()
      } yield {
        beerSupplier.value shouldEqual supplier
        (supplier, beer)
      }
    }
    supplier.id shouldBe defined
  }

  test("사용자가 지정한 Id 값으로 저장하기는 실패한다") {
    val (supplier, triedBeer) = commit {
      for {
        supplier <- Supplier("Acme, Inc.").save()
        beer: Try[Beer] <- Beer("Abc", 3.2, supplier.id.get, Some(10)).save().asTry
      } yield (supplier, beer)
    }

    supplier.id shouldBe defined
    triedBeer.isFailure shouldBe true
    triedBeer.failed.map(_ shouldBe a[RowNotFoundException[_]])
  }
}
