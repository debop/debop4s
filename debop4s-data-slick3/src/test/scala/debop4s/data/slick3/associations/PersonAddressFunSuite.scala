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
    Address("율곡로", "서울"),
    Address("광화문로", "서울"),
    Address("테헤란로", "서울"),
    Address("광안대교", "부산")
  )

  val personData = Seq(
    Person("배성혁", 45, 1),
    Person("이순신", 45, 2),
    Person("강감찬", 45, 3),
    Person("고종", 45, 2)
  )

  val taskData = Seq(
    Task("analysis"),
    Task("design"),
    Task("development"),
    Task("testing")
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
    ).exec
  }

  override def afterAll(): Unit = {
    schema.drop.exec
    super.afterAll()
  }

  test("many-to-one 테스트") {
    addresses.exec foreach println
    persons.exec foreach println

    addressPersons.exec foreach println

    addresses.exec foreach { addr =>
      println("\t" + addr)
      persons.findByAddress(addr.id.get).exec foreach { person => println("\t\t" + person) }
    }
  }

  test("many-to-many : people vs tasks") {
    val allTasks = for {
      person <- persons
      task <- person.tasks
    } yield task

    allTasks.exec foreach println
    allTasks.sortBy(_.id).exec shouldEqual Seq(
      Task("analysis", Some(1)),
      Task("design", Some(2)),
      Task("development", Some(3)),
      Task("testing", Some(4))
    )
  }

}
