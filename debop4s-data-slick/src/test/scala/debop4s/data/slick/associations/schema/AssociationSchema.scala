package debop4s.data.slick.associations.schema

import debop4s.data.slick.associations.model._
import debop4s.data.slick.customtypes.EncryptedString
import debop4s.data.slick.schema.SlickComponent
import org.joda.time.DateTime

import scala.slick.jdbc.Invoker

/**
 * SchemaSupport
 * @author sunghyouk.bae@gmail.com at 15. 3. 23.
 */
trait AssociationSchema {
  this: SlickComponent =>

  import driver.simple._

  class Employees(tag: Tag) extends IdTable[Employee, Int](tag, "ass_employees") {
    def id = column[Int]("emp_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("emp_name", O.NotNull, O.Length(64, varying = true))
    def empNo = column[String]("emp_no", O.NotNull, O.Length(24, varying = true))
    def password = column[EncryptedString]("emp_passwd", O.NotNull, O.Length(255, varying = true))
    def hireDate = column[DateTime]("hire_date")

    def * = (id.?, name, empNo, password, hireDate) <>(Employee.tupled, Employee.unapply)

    def ixName = index("ix_emp_name", (name, password), unique = true)
    def ixEmpNo = index("ix_emp_no", empNo, unique = true)
  }

  lazy val employees = TableQuery[Employees]
  implicit class EmployeeQueryExt(query: TableQuery[Employees]) extends IdTableExtensions[Employee, Int](query)

  class Sites(tag: Tag) extends IdTable[Site, Int](tag, "ass_site") {
    def id = column[Int]("site_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("site_name", O.NotNull, O.Length(255, varying = true))

    def * = (id.?, name) <>(Site.tupled, Site.unapply)
  }
  lazy val sites = TableQuery[Sites]
  implicit class SiteQueryExt(query: TableQuery[Sites]) extends IdTableExtensions[Site, Int](query)

  class Devices(tag: Tag) extends IdTable[Device, Int](tag, "ass_devices") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def price = column[Double]("device_price", O.NotNull)
    def acquisition = column[DateTime]("acquisition")
    def siteId = column[Int]("site_id", O.NotNull)

    def * = (id.?, price, acquisition, siteId) <>(Device.tupled, Device.unapply)

    def site = foreignKey("fk_device_site", siteId, TableQuery[Sites])(_.id)
    def ixSite = index("ix_device_site", siteId, unique = false)
  }
  lazy val devices = TableQuery[Devices]
  implicit class DeviceQueryExt(query: TableQuery[Devices]) extends IdTableExtensions[Device, Int](query) {
    def findBySite(site: Site)(implicit session: Session) = {
      query.filter(_.siteId === site.id.bind).list
    }
  }

  class Orders(tag: Tag) extends IdTable[Order, Int](tag, "ass_orders") {
    def id = column[Int]("order_id", O.PrimaryKey, O.AutoInc)
    def no = column[String]("order_no", O.NotNull, O.Length(255, varying = true))
    def date = column[DateTime]("order_date", O.NotNull)

    def * = (id.?, no, date) <>(Order.tupled, Order.unapply)

    def ixOrderNo = index("ix_order_no", no, unique = true)
    def ixOrderDate = index("ix_order_date", date, unique = false)

    def items = orderItems.filter(_.orderId === id)
  }

  lazy val orders = TableQuery[Orders]
  implicit class OrderQueryExt(query: TableQuery[Orders]) extends IdTableExtensions[Order, Int](query)

  class OrderItems(tag: Tag) extends IdTable[OrderItem, Int](tag, "ass_orderitem") {
    def id = column[Int]("orderitem_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("orderitem_name", O.Length(255, varying = true), O.NotNull)
    def price = column[Double]("orderitem_price", O.NotNull)
    def orderId = column[Int]("order_id", O.NotNull)

    def * = (id.?, name, price, orderId) <>(OrderItem.tupled, OrderItem.unapply)

    def order = foreignKey("fk_orderitem_order",
                            orderId,
                            TableQuery[Orders])(_.id,
                    onUpdate = ForeignKeyAction.NoAction,
                    onDelete = ForeignKeyAction.Cascade)
    def orderJoin = orders.filter(_.id === orderId)
    def ixOrder = index("ix_orderitem_order", orderId, unique = false)
  }

  lazy val orderItems = TableQuery[OrderItems]
  implicit class OrderItemQueryExt(query: TableQuery[OrderItems]) extends IdTableExtensions[OrderItem, Int](query)


  class Addresses(tag: Tag) extends IdTable[Address, Int](tag, "ass_address") {
    def id = column[Int]("address_id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street", O.NotNull, O.Length(1024, varying = true))
    def city = column[String]("city", O.NotNull, O.Length(255, varying = true))

    def * = (id.?, street, city) <>(Address.tupled, Address.unapply)
  }

  lazy val addresses = TableQuery[Addresses]
  implicit class AddressQueryExt(query: TableQuery[Addresses]) extends IdTableExtensions[Address, Int](query)

  class Persons(tag: Tag) extends IdTable[Person, Int](tag, "ass_person") {
    def id = column[Int]("person_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("person_name", O.NotNull, O.Length(255, varying = true))
    def age = column[Int]("person_age")
    def addressId = column[Int]("address_id", O.NotNull)

    def * = (id.?, name, age, addressId) <>(Person.tupled, Person.unapply)

    def address = foreignKey("fk_person_address", addressId, TableQuery[Addresses])(_.id)
    def ixAddress = index("ix_person_address", addressId, unique = false)

    // 관련된 Task 정보를 찾습니다. ( PersonTask 의 Mapping 정보로부터)
    def tasks =
      for {
        personTask <- personTasks
        task <- personTask.task if personTask.personId === id
      } yield task
  }

  lazy val persons = TableQuery[Persons]
  implicit class PersonsQueryExt(query: TableQuery[Persons]) extends IdTableExtensions[Person, Int](query) {
    def findByAddress(addressId: Int)(implicit session: Session) =
      query.filter(_.addressId === addressId.bind).list
  }

  class Tasks(tag: Tag) extends IdTable[Task, Int](tag, "ass_task") {
    def id = column[Int]("task_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("task_name", O.NotNull, O.Length(255, varying = true))

    def * = (id.?, name) <>(Task.tupled, Task.unapply)
  }
  lazy val tasks = TableQuery[Tasks]
  implicit class TaskQueryExt(query: TableQuery[Tasks]) extends IdTableExtensions[Task, Int](query)


  class PersonTasks(tag: Tag) extends Table[PersonTask](tag, "ass_person_task") {
    def personId = column[Int]("person_id", O.NotNull)
    def taskId = column[Int]("task_id", O.NotNull)

    def * = (personId, taskId) <>(PersonTask.tupled, PersonTask.unapply)

    def asignee = foreignKey("fk_person_task_person", personId, TableQuery[Persons])(_.id, onDelete=ForeignKeyAction.Cascade)
    def task = foreignKey("fk_person_task_task", taskId, TableQuery[Tasks])(_.id, onDelete=ForeignKeyAction.Cascade)

    def idxPersonTasks = index("ix_person_task", (personId, taskId), unique = true)
  }
  lazy val personTasks = TableQuery[PersonTasks]


  class BankAccounts(tag: Tag) extends IdTable[BankAccount, Int](tag, "ass_bankaccount") {
    def id = column[Int]("account_id", O.PrimaryKey, O.AutoInc)
    def number = column[String]("account_number", O.NotNull, O.Length(255, varying = true))

    def * = (id.?, number) <>(BankAccount.tupled, BankAccount.unapply)

    def owners = {
      for {
        ao <- bankAccountOwners
        o <- ao.owner if ao.accountId === id
      } yield o
    }
  }
  lazy val bankAccounts = TableQuery[BankAccounts]
  implicit class BankAccountQueryExt(query: TableQuery[BankAccounts]) extends IdTableExtensions[BankAccount, Int](query)

  class AccountOwners(tag: Tag) extends IdTable[AccountOwner, Int](tag, "ass_bankowner") {
    def id = column[Int]("owner_id", O.PrimaryKey, O.AutoInc)
    def ssn = column[String]("owner_ssn", O.NotNull, O.Length(255, varying = true))

    def * = (id.?, ssn) <>(AccountOwner.tupled, AccountOwner.unapply)

    def accounts: Query[BankAccounts, BankAccount, Seq] = {
      for {
        ao <- bankAccountOwners
        a <- ao.account if ao.ownerId === id
      } yield a
    }
  }
  lazy val accountOwners = TableQuery[AccountOwners]
  implicit class AccountOwnerQueryExt(query: TableQuery[AccountOwners]) extends IdTableExtensions[AccountOwner, Int](query)


  class BankAccountOwners(tag: Tag) extends Table[BankAccountOwner](tag, "ass_bankaccount_owner") {
    def accountId = column[Int]("account_id", O.NotNull)
    def ownerId = column[Int]("owner_id", O.NotNull)

    def * = (accountId, ownerId) <>(BankAccountOwner.tupled, BankAccountOwner.unapply)

    def account = foreignKey("fk_bankaccount_owner_account", accountId, TableQuery[BankAccounts])(_.id, onDelete=ForeignKeyAction.Cascade)
    def owner = foreignKey("fk_bankaccount_owner_owner", ownerId, TableQuery[AccountOwners])(_.id, onDelete=ForeignKeyAction.Cascade)

    def ixBankAccountOwners = index("ix_bankaccount_owner", (accountId, ownerId), unique = true)
  }
  lazy val bankAccountOwners = TableQuery[BankAccountOwners]

}
