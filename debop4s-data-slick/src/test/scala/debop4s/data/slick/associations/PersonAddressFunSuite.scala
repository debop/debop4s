package debop4s.data.slick.associations

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.associations.model._
import debop4s.data.slick.associations.schema.AssociationDatabase._
import debop4s.data.slick.associations.schema.AssociationDatabase.driver.simple._

import scala.slick.jdbc.{StaticQuery => Q}
import scala.util.Try

/**
 * PersonAddressFunSuite
 * @author sunghyouk.bae@gmail.com at 15. 3. 23.
 */
class PersonAddressFunSuite extends AbstractSlickFunSuite {

  val addressData = Seq(Address(None, "율곡로", "서울"),
                         Address(None, "광화문로", "서울"),
                         Address(None, "테헤란로", "서울"),
                         Address(None, "광안대교", "부산"))

  val personData = Seq(Person(None, "배성혁", 45, 1),
                        Person(None, "이순신", 45, 2),
                        Person(None, "강감찬", 45, 3),
                        Person(None, "고종", 45, 2))

  val taskData = Seq(Task(None, "analysis"),
                      Task(None, "design"),
                      Task(None, "development"),
                      Task(None, "testing"))

  val personTaskData = Seq(PersonTask(1, 1), PersonTask(2, 3), PersonTask(3, 2), PersonTask(4, 4))

  lazy val addressPersons =
    for {
      person <- Persons
      addr <- person.address
    } yield {
      (addr.id, addr.street, addr.city, person.id, person.name, person.age)
    }

  override def beforeAll(): Unit = {
    super.beforeAll()

    val ddl = Addresses.ddl ++ Persons.ddl ++ Tasks.ddl ++ PersonTasks.ddl

    withSession { implicit session => Try {ddl.drop} }

    withSession { implicit session =>
      ddl.create

      Addresses ++= addressData
      Persons ++= personData
      Tasks ++= taskData
      PersonTasks ++= personTaskData
    }
  }

  test("many-to-one join test") {
    withReadOnly { implicit session =>
      println(s"Addresses:")
      Addresses.list foreach println

      println(s"Persons:")
      Persons.list foreach println

      println("address and persons")
      Addresses.list foreach { addr =>
        println("\t" + addr)
        Persons.findByAddress(addr.id.get) foreach { person => println("\t\t" + person) }
      }
    }
  }

  test("미리 정의된 Query 실행하기") {
    withReadOnly { implicit session =>
      val addrPersons = addressPersons.drop(1).take(2).list
      addrPersons foreach println
    }
  }

  test("people vs tasks") {
    withReadOnly { implicit session =>
      val allTasks =
        for {
          person <- Persons
          task <- person.tasks
        } yield task

      allTasks.list foreach println
    }
  }


}
