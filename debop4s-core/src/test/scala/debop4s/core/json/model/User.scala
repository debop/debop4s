package debop4s.core.json.model

import com.google.common.collect.Lists
import debop4s.core.utils.{Arrays, Hashs}
import debop4s.core.{ToStringHelper, ValueObject}
import org.joda.time.DateTime

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class Address(street: String, phone: String, properties: mutable.Buffer[String] = ArrayBuffer[String]())

class User extends ValueObject with Ordered[User] {

  var firstName: String = _
  var lastName: String = _
  var addressStr: String = _
  var city: String = _
  var state: String = _
  var zipcode: String = _
  var email: String = _
  var username: String = _
  var password: String = _

  var age: Int = 0
  var updateTime: DateTime = DateTime.now

  var byteArray: Array[Byte] = Arrays.getRandomBytes(1024)

  var homeAddress = Address(null, null)
  var officeAddress = Address(null, null)

  val favoriteMovies = Lists.newArrayList[String]() // ArrayBuffer[String]()

  override def compare(that: User): Int = {
    firstName.compareTo(that.firstName)
  }

  override def hashCode: Int = Hashs.compute(firstName, lastName, age)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("firstName", firstName)
    .add("lastName", lastName)
    .add("addressStr", addressStr)
}


object User {

  def apply(favoriteMovieSize: Int = 0): User = {
    val user = new User()
    user.firstName = "성혁"
    user.lastName = "배"
    user.addressStr = "정릉1동 현대홈타운 107동 301호"
    user.city = "서울"
    user.state = "서울"
    user.email = "sunghyouk.bae@gmail.com"
    user.username = "debop"
    // user.password = "1234"
    user.homeAddress = new Address("정릉1동 현대홈타운 107동 301호", "555-5555")
    user.homeAddress.properties ++= ArrayBuffer("home", "addr")

    user.officeAddress = new Address("운니동 삼환빌딩 10F", "555-5555")
    user.officeAddress.properties ++= ArrayBuffer("office", "addr")

    var x = 0
    while (x < favoriteMovieSize) {
      user.favoriteMovies add "Favorite Movie number-" + x
      x += 1
    }
    user
  }
}