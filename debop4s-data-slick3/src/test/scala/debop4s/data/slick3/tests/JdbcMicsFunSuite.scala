package debop4s.data.slick3.tests


import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._


/**
 * JdbcMicsFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcMicsFunSuite extends AbstractSlickFunSuite {

  test("simple DBIO") {
    readonly(SimpleDBIO[Boolean](_.connection.getAutoCommit)) shouldEqual true
  }

  test("override statements") {

    class T(tag: Tag) extends Table[Int](tag, "misc_t") {
      def id = column[Int]("a")
      def * = id
    }
    val ts = TableQuery[T]

    val q1 = ts.filter(_.id === 1)
    val q2 = ts.filter(_.id === 2)

    commit {
      DBIO.seq(
        ts.schema.drop.asTry,
        ts.schema.create,
        ts ++= Seq(1, 2, 3),
        q1.result.map(_ shouldBe Seq(1)),
        q1.result.overrideStatements(q2.result.statements).map(_ shouldBe Seq(2)),
        q1.result.head.map(_ shouldBe 1),
        q1.result.head.overrideStatements(q2.result.head.statements).map(_ shouldBe 2),
        ts.schema.drop
      )
    }
  }
}
