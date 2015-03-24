package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * 컬럼 기본값 설정 테스트
 * @author sunghyouk.bae@gmail.com
 */
class ColumnDefaultFunSuite extends AbstractSlickFunSuite {

  class ColumnDefaults(tag: Tag) extends Table[(Int, String, Option[Boolean])](tag, "column_default") {
    def id = column[Int]("id")
    def a = column[String]("a", O.Default("foo"), O.Length(128, true))
    def b = column[Option[Boolean]]("b", O.Default(Some(true)))

    def * = (id, a, b)
  }
  lazy val columnDefaults = TableQuery[ColumnDefaults]

  test("default column") {
    withSession { implicit session =>
      Try {columnDefaults.ddl.drop}
      columnDefaults.ddl.create

      // insert into "column_default" ("id")  values (?)
      columnDefaults.map(_.id) += 42

      // select x2."id", x2."a", x2."b" from "column_default" x2
      columnDefaults.run shouldEqual List((42, "foo", Some(true)))
    }
  }

}
