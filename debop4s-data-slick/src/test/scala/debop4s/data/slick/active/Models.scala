package debop4s.data.slick.active

import debop4s.data.slick.associations.model.{SlickEntity, Versionable}

case class Beer(var id: Option[Int] = None,
                name: String,
                supplierId: Int,
                price: Double) extends SlickEntity[Int]

case class Supplier(var id: Option[Int] = None,
                    var version: Long = 0,
                    name: String) extends SlickEntity[Int] with Versionable
