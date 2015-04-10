package debop4s.data.slick3

import debop4s.core.concurrent._
import debop4s.data.slick3._
import debop4s.data.slick3.TestDatabase.driver.api._
import slick.backend.DatabasePublisher

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * TestDatabaseFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class TestDatabaseFunSuite extends AbstractSlickFunSuite {

  type Code = (Int, String, String)

  class Codes(tag: Tag) extends Table[Code](tag, "test_codes") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Length(128, true))
    def value = column[String]("value", O.Length(2000, true))

    def * = (id, name, value)
  }

  object Codes extends TableQuery(new Codes(_)) {
    lazy val byName = this.findBy(_.name)
  }

  lazy val ranges = Range(0, 5000)

  override def beforeAll(): Unit = {
    super.beforeAll()
    Codes.schema.create.run
  }
  override def afterAll(): Unit = {
    Codes.schema.drop.run
    super.beforeAll()
  }

  private def insertSampleData(): Unit = {
    ranges.grouped(100).toSeq.par.foreach { is =>
      (Codes.map(c => (c.name, c.value)) ++= is.map(i => (s"name=$i", s"value=$i")).toSet).run
    }
  }

  test("insert by grouped as parallel") {
    ranges.grouped(100).toSeq.par.foreach { is =>
      val samples = is.map(i => (s"name=$i", s"value=$i")).toSet
      Codes.map(c => (c.name, c.value)).forceInsertAll(samples).run
    }
  }

  test("read all data as parallel") {
    insertSampleData()

    ranges.grouped(100).toSeq.par.foreach { is =>
      val codes = Codes.filter(_.id inSet is.toSet).run.toSet
      codes.foreach { x => LOG.debug(x.toString()) }
    }
  }

  test("read all data by streaming") {
    insertSampleData()

    val iter: DatabasePublisher[Code] = Codes.result.stream
    iter.foreach { c => LOG.debug(c.toString()) }.await
  }
}
