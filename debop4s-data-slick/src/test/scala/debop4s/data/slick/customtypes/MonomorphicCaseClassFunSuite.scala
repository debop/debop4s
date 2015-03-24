package debop4s.data.slick.customtypes

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

case class LiftedAddress(street: Column[String], city: Column[String], zipcode: Column[String])
case class Address(street: String, city: String, zipcode: String)

class MonomorphicCaseClassFunSuite extends AbstractSlickFunSuite {

  implicit object AddressShape extends CaseClassShape(LiftedAddress.tupled, Address.tupled)

  case class UserAddress(var id: Option[Int] = None, name: String, address: Address)

  class UserAddressT(tag: Tag) extends Table[UserAddress](tag, "case_class_address_shape") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.NotNull, O.Length(64, true))
    def street = column[String]("street", O.Length(255, true))
    def city = column[String]("city", O.Length(128, true))
    def zipcode = column[String]("zipcode", O.Length(32, true))

    def address = LiftedAddress(street, city, zipcode)

    def * = (id.?, name, address) <>(UserAddress.tupled, UserAddress.unapply)
  }
  lazy val UserAddresses = TableQuery[UserAddressT]

  test("case class mapping") {
    withSession { implicit session =>
      Try {UserAddresses.ddl.drop}
      UserAddresses.ddl.create

      UserAddresses += UserAddress(None, "a", Address("aaaa", "seoul", "11111"))
      UserAddresses += UserAddress(None, "b", Address("bbbb", "seoul", "22222"))
      UserAddresses += UserAddress(None, "c", Address("cccc", "seoul", "33333"))

      UserAddresses.list foreach println

      UserAddresses.filter(_.id === 1.bind).first.address.street shouldEqual "aaaa"
      UserAddresses.filter(_.street === "bbbb".bind).map(_.zipcode).first shouldEqual "22222"
      UserAddresses.filter(_.address.city === "seoul".bind).map(_.address.zipcode).first shouldEqual "11111"
    }
  }
}
