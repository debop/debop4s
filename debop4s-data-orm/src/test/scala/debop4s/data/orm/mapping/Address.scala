package debop4s.data.orm.mapping

import javax.persistence.{Access, AccessType, Embeddable}


@Embeddable
@Access(AccessType.FIELD)
case class Address(street: String = null,
                   city: String = null,
                   state: String = null,
                   country: String = null,
                   zipcode: String = null) {

  def this() = this(null, null, null, null, null)
}
