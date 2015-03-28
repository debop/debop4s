package debop4s.data.slick.associations

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.associations.model.{ Address, Person, PersonTask, Task }
import debop4s.data.slick.associations.schema.AssociationDatabase._
import debop4s.data.slick.associations.schema.AssociationDatabase.driver.simple._

import scala.slick.jdbc.{ StaticQuery => Q }
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
      person <- persons
      addr <- person.address
    } yield {
      (addr.id, addr.street, addr.city, person.id, person.name, person.age)
    }

  override def beforeAll(): Unit = {
    super.beforeAll()

    val ddl = addresses.ddl ++ persons.ddl ++ tasks.ddl ++ personTasks.ddl

    withSession { implicit session => Try { ddl.drop } }

    withSession { implicit session =>
      ddl.create

      addresses ++= addressData
      persons ++= personData
      tasks ++= taskData
      personTasks ++= personTaskData
    }
  }

  test("many-to-one join test") {
    withReadOnly { implicit session =>
      LOG.debug(s"addresses:")
      addresses.list foreach println

      LOG.debug(s"persons:")
      persons.list foreach println

      LOG.debug("address and persons")
      addresses.list foreach { addr =>
        LOG.debug("\t" + addr)
        persons.findByAddress(addr.id.get) foreach { person => LOG.debug("\t\t" + person) }
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
          person <- persons
          task <- person.tasks
        } yield task

      allTasks.list foreach println
    }
  }


}
