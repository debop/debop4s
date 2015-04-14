package debop4s.data.slick3.associations

import debop4s.data.slick3.customtypes.EncryptedString
import debop4s.data.slick3.model.IntEntity
import org.joda.time.DateTime


case class Employee(name: String,
                    empNo: String,
                    password: EncryptedString,
                    hireDate: DateTime,
                    var id: Option[Int] = None) extends IntEntity

case class Site(name: String, var id: Option[Int] = None) extends IntEntity
case class Device(price: Double, acquisition: DateTime, siteId: Int, var id: Option[Int] = None) extends IntEntity

case class Order(no: String, date: DateTime, var id: Option[Int] = None) extends IntEntity
case class OrderItem(name: String, price: Double = 0.0, orderId: Int, var id: Option[Int] = None) extends IntEntity

case class Address(street: String, city: String, var id: Option[Int] = None) extends IntEntity
case class Person(name: String, age: Int, addressId: Int, var id: Option[Int] = None) extends IntEntity
case class Task(name: String, var id: Option[Int] = None) extends IntEntity
case class PersonTask(personId: Int, taskId: Int)


case class BankAccount(number: String, var id: Option[Int] = None) extends IntEntity
case class AccountOwner(ssn: String, var id: Option[Int] = None) extends IntEntity
case class BankAccountOwner(accountId: Int, ownerId: Int)