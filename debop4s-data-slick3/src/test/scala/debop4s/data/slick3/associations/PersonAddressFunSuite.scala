package debop4s.data.slick3.associations

import debop4s.data.slick3._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._

/**
 * PersonAddressFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PersonAddressFunSuite extends AbstractSlickFunSuite {

  val addressData = Seq(
    Address(None, "율곡로", "서울"),
    Address(None, "광화문로", "서울"),
    Address(None, "테헤란로", "서울"),
    Address(None, "광안대교", "부산")
  )

  val personData = Seq(
    Person(None, "배성혁", 45, 1),
    Person(None, "이순신", 45, 2),
    Person(None, "강감찬", 45, 3),
    Person(None, "고종", 45, 2)
  )

  val taskData = Seq(
    Task(None, "analysis"),
    Task(None, "design"),
    Task(None, "development"),
    Task(None, "testing")
  )

  val personTaskData = Seq(PersonTask(1, 1), PersonTask(2, 3), PersonTask(3, 2), PersonTask(4, 4))

  lazy val addressPersons = for {
    person <- persons
    addr <- person.address
  } yield (addr.id, addr.street, addr.city, person.id, person.name, person.age)

  lazy val schema = persons.schema ++ tasks.schema ++ personTasks.schema ++ addresses.schema

  override def beforeAll(): Unit = {
    super.beforeAll()
    Seq(
      schema.drop.asTry,
      schema.create,
      addresses ++= addressData,
      persons ++= personData,
      tasks ++= taskData,
      personTasks ++= personTaskData
    ).run
  }

  override def afterAll(): Unit = {
    schema.drop.run
    super.afterAll()
  }

  test("many-to-one 테스트") {
    addresses.run foreach println
    persons.run foreach println

    addressPersons.run foreach println

    addresses.run foreach { addr =>
      println("\t" + addr)
      persons.findByAddress(addr.id.get).run foreach { person => println("\t\t" + person) }
    }
  }

  test("many-to-many : people vs tasks") {
    val allTasks = for {
      person <- persons
      task <- person.tasks
    } yield task

    allTasks.run foreach println
    allTasks.sortBy(_.id).run shouldEqual Seq(
      Task(Some(1), "analysis"),
      Task(Some(2), "design"),
      Task(Some(3), "development"),
      Task(Some(4), "testing")
    )
  }

}
