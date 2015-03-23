package debop4s.data.slick.associations.model

import debop4s.data.slick.customtypes.EncryptedString
import debop4s.data.slick.model.IntEntity
import org.joda.time.DateTime

case class Employee(var id: Option[Int] = None,
                    name: String,
                    empNo: String,
                    password: EncryptedString,
                    hireDate: DateTime) extends IntEntity

case class Site(var id: Option[Int] = None, name: String) extends IntEntity
case class Device(var id: Option[Int] = None, price: Double, acquisition: DateTime, siteId: Int) extends IntEntity

case class Order(var id: Option[Int] = None, no: String, date: DateTime) extends IntEntity
case class OrderItem(var id: Option[Int] = None, name: String, price: Double = 0.0, orderId: Int) extends IntEntity

case class Address(var id: Option[Int] = None, street: String, city: String) extends IntEntity
case class Person(var id: Option[Int] = None, name: String, age: Int, addressId: Int) extends IntEntity
case class Task(var id: Option[Int] = None, name: String) extends IntEntity
case class PersonTask(personId: Int, taskId: Int)


case class BankAccount(var id: Option[Int] = None, number: String) extends IntEntity
case class AccountOwner(var id: Option[Int] = None, ssn: String) extends IntEntity
case class BankAccountOwner(accountId: Int, ownerId: Int)