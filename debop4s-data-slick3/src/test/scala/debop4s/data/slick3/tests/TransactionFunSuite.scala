package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}
import slick.dbio.{Effect, NoStream, SynchronousDatabaseAction}
import slick.jdbc.{JdbcBackend, TransactionIsolation}
import slick.util.DumpInfo

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * TransactionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class TransactionFunSuite extends AbstractSlickFunSuite {

  test("trasnactions") {
    class T(tag: Tag) extends Table[Int](tag, "t") {
      def a = column[Int]("a", O.PrimaryKey)
      def * = a
    }
    val ts = TableQuery[T]

    val getTI = SimpleDBIO(_.connection.getTransactionIsolation)

    class ExpectedException extends RuntimeException

    db.exec { ts.schema.create }
    db.exec {
      {
        (for {
          _ <- ts += 1
          _ <- ts.result.map(_ shouldEqual Seq(1))
          _ <- GetTransactionality.map(_ shouldBe(1, false))
          _ = throw new ExpectedException
        } yield ()).transactionally.failed.map(_.isInstanceOf[ExpectedException] shouldEqual true)
      } >> {
        ts.result.map(_ shouldEqual Nil) >>
        GetTransactionality.map(_ shouldEqual(0, true))
      } >> {
        // successful transaction
        (for {
          _ <- ts += 2
          _ <- ts.result.map(_ shouldEqual Seq(2))
          _ <- GetTransactionality.map(_ shouldEqual(1, false))
        } yield ()).transactionally
      } >> {
        (for {
          _ <- ts += 3
          _ <- ts.to[Set].result.map(_ shouldEqual Set(2, 3))
          _ <- GetTransactionality.map(_ shouldEqual(2, false))
        } yield ()).transactionally.transactionally
      } >> {
        ts.to[Set].result.map(_ shouldEqual Set(2, 3))
      } >> {
        // failed nested transaction
        (for {
          _ <- ts += 4
          _ <- ts.to[Set].result.map(_ shouldEqual Set(2, 3, 4))
          _ <- GetTransactionality.map(_ shouldEqual(2, false))
          _ = throw new ExpectedException
        } yield ()).transactionally.transactionally.failed.map(_.isInstanceOf[ExpectedException] should be(true))
      } >> {
        // fused successful transaction
        (ts += 5).andThen(ts += 6).transactionally
      } >> {
        ts.to[Set].result.map(_ shouldEqual Set(2, 3, 5, 6)) >>
        GetTransactionality.map(_ shouldEqual(0, true))
      } >> {
        // fused failed transaction
        (ts += 7).andThen(ts += 6).transactionally.failed
      } >> {
        ts.to[Set].result.map(_ shouldEqual Set(2, 3, 5, 6)) >>
        GetTransactionality.map(_ shouldEqual(0, true))
      } >> {
        ifCap(tcap.transactionIsolation) {
          (for {
            ti1 <- getTI
            _ <- (for {
              _ <- getTI.map(_ should be >= TransactionIsolation.ReadUncommitted.intValue)
              _ <- getTI.withTransactionIsolation(TransactionIsolation.Serializable).map(_ should be >= TransactionIsolation.Serializable.intValue)
              _ <- getTI.map(_ should be >= TransactionIsolation.ReadUncommitted.intValue)
            } yield ()).withTransactionIsolation(TransactionIsolation.ReadUncommitted)
            _ <- getTI.map(_ shouldEqual ti1)
          } yield ()).withPinnedSession
        }
      }
    }

    db.exec { ts.schema.drop }
  }

}

/** Test Action: Get the current transactionality level and autoCommit flag */
object GetTransactionality extends SynchronousDatabaseAction[(Int, Boolean), NoStream, JdbcBackend, Effect] {
  def run(context: JdbcBackend#Context) =
    context.session.asInstanceOf[JdbcBackend#BaseSession].getTransactionality
  def getDumpInfo = DumpInfo(name = "<GetTransactionality>")
}
