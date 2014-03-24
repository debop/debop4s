package com.github.debop4s.core.stests.model

import com.github.debop4s.core.AbstractValueObject
import com.github.debop4s.core.utils.{Arrays, ToStringHelper, Hashs}
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable


case class Address(street: String, phone: String, properties: mutable.Buffer[String] = ArrayBuffer[String]())

class User extends AbstractValueObject with Ordered[User] {

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

    val favoriteMovies = ArrayBuffer[String]()

    override def compare(that: User): Int = {
        firstName.compareTo(that.firstName)
    }

    override def hashCode(): Int = Hashs.compute(firstName, lastName, age)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
        .add("firstName", firstName)
        .add("lastName", lastName)
}


object User {

    def apply(favoriteMovieSize: Int): User = {
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

        (0 until favoriteMovieSize).foreach { x =>
            user.favoriteMovies += "Favorite Movie number-" + x
        }

        user
    }
}