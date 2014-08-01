package debop4s.slick.hello

import debop4s.slick.AbstractSlickFunSuite

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta._
import scala.slick.lifted.TableQuery

/**
 * TablesFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class TablesFunSuite extends AbstractSlickFunSuite {

  val suppliers = TableQuery[Suppliers]
  val coffees = TableQuery[Coffees]

  implicit var session: Session = _

  def createSchema() = (suppliers.ddl ++ coffees.ddl).create

  def insertSupplier(): Int = suppliers +=(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")

  before {
    session = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver").createSession()
    createSchema()
  }
  after {
    session.close()
  }

  test("Create the Schema works") {
    val tables = MTable.getTables.list

    tables.size shouldEqual 2
    tables.count(_.name.name.equalsIgnoreCase("suppliers")) shouldEqual 1
    tables.count(_.name.name.equalsIgnoreCase("coffees")) shouldEqual 1
  }

  test("Inserting a Supplier works") {
    val insertCount = insertSupplier()
    insertCount shouldEqual 1
  }

  test("Query Suppliers works") {
    insertSupplier()
    val results = suppliers.list
    results.size shouldEqual 1
    results.head._1 shouldEqual 101
  }


}
