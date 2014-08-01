package debop4s.slick.hello

import debop4s.slick.AbstractSlickFunSuite

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted

class HelloSlickFunSuite extends AbstractSlickFunSuite {

  val suppliers: lifted.TableQuery[Suppliers] = TableQuery[Suppliers]
  val coffees: lifted.TableQuery[Coffees] = TableQuery[Coffees]

  implicit var session: Session = _

  before {
    session = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver").createSession()

    // create the schema by combining the DDLs for the Suppliers and Coffees tables using the query interfaces
    (suppliers.ddl ++ coffees.ddl).create

    suppliers +=(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
    suppliers +=(49, "Suerior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
    suppliers +=(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")

    // insert some coffees (using JDBC's batch insert feature)
    val coffesInsertResult: Option[Int] =
      coffees ++= Seq(
                       ("Colombia", 101, 7.99, 0, 0),
                       ("French_Roast", 49, 8.99, 0, 0),
                       ("Espresso", 150, 9.99, 0, 0),
                       ("Colimbian_Decaf", 101, 8.99, 0, 0),
                       ("French_Roast_Decaf", 49, 9.99, 0, 0)
                     )

    coffesInsertResult foreach { numRows =>
      println(s"Inserted $numRows rows into the Coffees table")
    }
  }

  after {
    session.close()
  }

  test("read all") {

    val allSuppliers = suppliers.list
    println("Generated SQL for base Coffees query:\n" + coffees.selectStatement)
    coffees foreach {
      case (name, supId, price, sales, total) =>
        println(s"  $name\t$supId\t$price\t$sales\t$total")
    }
  }

  test("filtering / where") {
    val filterQuery = coffees.filter(_.price > 9.0)
    println("Generated SQL for filter query:\n" + filterQuery.selectStatement)
    println(filterQuery.list)
  }

  test("update") {
    // Construct an update query with the sales column being the one to update
    val updateQuery: Query[Column[Int], Int, Seq] = coffees.map(_.sales)
    println("Generated SQL for Coffees update:\n" + updateQuery.updateStatement)

    // perform udpate
    val updatedRows = updateQuery.update(1)
    println(s"Updated $updatedRows rows")
  }

  test("delete") {
    val deleteQuery = coffees.filter(_.price < 8.0)
    println("Generated SQL for Coffees delete:\n" + deleteQuery.deleteStatement)
    val deletedRows = deleteQuery.delete
    println(s"Deleted $deletedRows rows")
  }

  test("selecting specific columns") {
    val justNameQuery = coffees.map(_.name)
    println("Generated SQL for query returning just the name:\n" + justNameQuery.selectStatement)
    println(justNameQuery.list)
  }

  test("sort / order by") {
    val sortByPriceQuery = coffees.sortBy(_.price)
    println("Generated SQL for query sorted by price:\n" + sortByPriceQuery.selectStatement)
    println(sortByPriceQuery.list)
  }

  test("query composition") {
    val composedQuery = coffees.sortBy(_.name).take(3).filter(_.price > 9.0).map(_.name)
    println("Generated SQL for composed query:\n" + composedQuery.selectStatement)
    println(composedQuery.list)
  }

  test("join") {
    val joinQuery = for {
      c <- coffees if c.price > 9.0
      s <- c.supplier
    } yield (c.name, s.name)

    println("Generated SQL for the join query:\n" + joinQuery.selectStatement)
    println(joinQuery.list)
  }

  test("computed values") {
    val maxPriceColumn = coffees.map(_.price).max
    println("Generated SQL for max price column:\n" + maxPriceColumn.selectStatement)
    println(maxPriceColumn.run)
  }

  test("natual SQL / String interpolation") {
    import scala.slick.jdbc.StaticQuery.interpolation

    val state = "CA"
    val plainQuery = sql"select SUP_NAME from SUPPLIERS where STATE=$state".as[String]
    println("Generated SQL for plain query:\n" + plainQuery.getStatement)
    println(plainQuery.list)
  }

}
