package debop4s.data.slick.associations

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.associations.model._
import debop4s.data.slick.associations.schema.AssociationDatabase._
import debop4s.data.slick.associations.schema.AssociationDatabase.driver.simple._

import scala.util.Try

/**
 * BankAccountFunSuite
 * @author sunghyouk.bae@gmail.com at 15. 3. 23.
 */
class BankAccountFunSuite extends AbstractSlickFunSuite {


  lazy val accountData = Seq(BankAccount(None, "A-1111"),
                              BankAccount(None, "A-2222"),
                              BankAccount(None, "A-3333"))

  lazy val ownerData = Seq(AccountOwner(None, "SSN-111"),
                            AccountOwner(None, "SSN-222"),
                            AccountOwner(None, "SSN-333"))

  lazy val accountOwnerMap = Seq(BankAccountOwner(1, 1), BankAccountOwner(2, 2), BankAccountOwner(3, 2))

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val ddl = BankAccounts.ddl ++ AccountOwners.ddl ++ BankAccountOwners.ddl

    withSession { implicit session =>
      Try { ddl.drop }
    }

    withTransaction { implicit session =>
      ddl.create

      BankAccounts ++= accountData
      AccountOwners ++= ownerData
      BankAccountOwners ++= accountOwnerMap
    }
  }

  test("implicit join - many-to-many") {
    withReadOnly { implicit session =>
      val owners = for {
        account <- BankAccounts
        map <- BankAccountOwners if map.accountId === account.id
        owner <- AccountOwners if owner.id === map.ownerId
      } yield owner

      // Account Owner 가 join 으로 인해 여러 개가 중복될 수 있다. ( distinct 대신 groupBy 가 제공된다.)
      owners.groupBy(x => x).map(_._1) foreach println
    }

    // 삭제 처리를 시도해본다. (rollback 하므로 실제 데이터에는 영향은 없다)
    withRollback { implicit session =>
      BankAccountOwners.delete
      BankAccounts.delete
      AccountOwners.delete
    }
  }

  test("explicit inner join") {
    withReadOnly { implicit session =>
      val owners = for {
        (account, map) <- BankAccounts innerJoin BankAccountOwners on ( _.id === _.accountId )
        owner <- AccountOwners if owner.id === map.ownerId
      } yield owner

      owners.groupBy(x => x).map(_._1) foreach println
    }
  }

  test("using pre defined query") {
    val qOwners = for {
      account <- BankAccounts
      owner <- account.owners
    } yield owner

    withReadOnly { implicit session =>
      // select distinct(*) from AccountOwners
      qOwners.groupBy(x => x).map(_._1).list foreach println

      // select id, count(*) from AccountOwners group by id
      qOwners.groupBy(_.id).map(x => (x._1, x._2.length)).list foreach println
    }
  }
}
