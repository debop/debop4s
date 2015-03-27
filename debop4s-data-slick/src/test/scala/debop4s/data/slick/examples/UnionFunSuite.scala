package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * UnionFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 27.
 */
class UnionFunSuite extends AbstractSlickFunSuite {

  class Managers(tag: Tag) extends Table[(Int, String, String)](tag, "union_managers") {
    def id = column[Int]("manager_id")
    def name = column[String]("manager_name")
    def department = column[String]("department")
    def * = (id, name, department)
  }
  val managers = TableQuery[Managers]

  class Employees(tag: Tag) extends Table[(Int, String, Int)](tag, "union_employees") {
    def id = column[Int]("emp_id")
    def name = column[String]("emp_name")
    def managerId = column[Int]("manager_id")
    def * = (id, name, managerId)

    // 특정 부서에 해당하는 사용자 Id 조회
    def departmentIs(dept: String) = managerId in managers.filter(_.department === dept).map(_.id)
  }
  val employees = TableQuery[Employees]

  lazy val ddl = managers.ddl ++ employees.ddl

  before {
    withTransaction { implicit session =>
      Try { ddl.drop }
      ddl.create
    }
  }

  after {
    withTransaction { implicit session =>
      Try { ddl.drop }
    }
  }


  test("basic union") {
    withSession { implicit session =>
      managers ++= Seq(
                        (1, "Peter", "HR"),
                        (2, "Amy", "IT"),
                        (3, "Steve", "IT")
                      )
      employees ++= Seq(
                         (4, "Jennifer", 1),
                         (5, "Tom", 1),
                         (6, "Leonard", 2),
                         (7, "Ben", 2),
                         (8, "Greg", 3)
                       )

      val q1 = managers.filter(_.department === "IT").map(m => (m.id, m.name))
      LOG.debug("Managers in IT")
      q1.run foreach { m => LOG.debug(s"  $m") }
      q1.run.toSet shouldEqual Set((2, "Amy"), (3, "Steve"))

      val q2 = employees.filter(_.departmentIs("IT")).map(e => (e.id, e.name))
      LOG.debug("Employee in IT")
      q2.run foreach { e => LOG.debug(s"  $e") }
      q2.run.toSet shouldEqual Set((6, "Leonard"), (7, "Ben"), (8, "Greg"))

      val q3 = ( q1 union q2 ).sortBy(_._2.asc) // sort by name
      LOG.debug("Combined and sorted")
      q3.run foreach { o => LOG.debug(s"  $o") }
      q3.run shouldEqual Seq((2, "Amy"), (7, "Ben"), (8, "Greg"), (6, "Leonard"), (3, "Steve"))

      val q3c = Compiled(q3)
      LOG.debug("Combined and sorted with Compiled")
      q3c.run foreach { o => LOG.debug(s"  $o") }
      q3c.run shouldEqual Seq((2, "Amy"), (7, "Ben"), (8, "Greg"), (6, "Leonard"), (3, "Steve"))
    }
  }

  test("union without projection") {
    withSession { implicit session =>
      managers ++= Seq(
                        (1, "Peter", "HR"),
                        (2, "Amy", "IT"),
                        (3, "Steve", "IT")
                      )
      def f(s: String) = managers filter { _.name === s.bind }
      val q = f("Steve") union f("Amy")
      q.run.toSet shouldEqual Set((2, "Amy", "IT"), (3, "Steve", "IT"))
    }
  }

  test("union of joins") {

    class Drinks(tag: Tag, tablename: String) extends Table[(Long, Long)](tag, tablename) {
      def pk = column[Long]("pk")
      def pkCup = column[Long]("pkCup")
      def * = (pk, pkCup)
    }
    val coffees = TableQuery(new Drinks(_, "union_coffee"))
    val tees = TableQuery(new Drinks(_, "union_tee"))

    withSession { implicit session =>
      val ddl = coffees.ddl ++ tees.ddl
      Try { ddl.drop }
      ddl.create

      coffees ++= Seq(
                       (10L, 1L),
                       (20L, 2L),
                       (30L, 3L)
                     )
      tees ++= Seq(
                    (100L, 1L),
                    (200L, 2L),
                    (300L, 3L)
                  )

      val q1 = for {
        c <- coffees
        t <- tees if c.pkCup === t.pkCup
      } yield c

      val q2 = for {
        c <- coffees
        t <- tees if c.pkCup === t.pkCup
      } yield t

      val q3 = q1 union q2

      q1.run.toSet shouldEqual Set((10L, 1L), (20L, 2L), (30L, 3L))
      q2.run.toSet shouldEqual Set((100L, 1L), (200L, 2L), (300L, 3L))

      q3.run.toSet shouldEqual Set((10L, 1L), (20L, 2L), (30L, 3L), (100L, 1L), (200L, 2L), (300L, 3L))

      ddl.drop
    }
  }
}
