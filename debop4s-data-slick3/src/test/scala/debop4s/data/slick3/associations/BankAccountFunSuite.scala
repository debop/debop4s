package debop4s.data.slick3.associations

import debop4s.core.utils.Closer
import debop4s.data.slick3.associations._
import debop4s.data.slick3._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._

/**
 * BankAccountFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class BankAccountFunSuite extends AbstractSlickFunSuite {

  lazy val accountData = Seq(
    BankAccount(None, "A-1111"),
    BankAccount(None, "A-2222"),
    BankAccount(None, "A-3333")
  )

  lazy val ownerData = Seq(
    AccountOwner(None, "SSN-111"),
    AccountOwner(None, "SSN-222"),
    AccountOwner(None, "SSN-333")
  )

  lazy val accountOwnerMap = Seq(
    BankAccountOwner(1, 1),
    BankAccountOwner(2, 2),
    BankAccountOwner(3, 2)
  )

  lazy val schema = bankAccounts.schema ++ accountOwners.schema ++ bankAccountOwners.schema

  override def beforeAll(): Unit = {
    super.beforeAll()

    db.exec {
      schema.drop.asTry >>
      schema.create >>
      (bankAccounts ++= accountData) >>
      (accountOwners ++= ownerData) >>
      (bankAccountOwners ++= accountOwnerMap)
    }

  }
  override def afterAll(): Unit = {
    db.exec { schema.drop }
    super.afterAll()
  }

  test("implicit join - many-to-many") {

    /*
    ┇ select x2."owner_id", x2."owner_ssn"
    ┇ from "ass_bankaccount" x3, "ass_bank_account_owner" x4, "ass_bankowner" x2
    ┇ where (x4."account_id" = x3."account_id") and (x2."owner_id" = x4."owner_id")
    ┇ group by x2."owner_id", x2."owner_ssn"
     */
    val owners =
      for {
        account <- bankAccounts
        map <- bankAccountOwners if map.accountId === account.id
        owner <- accountOwners if owner.id === map.ownerId
      } yield owner

    // Account Owner 가 join 으로 인해 여러 개가 중복될 수 있다. ( distinct 대신 groupBy 가 제공된다.)
    db.exec { owners.groupBy(identity).map(_._1).result } foreach println
    db.exec { owners.groupBy(identity).map(_._1).length.result } shouldEqual 2

    // TODO: withSession, withTransaction, withRollback 은 구현해야겠다.
    Closer.using(db.createSession()) { s =>
      s.withTransaction {
        try {
          bankAccountOwners.delete
          bankAccounts.delete
          accountOwners.delete
          Thread.sleep(100)
        } finally {
          s.rollback()
        }
      }
    }
    db.exec { accountOwners.length.result } shouldEqual ownerData.size
  }

  test("explicit join") {
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select x5.x6 as x7
    ┇   from (
    ┇     select x8."account_id" as x9, x8."account_num" as x10
    ┇     from "ass_bankaccount" x8
    ┇   ) x11
    ┇   inner join (
    ┇     select x12."account_id" as x13, x12."owner_id" as x6
    ┇     from "ass_bank_account_owner" x12
    ┇   ) x5
    ┇   on x11.x9 = x5.x13
    ┇ ) x14
    ┇ inner join (
    ┇   select x15."owner_id" as x3, x15."owner_ssn" as x4
    ┇   from "ass_bankowner" x15
    ┇ ) x2
    ┇ on x14.x7 = x2.x3
    ┇ group by x2.x3, x2.x4
     */
    val owners = for {
      ((a, m), o) <- bankAccounts join bankAccountOwners on (_.id === _.accountId) join accountOwners on (_._2.ownerId === _.id)
    } yield o

    db.result { owners.groupBy(identity).map(_._1) } foreach println
    db.exec { owners.groupBy(identity).map(_._1).length.result } shouldEqual 2
  }

  test("using pre defined query") {

    val owners = for {
      account <- bankAccounts
      owner <- account.owners
    } yield owner

    /*
    ┇ select x2."owner_id", x2."owner_ssn"
    ┇ from "ass_bankaccount" x3, "ass_bank_account_owner" x4, "ass_bankowner" x2
    ┇ where (x2."owner_id" = x4."owner_id") and (x4."account_id" = x3."account_id")
    ┇ group by x2."owner_id", x2."owner_ssn"
     */
    db.result(owners.groupBy(identity).map(_._1.id)) shouldEqual Seq(1, 2)

    /*
    ┇ select x2."owner_id", count(1)
    ┇ from "ass_bankaccount" x3, "ass_bank_account_owner" x4, "ass_bankowner" x2
    ┇ where (x2."owner_id" = x4."owner_id") and (x4."account_id" = x3."account_id")
    ┇ group by x2."owner_id"
     */
    db.exec { owners.groupBy(_.id).map(x => (x._1, x._2.length)).result } shouldEqual Seq((1, 1), (2, 2))
  }


}
