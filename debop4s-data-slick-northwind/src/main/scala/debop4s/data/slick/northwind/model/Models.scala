package debop4s.data.slick.northwind.model

import java.sql.Blob

import debop4s.data.slick.model.{IntEntity, StringEntity}
import org.joda.time.DateTime


case class AddressComponent(address: Option[String] = None,
                            city: Option[String] = None,
                            region: Option[String] = None,
                            postalCode: Option[String] = None,
                            country: Option[String] = None)

case class Category(name: String,
                    description: Option[String] = None,
                    picture: Option[Blob] = None,
                    var id: Option[Int]) extends IntEntity

case class CustomerCustomerDemo(id: String, typeId: String)

case class CustomerDemographic(typeId: String, desc: Option[String] = None)

case class Customer(companyName: String,
                    contactName: Option[String] = None,
                    contactTitle: Option[String] = None,
                    address: Option[AddressComponent] = None,
                    phone: Option[String] = None,
                    fax: Option[String] = None,
                    var id: Option[String]) extends StringEntity

case class Employee(lastname: String,
                    firstname: String,
                    title: Option[String] = None,
                    titleOfCoutesy: Option[String] = None,
                    birthDate: Option[DateTime] = None,
                    hireDate: Option[DateTime] = None,
                    address: Option[AddressComponent] = None,
                    homePhone: Option[String] = None,
                    extension: Option[String] = None,
                    photo: Option[Blob] = None,
                    notes: String,
                    reportsTo: Option[Int] = None,
                    photoPath: Option[String] = None,
                    salary: Option[Float] = None,
                    var id: Option[Int]) extends IntEntity {

  lazy val Salesperson = firstname + " " + lastname
}


case class OrderDetail(orderId: Int,
                       productId: Int,
                       unitPrice: BigDecimal = 0.0000,
                       quantity: Short = 1,
                       discount: Double = 0.0) {
  lazy val extendedPrice: Double =
    (((unitPrice.toDouble * quantity.toDouble) * (1.0 - discount)) / 100.0) * 100.0
}

case class Order(customerId: Option[String] = None,
                 employeeId: Option[Int] = None,
                 orderDate: Option[DateTime] = None,
                 requiredDate: Option[DateTime] = None,
                 shippedDate: Option[DateTime] = None,
                 shipVia: Option[Int] = None,
                 freight: Option[BigDecimal] = None,
                 shipName: Option[String] = None,
                 shipAddress: Option[AddressComponent] = None,
                 var id: Option[Int]) extends IntEntity

case class Product(name: String,
                   supplierId: Option[Int] = None,
                   categoryId: Option[Int] = None,
                   quantityPerUnit: Option[String] = None,
                   unitPrice: Option[BigDecimal] = None,
                   unitsInStock: Option[Short] = None,
                   unitsOnOrder: Option[Short] = None,
                   reorderLevel: Option[Short] = None,
                   discontinued: Boolean = false,
                   var id: Option[Int]) extends IntEntity

case class Region(id: Int, description: String)

case class Shipper(companyName: String,
                   phone: Option[String] = None,
                   var id: Option[Int]) extends IntEntity

case class Supplier(companyName: String,
                    contactName: Option[String] = None,
                    contactTitle: Option[String] = None,
                    address: Option[AddressComponent] = None,
                    phone: Option[String] = None,
                    fax: Option[String] = None,
                    homepage: Option[String] = None,
                    var id: Option[Int]) extends IntEntity

case class Territory(description: String,
                     regionId: Int,
                     var id: Option[String]) extends StringEntity