package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._
import debop4s.data.slick3.SlickContext._
import slick.backend.DatabasePublisher
import slick.util.TupleMethods._

import scala.concurrent.Future

/**
 * PagingFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PagingFunSuite extends AbstractSlickFunSuite {

  class IDs(tag: Tag, name: String) extends Table[Int](tag, name) {
    def id = column[Int]("id", O.PrimaryKey)
    def * = id
  }

  test("row pagination") {
    lazy val ids = TableQuery(new IDs(_, "ids_row"))
    val q1 = ids.sortBy(_.id)
    val q2 = q1 take 5
    val q3 = q1 drop 5
    val q4 = q1 drop 5 take 3
    val q5 = q1 take 5 drop 3
    val q6 = q1 take 0

    db.seq(
      ids.schema.drop.asTry,
      ids.schema.create,
      ids ++= (1 to 10),
      q1.result.map(_ shouldEqual (1 to 10).toSeq),
      q2.result.map(_ shouldEqual (1 to 5).toSeq),
      ifCap(rcap.pagingDrop) {
        DBIO.seq(
          q3.result.map(_ shouldEqual (6 to 10).toSeq),
          q4.result.map(_ shouldEqual (6 to 8).toSeq),
          q5.result.map(_ shouldEqual (4 to 5).toSeq)
        )
      },
      ids.schema.drop
    )
  }

  test("compiled pagination") {
    lazy val ids = TableQuery(new IDs(_, "ids_compiled"))
    val q = Compiled { (offset: ConstColumn[Long], fetch: ConstColumn[Long]) =>
      ids.sortBy(_.id).drop(offset).take(fetch)
    }

    db.seq(
      ids.schema.drop.asTry,
      ids.schema.create,
      ids ++= (1 to 10),
      q(0, 5).result.map(_ shouldEqual (1 to 5).toSeq),
      ifCap(rcap.pagingDrop) {
        DBIO.seq(
          q(5, 1000).result.map(_ shouldEqual (6 to 10).toSeq),
          q(5, 3).result.map(_ shouldEqual (6 to 8).toSeq)
        )
      },
      ids.schema.drop
    )
  }

}
