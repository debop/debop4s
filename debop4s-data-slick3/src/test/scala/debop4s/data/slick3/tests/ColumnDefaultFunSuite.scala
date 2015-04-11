package debop4s.data.slick3.tests

import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.core.concurrent._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * ColumnDefaultFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ColumnDefaultFunSuite extends AbstractSlickFunSuite {

  class A(tag: Tag) extends Table[(Int, String, Option[Boolean])](tag, "a") {
    def id = column[Int]("id")
    def a = column[String]("a", O Default "foo", O.Length(255))
    def b = column[Option[Boolean]]("b", O Default Some(true))
    def * = (id, a, b)
  }
  lazy val as = TableQuery[A]

  test("column default") {
    db.exec {
      as.schema.drop.asTry >>
      as.schema.create >>
      (as.map(_.id) += 42)
    }
    as.exec shouldEqual Seq((42, "foo", Some(true)))

    as.schema.drop.exec
  }

}
