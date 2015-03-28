package debop4s.data.slick3.samples

import debop4s.core.concurrent.Asyncs
import debop4s.core.concurrent._
import debop4s.data.slick3.AbstractSlickFunSuite

import slick.driver.H2Driver.api._
import slick.jdbc.StaticQuery

import scala.util.Try

/**
 * JdbcFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 28.
 */
class JdbcFunSuite extends AbstractSlickFunSuite {

  type Person = (Int, String, Int, Int)
  class People(tag:Tag) extends Table[Person](tag, "simple_jdbc_person") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def age = column[Int]("age")
    def addressId = column[Int]("addressId")

    def * = (id, name, age, addressId)
    def address = foreignKey("fk_simple_jdbc_person_address", addressId, addresses)(_.id)
  }
  lazy val people = TableQuery[People]

  type Address = (Int, String, String)
  class Addresses(tag:Tag) extends Table[Address](tag, "simple_jdbc_address") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street")
    def city = column[String]("city")
    def * = (id, street, city)
  }
  lazy val addresses = TableQuery[Addresses]

  test("plain sql query") {

    LOG.info("create database ...")

    // val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1;", driver="org.h2.Driver", keepAliveConnection = true)
    val db = Database.forConfig("h2mem1")

    val schema = people.schema ++ addresses.schema
    Try { db.run(schema.drop).await }
    db.run(schema.create).await

    db.run(addresses.map(x=>(x.street, x.city)).forceInsert(("jr", "seoul"))).await
    db.run(people.map(x => (x.name, x.age, x.addressId)).forceInsert(("debop", 46, 1))).await

    // val action = sql"select ID, NAME, AGE from simple_jdbc_person".as[Person]
    val action: Query[People, (Int, String, Int, Int), Seq] = people
    val list = db.run(action.result).await

    db.close()
  }

}
