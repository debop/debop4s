package debop4s.data.slick.examples

import debop4s.core.utils.{ClosableStopwatch, Closer}
import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * ParallelFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ParallelFunSuite extends AbstractSlickFunSuite {

  class Codes(tag: Tag) extends Table[(Int, String, String)](tag, "parallel_codes") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.NotNull)
    def value = column[String]("value", O.NotNull)

    def * = (id, name, value)
  }

  object Codes extends TableQuery(new Codes(_)) {
    val byName = this.findBy(_.name)
  }

  lazy val ranges = Range(0, 5000)

  override def beforeAll(): Unit = {
    super.beforeAll()
    withTransaction { implicit session =>
      Try { Codes.ddl.drop }
      Codes.ddl.create
    }
  }
  override def afterAll(): Unit = {
    withTransaction { implicit session =>
      Try { Codes.ddl.drop }
    }
    super.afterAll()
  }

  test("parallel insert with new session") {
    Closer.using(new ClosableStopwatch()) { sw =>
      ranges.par.foreach { i =>
        withTransaction { implicit session =>
          Codes.map(c => (c.name, c.value)).insert("name-" + i, "value-" + i)
        }
      }
    }
  }

}
