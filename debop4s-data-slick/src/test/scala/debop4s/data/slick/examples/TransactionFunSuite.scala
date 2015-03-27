package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * TransactionFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 27.
 */
class TransactionFunSuite extends AbstractSlickFunSuite {

  test("transaction") {

    class T(tag: Tag) extends Table[Int](tag, "transaction_t") {
      def a = column[Int]("a")
      def * = a
    }
    lazy val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create
    }

    withTransaction { implicit session =>
      ts.insert(42)
      ts.firstOption shouldEqual Some(42)
      ts.delete
    }

    withRollback { implicit session =>
      ts.insert(24)
      ts.firstOption shouldEqual Some(24)
    }

    withReadOnly { implicit session =>
      ts.firstOption shouldEqual None
    }

    withTransaction { implicit session =>
      ts.insert(1)
      ts.firstOption shouldEqual Some(1)
    }

    withRollback { implicit session =>
      ts.delete
      ts.firstOption shouldEqual None
    }

    withReadOnly { implicit session =>
      ts.firstOption shouldEqual Some(1)
    }
  }

}
