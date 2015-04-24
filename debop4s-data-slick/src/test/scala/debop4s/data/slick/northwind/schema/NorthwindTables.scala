package debop4s.data.slick.northwind.schema

import java.sql.Blob

import debop4s.data.slick.schema.SlickComponent
import org.joda.time.DateTime

/**
 * NorthwindTables
 * @author sunghyouk.bae@gmail.com
 */
trait NorthwindTables {self: SlickComponent =>

  import driver.simple._

  private def addressUnapply(addr: AddressComponent) = AddressComponent.unapply(addr).get

  class Categories(tag: Tag) extends IdTable[Category, Int](tag, "categories") {
    def id = column[Int]("CategoryID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("CategoryName", O.NotNull, O.Length(15, true))
    def description = column[Option[String]]("Description", O.Length(2000, true))
    def picture = column[Option[Blob]]("Picture")

    def * = (name, description, picture, id.?) <>(Category.tupled, Category.unapply)
  }

  lazy val categories = TableQuery[Categories]

  implicit class CategoryQueryExt(query: TableQuery[Categories]) extends IdTableExtensions[Category, Int](query)
  implicit class CategoryExt(self: Category) {
    def save(implicit session: Session) = categories.save(self)
    def delete(implicit session: Session) = categories.remove(self)

    def loadProducts(implicit session: Session) = products.filter(_.categoryId === self.id).list
  }

  class Customers(tag: Tag) extends IdTable[Customer, String](tag, "Customers") {
    def id = column[String]("CustomerID", O.PrimaryKey, O.Length(5, false))
    def companyName = column[String]("CompanyName", O.NotNull, O.Length(40, true))
    def contactName = column[Option[String]]("ContactName", O.Length(30, true))
    def contactTitle = column[Option[String]]("ContactTitle", O.Length(30, true))
    def address = column[Option[String]]("Address", O.Length(60, true))
    def city = column[Option[String]]("City", O.Length(15, true))
    def region = column[Option[String]]("Region", O.Length(15, true))
    def postalCode = column[Option[String]]("PostalCode", O.Length(10, true))
    def country = column[Option[String]]("Country", O.Length(15, true))
    def phone = column[Option[String]]("Phone", O.Length(24, true))
    def fax = column[Option[String]]("Fax", O.Length(24, true))

    /** Address Component 를 변환하는 예 */
    def * = (companyName,
              contactName,
              contactTitle,
              (address, city, region, postalCode, country),
              phone,
              fax,
              id.?).shaped <>( {
      case (companyName, contactName, contactTitle, addrComponent, phone, fax, id) =>
        Customer(companyName, contactName, contactTitle, Some(AddressComponent.tupled.apply(addrComponent)), phone, fax, id)
    }, {
      c: Customer =>
        Some((c.companyName, c.contactName, c.contactTitle, addressUnapply(c.address.get), c.phone, c.fax, c.id))
    })
  }
  lazy val customers = TableQuery[Customers]
  implicit class CustomerQueryExt(query: TableQuery[Customers]) extends IdTableExtensions[Customer, String](query)
  implicit class CustomerExt(self: Customer) {
    def save(implicit session: Session) = customers.save(self)
    def delete(implicit session: Session) = customers.remove(self)

    def getOrders(implicit sess: Session): List[Order] = orders.filter(_.customerId === self.id.bind).list
  }

  class Employees(tag: Tag) extends IdTable[Employee, Int](tag, "Employees") {
    def id = column[Int]("EmployeeID", O.PrimaryKey, O.AutoInc)
    def lastname = column[String]("LastName", O.NotNull, O.Length(20, true))
    def firstname = column[String]("FirstName", O.NotNull, O.Length(10, true))
    def title = column[Option[String]]("Title", O.Length(30, true))
    def titleOfCourtesy = column[Option[String]]("TitleOfCourtesy", O.Length(25, true))
    def birthDate = column[DateTime]("BirthDate")
    def hireDate = column[DateTime]("HireDate")

    def address = column[Option[String]]("Address", O.Length(60, true))
    def city = column[Option[String]]("City", O.Length(15, true))
    def region = column[Option[String]]("Region", O.Length(15, true))
    def postalCode = column[Option[String]]("PostalCode", O.Length(10, true))
    def country = column[Option[String]]("Country", O.Length(15, true))

    def homePhone = column[Option[String]]("homePhone", O.Length(24, true))
    def extension = column[Option[String]]("Extension", O.Length(4, true))

    def photo = column[Option[Blob]]("Photo")
    def notes = column[String]("Notes", O.NotNull, O.Length(2000, true))
    def reportsTo = column[Option[Int]]("ReportsTo")
    def photoPath = column[Option[String]]("PhotoPath", O.Length(255, true))
    def salary = column[Option[Float]]("Salary")

    def * = (lastname,
              firstname,
              title,
              titleOfCourtesy,
              birthDate.?,
              hireDate.?,
              (address, city, region, postalCode, country),
              homePhone,
              extension,
              photo,
              notes,
              reportsTo,
              photoPath,
              salary,
              id.?).shaped <>( {
      case (lastname, firstname, title, titleOfCourtesy, birthDate, hireDate,
      addrComponent,
      homePhone, extension, photo, notes, reportsTo, photoPath, salary, id) =>
        Employee(lastname, firstname, title, titleOfCourtesy, birthDate, hireDate,
                 Some(AddressComponent.tupled.apply(addrComponent)),
                 homePhone, extension, photo, notes, reportsTo, photoPath, salary, id)
    }, {
      e: Employee =>
        Some((e.lastname, e.firstname, e.title, e.titleOfCoutesy, e.birthDate, e.hireDate,
               addressUnapply(e.address.get),
               e.homePhone, e.extension, e.photo, e.notes, e.reportsTo, e.photoPath, e.salary, e.id))
    })
  }

  lazy val employees = TableQuery[Employees]
  implicit class EmployeeQueryExt(query: TableQuery[Employees]) extends IdTableExtensions[Employee, Int](query)
  implicit class EmployeeExt(self: Employee) {
    def getOrders(implicit sess: Session): List[Order] =
      orders.filter(_.employeeId === self.id.bind).list

    def getTerritories(implicit sess: Session): Seq[Territory] = {
      val q = for {
        et <- employeeTerritories if et.employeeId === self.id
        t <- et.territoryFK
      } yield t
      q.run
    }
  }

  class EmployeeTerritories(tag: Tag) extends Table[(Int, String)](tag, "EmployeeTerritories") {
    def employeeId = column[Int]("EmployeeID", O.NotNull)
    def territoryId = column[String]("TerritoryID", O.NotNull, O.Length(20, true))

    def * = (employeeId, territoryId)

    def employeeFK = foreignKey("FK_EmployeeTerritories_Employees", employeeId, employees)(_.id)
    def territoryFK = foreignKey("FK_EmployeeTerritories_Territories", territoryId, territories)(_.id)
  }
  lazy val employeeTerritories = TableQuery[EmployeeTerritories]

  //  object EmployeeTerritories extends TableQuery(new EmployeeTerritories(_)) {
  //
  //    // TODO: join 구문으로 변경
  //    def filterEmployees(territoryId: String) = {
  //      //    val empIds = this.filter(_.territoryId === territoryId.bind).map(_.employeeId)
  //      //    Employees.filter(_.id in empIds)
  //      for {
  //        et <- this if et.territoryId === territoryId.bind
  //        emp <- Employees if emp.id === et.employeeId
  //      } yield emp
  //    }
  //
  //    def filterTerritories(empId: Int) = {
  //      //    val territorieIds = this.filter(_.employeeId === empId.bind).map(_.territoryId)
  //      //    Territories.filter(_.id in territorieIds)
  //      for {
  //        et <- this if et.employeeId === empId.bind
  //        territory <- Territories if territory.id === et.territoryId
  //      } yield territory
  //    }
  //  }

  class OrderDetails(tag: Tag) extends Table[OrderDetail](tag, "Order Details") {
    def orderId = column[Int]("OrderID", O.PrimaryKey)
    def productId = column[Int]("ProductID")
    def unitPrice = column[BigDecimal]("UnitPrice", O.DBType("DECIMAL(10,4)"), O.Default(0.0000))
    def quantity = column[Short]("Quantity", O.Default(1))
    def discount = column[Double]("Discount", O.Default(0.0))

    def * = (orderId, productId, unitPrice, quantity, discount) <>(OrderDetail.tupled, OrderDetail.unapply)

    def orderFK = foreignKey("FK_OrderDetails_Orders", orderId, orders)(_.id)
    def productFK = foreignKey("FK_OrderDetails_Product", productId, products)(_.id)
  }
  lazy val orderDetails = TableQuery[OrderDetails]

  implicit class OrderDetailExt(self: OrderDetail) {
    def save(implicit sess: Session): OrderDetail = orderDetails.insertOrUpdate(self).asInstanceOf[OrderDetail]
    def order(implicit sess: Session): Option[Order] =
      orders.filter(_.id === self.orderId.bind).firstOption
    def product(implicit sess: Session): Option[Product] =
      products.filter(_.id === self.productId.bind).firstOption
  }

  class Orders(tag: Tag) extends IdTable[Order, Int](tag, "Orders") {
    def id = column[Int]("orderID", O.PrimaryKey, O.AutoInc)
    def customerId = column[String]("CustomerID")
    def employeeId = column[Option[Int]]("EmployeeID")
    def orderDate = column[DateTime]("OrderDate")
    def requiredDate = column[DateTime]("RequiredDate")
    def shippedDate = column[DateTime]("ShippedDate")
    def shipVia = column[Option[Int]]("ShipVia")
    // Shippers
    def freight = column[BigDecimal]("Freight", O.Default(0.0))
    def shipName = column[Option[String]]("ShipName")
    def address = column[Option[String]]("ShipAddress", O.Length(60, true))
    def city = column[Option[String]]("ShipCity", O.Length(15, true))
    def region = column[Option[String]]("ShipRegion", O.Length(15, true))
    def postalCode = column[Option[String]]("ShipPostalCode", O.Length(10, true))
    def country = column[Option[String]]("ShipCountry", O.Length(15, true))

    def * = (customerId.?, employeeId, orderDate.?, requiredDate.?, shippedDate.?, shipVia, freight.?, shipName,
              (address, city, region, postalCode, country), id.?).shaped <>( {
      case (customerId, employeeId, orderDate, requiredDate, shippedDate, shipVia, freight, shipName, shipAddr, id) =>
        Order(customerId, employeeId, orderDate, requiredDate, shippedDate, shipVia, freight, shipName, Some(AddressComponent.tupled.apply(shipAddr)), id)
    }, {
      o: Order =>
        Some(o.customerId, o.employeeId, o.orderDate, o.requiredDate, o.shippedDate, o.shipVia, o.freight, o.shipName,
             addressUnapply(o.shipAddress.get), o.id)
    })

    def customerFK = foreignKey("FK_Orders_Customers", customerId, customers)(_.id)
    def employeeFK = foreignKey("FK_Orders_Employees", employeeId, employees)(_.id)
    def shipperFK = foreignKey("FK_Orders_Shippers", shipVia, shippers)(_.id)
  }

  lazy val orders = TableQuery[Orders]
  implicit class OrderQueryExt(query: TableQuery[Orders]) extends IdTableExtensions[Order, Int](query)
  implicit class OrderExt(self: Order) {
    def save(implicit sess: Session) = orders.save(self)
    def customer(implicit sess: Session): Option[Customer] = customers.filter(_.id === self.customerId.bind).firstOption
    def employee(implicit sess: Session): Option[Employee] = employees.filter(_.id === self.employeeId.bind).firstOption
    def shipper(implicit sess: Session): Option[Shipper] = shippers.filter(_.id === self.shipVia.bind).firstOption
  }

  class Products(tag: Tag) extends IdTable[Product, Int](tag, "Products") {
    def id = column[Int]("ProductID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("ProductName", O.NotNull, O.Length(40, true))
    def supplierId = column[Option[Int]]("SupplierID")
    def categoryId = column[Option[Int]]("CategoryID")
    def quantityPerUnit = column[Option[String]]("QuantityPerUnit", O.Length(20, true))
    def unitPrice = column[BigDecimal]("UnitPrice", O.Default(0))
    def unitsInStock = column[Short]("UnitsInStock", O.Default(0))
    def unitsOnOrder = column[Short]("UnitsOnOrder", O.Default(0))
    def reorderLevel = column[Short]("ReorderLevel", O.Default(0))
    def discontinued = column[Boolean]("Discontinued", O.Default(false))

    def * = (name, supplierId, categoryId,
              quantityPerUnit, unitPrice.?, unitsInStock.?, unitsOnOrder.?,
              reorderLevel.?, discontinued, id.?) <>(Product.tupled, Product.unapply)

    def idxName = index("ProductName", name)
    def categoryFK = foreignKey("FK_Products_Categories", categoryId, categories)(_.id, onDelete = ForeignKeyAction.Cascade)
    def supplierFK = foreignKey("FK_Products_Suppliers", supplierId, suppliers)(_.id, onDelete = ForeignKeyAction.Cascade)
  }
  lazy val products = TableQuery[Products]
  implicit class ProductQueryExt(query: TableQuery[Products]) extends IdTableExtensions[Product, Int](query)
  implicit class ProductExt(self: Product) {
    def save(implicit sess: Session): Product = products.save(self)
    def delete(implicit sess: Session): Boolean = products.remove(self)
    def category(implicit sess: Session) = categories.filter(_.id === self.categoryId.bind).firstOption
    def supplier(implicit sess: Session) = suppliers.filter(_.id === self.supplierId.bind).firstOption
  }

  class Regions(tag: Tag) extends Table[Region](tag, "Region") {
    def id = column[Int]("RegionID", O.NotNull)
    def description = column[String]("RegionDescription", O.NotNull, O.Length(50, true))

    def * = (id, description) <>(Region.tupled, Region.unapply)
  }
  lazy val regions = TableQuery[Regions]
  implicit class RegionExt(self: Region) {
    def getTerritories(implicit sess: Session) = territories.filter(_.regionId === self.id).list
    def getEmployees(implicit sess: Session) = {
      val q = for {
        t <- territories if t.regionId === self.id.bind
        et <- employeeTerritories if et.territoryId === t.id
        emp <- employees if emp.id === et.employeeId
      } yield emp

      q.run.toList
    }
  }

  class Shippers(tag: Tag) extends IdTable[Shipper, Int](tag, "Shippers") {
    def id = column[Int]("ShipperID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("CompanyName", O.NotNull, O.Length(40, true))
    def phone = column[String]("Phone")

    def * = (name, phone.?, id.?) <>(Shipper.tupled, Shipper.unapply)
  }
  lazy val shippers = TableQuery[Shippers]
  implicit class ShipperQueryExt(query: TableQuery[Shippers]) extends IdTableExtensions[Shipper, Int](query)
  implicit class ShipperExt(self: Shipper) {
    def getOrders(implicit sess: Session) = orders.filter(_.shipVia === self.id.bind).list
  }

  class Suppliers(tag: Tag) extends IdTable[Supplier, Int](tag, "Suppliers") {
    def id = column[Int]("SupplierID", O.PrimaryKey, O.AutoInc)
    def companyName = column[String]("CompanyName", O.NotNull, O.Length(40, true))
    def contactName = column[Option[String]]("ContactName", O.Length(30, true))
    def contactTitle = column[Option[String]]("ContactTitle", O.Length(30, true))
    def address = column[Option[String]]("Address", O.Length(60, true))
    def city = column[Option[String]]("City", O.Length(15, true))
    def region = column[Option[String]]("Region", O.Length(15, true))
    def postalCode = column[Option[String]]("PostalCode", O.Length(10, true))
    def country = column[Option[String]]("Country", O.Length(15, true))
    def phone = column[Option[String]]("Phone", O.Length(24, true))
    def fax = column[Option[String]]("Fax", O.Length(24, true))
    def homepage = column[Option[String]]("HomePage", O.Length(15, true))

    // NOTE: Option 에 대한 처리에 문제가 있을 수 있다.
    def * = (
              companyName,
              contactName,
              contactTitle,
              (address, city, region, postalCode, country),
              phone,
              fax,
              homepage,
              id.?
              ).shaped <>( {
      case (companyName, contactName, contactTitle, addrComponent, phone, fax, homepage, id) =>
        Supplier(companyName, contactName, contactTitle, Some(AddressComponent.tupled.apply(addrComponent)), phone, fax, homepage, id)
    }, { s: Supplier =>
      Some((s.companyName, s.contactName, s.contactTitle, addressUnapply(s.address.get), s.phone, s.fax, s.homepage, s.id))
    })
  }
  lazy val suppliers = TableQuery[Suppliers]
  implicit class SupplierQueryExt(query: TableQuery[Suppliers]) extends IdTableExtensions[Supplier, Int](query)
  implicit class SupplierExt(self: Supplier) {
    def save(implicit sess: Session): Supplier = suppliers.save(self)
    def delete(implicit sess: Session): Boolean = suppliers.remove(self)
    def getOrders(implicit sess: Session): List[Order] = orders.filter(_.shipVia === self.id.bind).list
  }

  class Territories(tag: Tag) extends IdTable[Territory, String](tag, "Territories") {
    def id = column[String]("TerritoryID", O.NotNull)
    def description = column[String]("TerritoryDescription", O.NotNull)
    def regionId = column[Int]("RegionID", O.NotNull)

    def * = (description, regionId, id.?) <>(Territory.tupled, Territory.unapply)

    def regionFK = foreignKey("FK_Territories_Region", regionId, regions)(_.id)
  }

  lazy val territories = TableQuery[Territories]
  implicit class TerritoryQueryExt(query: TableQuery[Territories]) extends IdTableExtensions[Territory, String](query)
  implicit class TerritoryExt(self: Territory) {
    def save(implicit sess: Session): Territory = territories.save(self)
    def delete(implicit sess: Session): Boolean = territories.remove(self)
    def getRegion(implicit sess: Session): Option[Region] =
      regions.filter(_.id === self.regionId.bind).firstOption

    def getEmployees(implicit sess: Session): List[Employee] = {
      val q = for {
        et <- employeeTerritories if et.territoryId === self.id.bind
        emp <- et.employeeFK
      } yield emp

      q.list
    }
  }
}
