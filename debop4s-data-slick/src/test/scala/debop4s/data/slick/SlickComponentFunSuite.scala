package debop4s.data.slick

import debop4s.data.slick.SlickComponentExample._

import scala.util.Try

/**
 * SlickComponentFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
class SlickComponentFunSuite extends AbstractSlickFunSuite {

  import driver.simple._

  class Codes(tag: Tag) extends Table[(Int, String, String)](tag, "implicits_codes") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.NotNull, O.Length(254, varying = true))
    def value = column[String]("value", O.NotNull, O.Length(254, varying = true))

    def * = (id, name, value)
  }

  object CodeRepository extends TableQuery(new Codes(_)) {
    lazy val byName = this.findBy(_.name)
  }

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
        codes.foreach(println)
      }
    }
  }

  test("with dynamic readonly") {
    insertSamples()

    ranges.grouped(100).toSeq.par.foreach { is =>
      withDynReadOnly { implicit session =>
        val codes = CodeRepository.filter(_.id inSet is.toSet).run.toSet
        codes.foreach(println)
      }
    }
  }


}
