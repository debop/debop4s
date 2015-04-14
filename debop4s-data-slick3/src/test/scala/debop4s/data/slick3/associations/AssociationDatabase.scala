package debop4s.data.slick3.associations

import debop4s.data.slick3.customtypes.EncryptedString
import debop4s.data.slick3.schema.SlickComponent
import org.joda.time.DateTime

/**
 * Association 관련 예제 Database
 * @author sunghyouk.bae@gmail.com
 */
object AssociationDatabase extends SlickComponent with AssociationSchema {}

/**
 * Association DB Schema
 */
trait AssociationSchema {
  this: SlickComponent =>

  import driver.api._

  class Employees(tag: Tag) extends IdTable[Employee, Int](tag, "ass_employee") {
    def id = column[Int]("emp_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("emp_name", O.Length(64))
    def empNo = column[String]("emp_no", O.Length(24))
    def password = column[EncryptedString]("emp_passwd", O.Length(254))
    def hireDate = column[DateTime]("hire_date")

    def * = (name, empNo, password, hireDate, id.?) <>(Employee.tupled, Employee.unapply)

    def ixName = index("ix_emp_name", (name, password), unique = true)
    def ixEmpNo = index("ix_emp_no", empNo, unique = true)
  }
  lazy val employees = TableQuery[Employees]
  implicit class EmployeeQueryExt(query: TableQuery[Employees]) extends IdTableExtensions[Employee, Int](query)

  class Sites(tag: Tag) extends IdTable[Site, Int](tag, "ass_site") {
    def id = column[Int]("site_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("site_name", O.Length(255))
    def * = (name, id.?) <>(Site.tupled, Site.unapply)
  }
  lazy val sites = TableQuery[Sites]
  implicit class SiteQueryExt(query: TableQuery[Sites]) extends IdTableExtensions[Site, Int](query)

  class Devices(tag: Tag) extends IdTable[Device, Int](tag, "ass_device") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def price = column[Double]("device_price")
    def acquisition = column[DateTime]("acquisition")
    def siteId = column[Int]("site_id")

    def * = (price, acquisition, siteId, id.?) <>(Device.tupled, Device.unapply)

    def site = foreignKey("fk_device_site", siteId, TableQuery[Sites])(_.id)
    def ixSite = index("ix_device_site", siteId, unique = false)
  }
  lazy val devices = TableQuery[Devices]
  implicit class DeviceQueryExt(query: TableQuery[Devices]) extends IdTableExtensions[Device, Int](query) {
    def findBySite(site: Site) = {
      query.filter(_.siteId === site.id.bind)
    }
  }

  class Orders(tag: Tag) extends IdTable[Order, Int](tag, "ass_order") {
    def id = column[Int]("order_id", O.PrimaryKey, O.AutoInc)
    def no = column[String]("order_no", O.Length(64))
    def date = column[DateTime]("order_date")

    def * = (no, date, id.?) <>(Order.tupled, Order.unapply)

    def ixOrderNo = index("ix_order_no", no, unique = true)
    def ixOrderDate = index("ix_order_date", date, unique = false)

    def itemJoin = orderItems.filter(_.orderId === id)
  }
  lazy val orders = TableQuery[Orders]
  implicit class OrderQueryExt(query: TableQuery[Orders]) extends IdTableExtensions[Order, Int](query)

  class OrderItems(tag: Tag) extends IdTable[OrderItem, Int](tag, "ass_orderitem") {
    def id = column[Int]("item_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("item_name", O.Length(254))
    def price = column[Double]("item_price")
    def orderId = column[Int]("order_id")

    def * = (name, price, orderId, id.?) <>(OrderItem.tupled, OrderItem.unapply)

    def order = foreignKey("fk_orderitem_order", orderId, TableQuery[Orders])(_.id, onDelete = ForeignKeyAction.Cascade)
    def orderJoin = orders.filter(_.id === orderId)
    def ixOrder = index("ix_orderitem_order", orderId, unique = false)
  }
  lazy val orderItems = TableQuery[OrderItems]
  implicit class OrderItemExt(query: TableQuery[OrderItems]) extends IdTableExtensions[OrderItem, Int](query)

  class Addresses(tag: Tag) extends IdTable[Address, Int](tag, "ass_address") {
    def id = column[Int]("addr_id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street", O.Length(1024))
    def city = column[String]("city", O.Length(255))

    def * = (street, city, id.?) <>(Address.tupled, Address.unapply)
  }
  lazy val addresses = TableQuery[Addresses]
  implicit class AddressQueryExt(query: TableQuery[Addresses]) extends IdTableExtensions[Address, Int](query)


  class Persons(tag: Tag) extends IdTable[Person, Int](tag, "ass_person") {
    def id = column[Int]("person_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("person_name", O.Length(254))
    def age = column[Int]("person_age")
    def addressId = column[Int]("addr_id")

    def * = (name, age, addressId, id.?) <>(Person.tupled, Person.unapply)

    def address = foreignKey("fk_person_addr", addressId, TableQuery[Addresses])(_.id)
    def ixAddr = index("ix_person_addr", addressId, unique = false)

    // PersonTask 의 Mapping 정보로부터 Task 정보를 찾습니다. ( many-to-many )
    def tasks = for {
      pt <- personTasks
      t <- pt.task if pt.personId === id
    } yield t
  }
  lazy val persons = TableQuery[Persons]
  implicit class PersonQueryExt(query: TableQuery[Persons]) extends IdTableExtensions[Person, Int](query) {
    def findByAddress(addrId: Int) =
      query.filter(_.addressId === addrId.bind)
  }

  class Tasks(tag: Tag) extends IdTable[Task, Int](tag, "ass_task") {
    def id = column[Int]("task_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("task_name", O.Length(254))

    def * = (name, id.?) <>(Task.tupled, Task.unapply)
  }
  lazy val tasks = TableQuery[Tasks]
  implicit class TaskQueryExt(query: TableQuery[Tasks]) extends IdTableExtensions[Task, Int](query)

  class PersonTasks(tag: Tag) extends Table[PersonTask](tag, "ass_person_task") {
    def personId = column[Int]("person_id")
    def taskId = column[Int]("taskId")

    def * = (personId, taskId) <>(PersonTask.tupled, PersonTask.unapply)

    def assignee = foreignKey("fk_person_task_person", personId, TableQuery[Persons])(_.id, onDelete = ForeignKeyAction.Cascade)
    def task = foreignKey("fk_person_task_task", taskId, TableQuery[Tasks])(_.id, onDelete = ForeignKeyAction.Cascade)

    def pkPersonTaks = primaryKey("pk_person_task", (personId, taskId))
  }
  lazy val personTasks = TableQuery[PersonTasks]


  class BankAccounts(tag: Tag) extends IdTable[BankAccount, Int](tag, "ass_bankaccount") {
    def id = column[Int]("account_id", O.PrimaryKey, O.AutoInc)
    def number = column[String]("account_num", O.Length(64))

    def * = (number, id.?) <>(BankAccount.tupled, BankAccount.unapply)

    def owners = {
      for {
        bao <- bankAccountOwners
        owner <- bao.owner if bao.accountId === id
      } yield owner
    }
  }
  lazy val bankAccounts = TableQuery[BankAccounts]
  implicit class BankAccountQueryExt(query: TableQuery[BankAccounts]) extends IdTableExtensions[BankAccount, Int](query)

  class AccountOwners(tag: Tag) extends IdTable[AccountOwner, Int](tag, "ass_bankowner") {
    def id = column[Int]("owner_id", O.PrimaryKey, O.AutoInc)
    def ssn = column[String]("owner_ssn", O.Length(64))

    def * = (ssn, id.?) <>(AccountOwner.tupled, AccountOwner.unapply)

    def accounts = for {
      map <- bankAccountOwners
      account <- map.account if map.ownerId === id
    } yield account
  }
  lazy val accountOwners = TableQuery[AccountOwners]
  implicit class AccountOwnerQueryExt(query: TableQuery[AccountOwners]) extends IdTableExtensions[AccountOwner, Int](query)

  class BankAccountOwners(tag: Tag) extends Table[BankAccountOwner](tag, "ass_bank_account_owner") {
    def accountId = column[Int]("account_id")
    def ownerId = column[Int]("owner_id")

    def * = (accountId, ownerId) <>(BankAccountOwner.tupled, BankAccountOwner.unapply)

    def account = foreignKey("fk_bank_account_owner_account", accountId, TableQuery[BankAccounts])(_.id, onDelete = ForeignKeyAction.Cascade)
    def owner = foreignKey("fk_bank_account_owner_owner", ownerId, TableQuery[AccountOwners])(_.id, onDelete = ForeignKeyAction.Cascade)

    def pkBankAccountOwners = primaryKey("pk_bank_account_owner", (accountId, ownerId))
  }
  lazy val bankAccountOwners = TableQuery[BankAccountOwners]
}
