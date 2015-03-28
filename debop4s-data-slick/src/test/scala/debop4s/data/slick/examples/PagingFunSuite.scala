package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * PagingFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PagingFunSuite extends AbstractSlickFunSuite {

  class IDs(tag: Tag) extends Table[Int](tag, "paging_ids") {
    def id = column[Int]("id", O.PrimaryKey)
    def * = id
  }
  lazy val ids = TableQuery[IDs]

  test("paging") {
    withSession { implicit session =>
      Try { ids.ddl.drop }
      ids.ddl.create

      ids ++= ( 1 to 10 )

      val q1 = ids.sortBy(_.id)
      q1.run shouldEqual ( 1 to 10 ).toList

      val q2 = q1 take 5
      q2.run shouldEqual ( 1 to 5 ).toList

      ifCap(rcap.pagingDrop) {
        val q3 = q1 drop 5
        q3.run shouldEqual ( 6 to 10 ).toList

        val q4 = q1 drop 5 take 3
        q4.run shouldEqual ( 6 to 8 ).toList

        val q5 = q1 take 5 drop 3
        q5.run shouldEqual ( 4 to 5 ).toList
      }

      val q6 = q1 take 0
      q6.run shouldEqual Seq()
    }
  }
}
