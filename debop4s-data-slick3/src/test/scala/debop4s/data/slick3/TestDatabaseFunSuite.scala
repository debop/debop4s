package debop4s.data.slick3

import debop4s.core.concurrent._
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import slick.backend.DatabasePublisher


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
    commit {
      Codes.schema.drop.asTry >>
      Codes.schema.create
    }
  }
  override def afterAll(): Unit = {
    commit { Codes.schema.drop }
    super.beforeAll()
  }

  private def insertSampleData(): Unit = {
    val actions = ranges.grouped(100).toSeq.par.map { is =>
      Codes.map(c => (c.name, c.value)) ++= is.map(i => (s"name=$i", s"value=$i")).toSet
    }.seq.toSeq
    commit { DBIO.seq(actions: _*) }
  }

  test("insert by grouped as parallel") {
    val actions = ranges.grouped(100).toSeq.par.map { is =>
      val samples = is.map(i => (s"name=$i", s"value=$i")).toSet
      Codes.map(c => (c.name, c.value)).forceInsertAll(samples)
    }.seq.toSeq
    commit { DBIO.seq(actions: _*) }
  }

  test("read all data as parallel") {
    insertSampleData()

    ranges.grouped(100).toSeq.par.foreach { is =>
      val codes = readonly { Codes.filter(_.id inSet is.toSet).to[Set].result }
      codes.foreach { x => log.debug(x.toString) }
    }
  }

  test("read all data by streaming") {
    insertSampleData()

    val iter: DatabasePublisher[Code] = db.stream(Codes.result)
    iter.foreach { c => log.debug(c.toString()) }.stay
  }
}
