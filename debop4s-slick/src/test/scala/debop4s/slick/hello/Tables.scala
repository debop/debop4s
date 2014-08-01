package debop4s.slick.hello

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}


// A Suppliers table with 6 columns: id, name, street, city, state, zip
class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
  def id: Column[Int] = column[Int]("SUP_ID", O.PrimaryKey)
  def name: Column[String] = column[String]("SUP_NAME")
  def street: Column[String] = column[String]("STREET")
  def city: Column[String] = column[String]("CITY")
  def state: Column[String] = column[String]("STATE")
  def zip: Column[String] = column[String]("ZIP")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(Int, String, String, String, String, String)] = (id, name, street, city, state, zip)
}

// A Coffees table with 5 columns: name, supplier id, price, sales, total
class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
  def name: Column[String] = column[String]("COF_NAME", O.PrimaryKey)
  def supId: Column[Int] = column[Int]("SUP_ID")
  def price: Column[Double] = column[Double]("PRICE")
  def sales: Column[Int] = column[Int]("SALES")
  def total: Column[Int] = column[Int]("TOTAL")

  def * : ProvenShape[(String, Int, Double, Int, Int)] = (name, supId, price, sales, total)

  // A reified foreign key relation that can be navigated to create a join
  def supplier: ForeignKeyQuery[Suppliers, (Int, String, String, String, String, String)] =
    foreignKey("SUP_FK", supId, TableQuery[Suppliers])(_.id)
}
