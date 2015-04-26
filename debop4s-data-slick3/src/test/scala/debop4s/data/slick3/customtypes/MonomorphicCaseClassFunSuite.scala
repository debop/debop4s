package debop4s.data.slick3.customtypes

import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

/**
 * CustomRecordType 처럼 Generic 이 아닌 case class 처럼 component 로 표현이 가능하다면 이 방식이 더 편하고 좋다.
 * @author sunghyouk.bae@gmail.com
 */
class MonomorphicCaseClassFunSuite extends AbstractSlickFunSuite {

  case class LiftedAddress(street: Rep[String], city: Rep[String], zipcode: Rep[String])
  case class Address(street: String, city: String, zipcode: String)

  implicit object AddressShape extends CaseClassShape(LiftedAddress.tupled, Address.tupled)

  case class UserAddress(name: String, address: Address, var id: Option[Int] = None)

  class UserAddresses(tag: Tag) extends Table[UserAddress](tag, "case_class_address") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Length(64))
    def street = column[String]("street", O.Length(255))
    def city = column[String]("city", O.Length(128))
    def zipcode = column[String]("zipcode", O.Length(32))

    def address = LiftedAddress(street, city, zipcode)
    def * = (name, address, id.?) <>(UserAddress.tupled, UserAddress.unapply)
  }
  lazy val userAddresses = TableQuery[UserAddresses]

  before {
    commit {
      userAddresses.schema.drop.asTry >>
      userAddresses.schema.create
    }

    commit {
      DBIO.seq(userAddresses += UserAddress("a", Address("aaaa", "seoul", "11111")),
               userAddresses += UserAddress("b", Address("bbbb", "seoul", "22222")),
               userAddresses += UserAddress("c", Address("cccc", "seoul", "33333"))
      )
    }
  }
  after {
    commit { userAddresses.schema.drop }
  }

  test("case class mapping") {
    readonly { userAddresses.result } foreach { ua => log.debug(ua.toString) }

    readonly {
      userAddresses.filter(_.id === 1.bind).take(1).result
    }.head.address.street shouldEqual "aaaa"

    readonly {
      userAddresses.filter(_.street === "bbbb".bind).map(_.zipcode).take(1).result
    }.head shouldEqual "22222"

    readonly {
      userAddresses.filter(_.address.city === "seoul".bind).map(_.address.zipcode).take(1).result
    }.head shouldEqual "11111"
  }
}
