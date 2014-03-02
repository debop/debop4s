package com.github.debop4s.data.tests.mapping

import javax.persistence.{AccessType, Access, Embeddable}


@Embeddable
@Access(AccessType.FIELD)
case class Address(street: String,
                   city: String,
                   state: String,
                   country: String,
                   zipcode: String) {
}
