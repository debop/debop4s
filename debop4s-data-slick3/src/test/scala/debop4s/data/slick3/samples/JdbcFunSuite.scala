package debop4s.data.slick3.samples

import debop4s.core.concurrent.Asyncs
import debop4s.core.concurrent._
import debop4s.core.utils.Closer
import debop4s.data.slick3.AbstractSlickFunSuite

import slick.driver.H2Driver.api._
import slick.jdbc.StaticQuery
import slick.profile.SqlStreamingAction

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/**
 * JdbcFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 28.
 */
class JdbcFunSuite extends AbstractSlickFunSuite {

  type Person = (Int, String, Int, Int)
  class People(tag: Tag) extends Table[Person](tag, "simple_jdbc_person") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def age = column[Int]("age")
    def addressId = column[Int]("addressId")

    def * = (id, name, age, addressId)
    def address = foreignKey("fk_simple_jdbc_person_address", addressId, addresses)(_.id)
  }
  lazy val people = TableQuery[People]

  type Address = (Int, String, String)
  class Addresses(tag: Tag) extends Table[Address](tag, "simple_jdbc_address") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street")
    def city = column[String]("city")
    def * = (id, street, city)
  }
  lazy val addresses = TableQuery[Addresses]

  lazy val schema = people.schema ++ addresses.schema

  override def beforeAll(): Unit = {
    super.beforeAll()

    db.run(DBIO.seq(
                     schema.create,
                     addresses.map(x => (x.street, x.city)).forceInsert("정릉", "서울"),
                     people.map(x => (x.name, x.age, x.addressId)).forceInsert(("debop", 46, 1))
                   )
          ).await
  }

  override def afterAll(): Unit = {
    db.run(schema.drop).await
    super.afterAll()
  }

  test("plain sql query") {
    // val action = sql"select id, name, age, addressId from simple_jdbc_person".as[Person]
    val action = people.result
    val list = db.run(action).await
    list.foreach { p => LOG.debug(p.toString()) }
  }

  test("with session") {
    val list = db.run(people.result).await
    list.foreach { p => println(p) }
  }

}
