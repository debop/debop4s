package debop4s.data.slick3.samples

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

/**
 * JdbcFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 28.
 */
class JdbcFunSuite extends AbstractSlickFunSuite {

  type Address = (Int, String, String)
  class Addresses(tag: Tag) extends Table[Address](tag, "address") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street", O.Length(255, true))
    def city = column[String]("city", O.Length(255, true))
    def * = (id, street, city)
  }
  lazy val addresses = TableQuery[Addresses]

  type Person = (Int, String, Int, Int)
  class People(tag: Tag) extends Table[Person](tag, "person") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Length(255, true))
    def age = column[Int]("age")
    def addressId = column[Int]("addressId")

    def * = (id, name, age, addressId)
    def address = foreignKey("fk_person_address", addressId, addresses)(_.id, onDelete = ForeignKeyAction.Cascade)
  }
  lazy val people = TableQuery[People]

  lazy val schema = addresses.schema ++ people.schema

  override def beforeAll(): Unit = {
    super.beforeAll()
    Seq(
      schema.drop.asTry,
      schema.create,
      addresses.map(x => (x.street, x.city)) +=("정릉", "서울"),
      people.map(x => (x.name, x.age, x.addressId)) +=("debop", 46, 1)
    ).exec
  }

  override def afterAll(): Unit = {
    schema.drop.exec
    super.afterAll()
  }

  test("plain sql query") {
    // val action = sql"select id, name, age, addressId from simple_jdbc_person".as[Person]
    val list = people.exec
    list.foreach { p => LOG.debug(p.toString()) }
  }

  test("with session") {
    val list = people.exec
    list.foreach { p => println(p) }
  }

}
