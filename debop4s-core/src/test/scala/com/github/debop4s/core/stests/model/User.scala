package com.github.debop4s.core.stests.model

import com.github.debop4s.core.AbstractValueObject
import com.github.debop4s.core.utils.{ToStringHelper, Hashs}
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer

/**
 * User
 * Created by debop on 2014. 2. 22.
 */
class User extends AbstractValueObject with Ordered[User] {

    var firstName: String = _
    var lastName: String = _

    var age: Int = 0
    var updateTime: DateTime = DateTime.now

    var homeAddress = Address(null, null)
    var officeAddress = Address(null, null)

    val favoriteMovies = ArrayBuffer()

    override def compare(that: User): Int = {
        firstName.compareTo(that.firstName)
    }

    override def hashCode(): Int = Hashs.compute(firstName, lastName, age)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
        .add("firstName", firstName)
        .add("lastName", lastName)
}

case class Address(street: String, phone: String)
