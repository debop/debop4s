package debop4s.data.slick3.associations

import debop4s.data.slick3.{AbstractSlickFunSuite, _}
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._

/**
 * BankAccountFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class BankAccountFunSuite extends AbstractSlickFunSuite {

  lazy val accountData = Seq(
    BankAccount("A-1111"),
    BankAccount("A-2222"),
    BankAccount("A-3333")
  )

  lazy val ownerData = Seq(
    AccountOwner("SSN-111"),
    AccountOwner("SSN-222"),
    AccountOwner("SSN-333")
  )

  lazy val accountOwnerMap = Seq(
    BankAccountOwner(1, 1),
    BankAccountOwner(2, 2),
    BankAccountOwner(3, 2)
  )

  lazy val schema = bankAccounts.schema ++ accountOwners.schema ++ bankAccountOwners.schema

  override def beforeAll(): Unit = {
    super.beforeAll()

    commit {
      schema.drop.asTry >>
      schema.create >>
      (bankAccounts ++= accountData) >>
      (accountOwners ++= ownerData) >>
      (bankAccountOwners ++= accountOwnerMap)
    }
  }
  override def afterAll(): Unit = {
    commit { schema.drop }
    super.afterAll()
  }

  test("implicit join - many-to-many") {
    val owners =
      for {
        account <- bankAccounts
        map <- bankAccountOwners if map.accountId === account.id
        owner <- accountOwners if owner.id === map.ownerId
      } yield owner

    // Account Owner 가 join 으로 인해 여러 개가 중복될 수 있다. ( distinct 대신 groupBy 가 제공된다.)
    val q = owners.groupBy(identity).map(_._1)
    commit {
      for {
        owners <- q.result
        _ <- q.length.result.map(_ shouldEqual 2)
      } yield {
        owners foreach { owner => log.debug(s"owner=$owner") }
        ()
      }
    }

    //    db.withTransaction { session =>
    //      try {
    //        bankAccountOwners.delete
    //        bankAccounts.delete
    //        accountOwners.delete
    //        Thread.sleep(100)
    //      } finally {
    //        session.rollback()
    //      }
    //    }
    //    // TODO: withSession, withTransaction, withRollback 은 구현해야겠다.
    //    Closer.using(db.createSession()) { s =>
    //      s.withTransaction {
    //        try {
    //          bankAccountOwners.delete
    //          bankAccounts.delete
    //          accountOwners.delete
    //          Thread.sleep(100)
    //        } finally {
    //          s.rollback()
    //        }
    //      }
    //    }
    //    accountOwners.count shouldEqual ownerData.size
  }

  test("explicit join") {
    val qOwners = for {
      ((a, m), o) <- bankAccounts join bankAccountOwners on (_.id === _.accountId) join accountOwners on (_._2.ownerId === _.id)
    } yield o

    val q = qOwners.groupBy(identity).map(_._1)
    commit {
      for {
        owners <- q.result
        length <- q.length.result.map(_ shouldEqual 2)
      } yield {
        owners foreach { owner => log.debug(s"owner=$owner") }
        ()
      }
    }

  }

  test("using pre defined query") {

    val qOwners = for {
      account <- bankAccounts.sortBy(_.id.asc)
      owner <- account.owners
    } yield owner

    val q1 = qOwners.groupBy(identity).map(_._1.id).sorted
    val q2 = qOwners.groupBy(_.id).map(x => (x._1, x._2.length)).sortBy(_._1)

    commit {
      for {
        _ <- q1.result.map(_ shouldEqual Seq(1, 2))
        _ <- q2.result.map(_ shouldEqual Seq((1, 1), (2, 2)))
      } yield ()
    }
  }

}
