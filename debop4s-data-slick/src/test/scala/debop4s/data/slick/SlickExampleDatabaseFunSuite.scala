package debop4s.data.slick

// NOTE : SlickExampleDatabase._ 와 SlickExampleDatabase.driver.simple._ 을 꼭 import 해줘야 합니다.

import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * SlickComponentFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
class SlickExampleDatabaseFunSuite extends AbstractSlickFunSuite {

  class CodeT(tag: Tag) extends Table[(Int, String, String)](tag, "implicits_codes") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.NotNull, O.Length(254, varying = true))
    def value = column[String]("value", O.NotNull, O.Length(254, varying = true))

    def * = (id, name, value)
  }
  // 아래의 Codes 변수와 같은 기능을 수행한다.
  object CodeRepository extends TableQuery(new CodeT(_)) {
    lazy val byName = this.findBy(_.name)
  }
  lazy val Codes = TableQuery[CodeT]


  lazy val ranges = Range(0, 5000)

  override def beforeAll(): Unit = {
    super.beforeAll()

    withTransaction { implicit session =>
      Try { CodeRepository.ddl.drop }
    }
    withTransaction { implicit session =>
      CodeRepository.ddl.create
    }
  }

  override def afterAll(): Unit = {
    withTransaction { implicit session =>
      Try { CodeRepository.ddl.drop }
    }
    super.afterAll()
  }

  private def insertSamples(): Unit = {
    // 100개 단위로 나눠 Insert를 수행합니다.
    ranges.grouped(100).toSeq.par.foreach { is =>
      withTransaction { implicit session =>
        CodeRepository.map(c => (c.name, c.value)) ++= is.map(i => (s"name-$i", s"value-$i")).toSet
      }
    }
  }

  test("with transaction") {
    // 100개 단위로 나눠 Insert를 수행합니다.
    ranges.grouped(100).toSeq.par.foreach { is =>
      withTransaction { implicit session =>
        CodeRepository.map(c => (c.name, c.value)) ++= is.map(i => (s"name-$i", s"value-$i")).toSet
      }
    }
  }

  test("with dynamic transaction") {
    // 100개 단위로 나눠 Insert를 수행합니다.
    ranges.grouped(100).toSeq.par.foreach { is =>
      withDynTransaction { implicit session =>
        CodeRepository.map(c => (c.name, c.value)) ++= is.map(i => (s"name-$i", s"value-$i")).toSet
      }
    }
  }

  test("with rollback") {
    // 100개 단위로 나눠 Insert를 수행합니다.
    ranges.grouped(100).toSeq.par.foreach { is =>
      withRollback { implicit session =>
        CodeRepository.map(c => (c.name, c.value)) ++= is.map(i => (s"name-$i", s"value-$i")).toSet
      }
    }
  }

  test("with dynamic rollback") {
    // 100개 단위로 나눠 Insert를 수행합니다.
    ranges.grouped(100).toSeq.par.foreach { is =>
      withDynRollback { implicit session =>
        CodeRepository.map(c => (c.name, c.value)) ++= is.map(i => (s"name-$i", s"value-$i")).toSet
      }
    }
  }


  test("with readonly") {
    insertSamples()

    ranges.grouped(100).toSeq.par.foreach { is =>
      withReadOnly { implicit session =>
        val codes = CodeRepository.filter(_.id inSet is.toSet).run.toSet
        codes.foreach(x => LOG.debug(x.toString))
      }
    }
  }

  test("with dynamic readonly") {
    insertSamples()

    ranges.grouped(100).toSeq.par.foreach { is =>
      withDynReadOnly { implicit session =>
        val codes = CodeRepository.filter(_.id inSet is.toSet).run.toSet
        codes.foreach(x => LOG.debug(x.toString))
      }
    }
  }


}
