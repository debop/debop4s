package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

/**
 * UnionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class UnionFunSuite extends AbstractSlickFunSuite {

  class Managers(tag: Tag) extends Table[(Int, String, String)](tag, "managers") {
    def id = column[Int]("id")
    def name = column[String]("name")
    def department = column[String]("department")
    def * = (id, name, department)
  }
  lazy val managers = TableQuery[Managers]

  class Employees(tag: Tag) extends Table[(Int, String, Int)](tag, "employees") {
    def id = column[Int]("id")
    def name = column[String]("name2")
    def managerId = column[Int]("managerId")
    def * = (id, name, managerId)

    // A convience method for selecting employees by department
    def departmentIs(dept: String) = managerId in managers.filter(_.department === dept).map(_.id)
  }
  lazy val employees = TableQuery[Employees]

  test("basic") {

    val q1 = managers.filter(_.department === "IT").map(m => (m.id, m.name))
    val q2 = employees.filter(_.departmentIs("IT")).map(e => (e.id, e.name))
    val q3 = (q1 union q2).sortBy(_._2.asc) // name
    val q4 = managers.map(_.id)
    val q4b = q4 union q4
    val q4c = q4 union q4 union q4

    db.seq(
      (managers.schema ++ employees.schema).create,
      managers ++= Seq(
        (1, "Peter", "HR"),
        (2, "Amy", "IT"),
        (3, "Steve", "IT")
      ),
      employees ++= Seq(
        (4, "Jennifer", 1),
        (5, "Tom", 1),
        (6, "Leonard", 2),
        (7, "Ben", 2),
        (8, "Greg", 3)
      )
    )
    db.result(q1).toSet shouldEqual Set((2, "Amy"), (3, "Steve"))
    db.result(q2).toSet shouldEqual Set((7, "Ben"), (8, "Greg"), (6, "Leonard"))
    db.result(q3) shouldEqual Seq((2, "Amy"), (7, "Ben"), (8, "Greg"), (6, "Leonard"), (3, "Steve"))
    db.result(q4b).toSet shouldEqual Set(1, 2, 3)
    db.result(q4c).toSet shouldEqual Set(1, 2, 3)

    db.exec((managers.schema ++ employees.schema).drop)
  }

  test("union without projection") {
    def f(s: String) = managers filter { _.name === s }
    val q = f("Peter") union f("Amy")

    db.seq(
      managers.schema.create,
      managers ++= Seq(
        (1, "Peter", "HR"),
        (2, "Amy", "IT"),
        (3, "Steve", "IT")
      )
    )
    db.result(q).toSet shouldEqual Set((1, "Peter", "HR"), (2, "Amy", "IT"))
    db.exec(managers.schema.drop)
  }

  test("union of joins") {
    class Drinks(tag: Tag, tableName: String) extends Table[(Long, Long)](tag, tableName) {
      def pk = column[Long]("pk")
      def pkCup = column[Long]("pkCup")
      def * = (pk, pkCup)
    }

    lazy val coffees = TableQuery(new Drinks(_, "Coffee"))
    lazy val teas = TableQuery(new Drinks(_, "Tea"))

    val q1 = for {
      coffee <- coffees
      tea <- teas if tea.pkCup === coffee.pkCup
    } yield (coffee.pk, coffee.pkCup)

    val q2 = for {
      coffee <- coffees
      tea <- teas if tea.pkCup === coffee.pkCup
    } yield (tea.pk, tea.pkCup)

    val q3 = q1 union q2

    val schema = coffees.schema ++ teas.schema
    db.seq(
      schema.create,
      coffees ++= Seq((10L, 1L), (20L, 2L), (30L, 3L)),
      teas ++= Seq((100L, 1L), (200L, 2L), (300L, 3L))
    )

    db.result(q1).toSet shouldEqual Set((10L, 1L), (20L, 2L), (30L, 3L))
    db.result(q2).toSet shouldEqual Set((100L, 1L), (200L, 2L), (300L, 3L))
    db.result(q3).toSet shouldEqual Set((10L, 1L), (20L, 2L), (30L, 3L), (100L, 1L), (200L, 2L), (300L, 3L))

    db.exec { schema.drop }
  }
}
