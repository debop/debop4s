package debop4s.data.slick3.northwind.schema

import java.sql.Blob

import debop4s.data.slick3.northwind.model._
import debop4s.data.slick3.schema.SlickComponent
import org.joda.time.DateTime
import shapeless._

/**
 * NorthwindTables
 * @author sunghyouk.bae@gmail.com
 */
trait NorthwindTables {self: SlickComponent =>

  import driver.api._

  private def addressUnapply(addr: AddressComponent) = AddressComponent.unapply(addr).get

  class Categories(tag: Tag) extends EntityTable[Category](tag, "Categories") {
    def id = column[Int]("CategoryID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("CategoryName", O.Length(15, true))
    def description = column[Option[String]]("Description", O.Length(2000, true))
    def price = column[Option[Blob]]("Picture")

    def * = (name, description, price, id.?) <>(Category.tupled, Category.unapply)
  }

  lazy val categories = EntityTableQuery[Category, Categories](cons = tag => new Categories(tag),
                                                               idLens = lens[Category] >> 'id
  )

  class Customers(tag: Tag) extends EntityTable[Customer](tag, "Customers") {
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

  lazy val customers = EntityTableQuery[Customer, Customers](cons = tag => new Customers(tag),
                                                             idLens = lens[Customer] >> 'id)

  class Employees(tag: Tag) extends EntityTable[Employee](tag, "Employees") {
    def id = column[Int]("EmployeeID", O.PrimaryKey, O.AutoInc)
    def lastname = column[String]("LastName", O.NotNull, O.Length(20, true))
    def firstname = column[String]("FirstName", O.NotNull, O.Length(10, true))
    def title = column[Option[String]]("Title", O.Length(30, true))
    def titleOfCourtesy = column[Option[String]]("TitleOfCourtesy", O.Length(25, true))
    def birthDate = column[Option[DateTime]]("BirthDate")
    def hireDate = column[Option[DateTime]]("HireDate")

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
              birthDate,
              hireDate,
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

    def getOrders = orders.filter(_.employeeId === id)
  }

  lazy val employees = EntityTableQuery[Employee, Employees](cons = tag => new Employees(tag),
                                                             idLens = lens[Employee] >> 'id)

  class EmployeeTerritories(tag: Tag) extends Table[(Int, String)](tag, "EmployeeTerritories") {
    def employeeId = column[Int]("EmployeeID")
    def territoryId = column[String]("TerritoryID", O.Length(20, true))

    def * = (employeeId, territoryId)

    def employeeFK = foreignKey("FK_EmployeeTerritories_Employees", employeeId, employees)(_.id)
    def territoryFK = foreignKey("FK_EmployeeTerritories_Territory", territoryId, territories)(_.id)
  }

  lazy val employeeTerritories = TableQuery[EmployeeTerritories]

  class OrderDetails(tag: Tag) extends Table[OrderDetail](tag, "Order Details") {
    def orderId = column[Int]("OrderID", O.PrimaryKey)
    def productId = column[Int]("ProductID")
    def unitPrice = column[BigDecimal]("UnitPrice", O.SqlType("DECIMAL(10, 4)"), O.Default(0.000))
    def quantity = column[Short]("Quantity", O.Default(1))
    def discount = column[Double]("Discount", O.Default(0.0))

    def * = (orderId, productId, unitPrice, quantity, discount) <>(OrderDetail.tupled, OrderDetail.unapply)

    def orderFK = foreignKey("FK_OrderDetails_Orders", orderId, orders)(_.id)
    def productFK = foreignKey("FK_OrderDetails_Products", productId, products)(_.id)

  }
  lazy val orderDetails = TableQuery[OrderDetails]


  class Orders(tag: Tag) extends EntityTable[Order](tag, "Orders") {
    def id = column[Int]("orderID", O.PrimaryKey, O.AutoInc)
    def customerId = column[Option[String]]("CustomerID")
    def employeeId = column[Option[Int]]("EmployeeID")
    def orderDate = column[Option[DateTime]]("OrderDate")
    def requiredDate = column[Option[DateTime]]("RequiredDate")
    def shippedDate = column[Option[DateTime]]("ShippedDate")
    def shipVia = column[Option[Int]]("ShipVia")
    // Shippers
    def freight = column[BigDecimal]("Freight", O.Default(0.0))
    def shipName = column[Option[String]]("ShipName")
    def address = column[Option[String]]("ShipAddress", O.Length(60, true))
    def city = column[Option[String]]("ShipCity", O.Length(15, true))
    def region = column[Option[String]]("ShipRegion", O.Length(15, true))
    def postalCode = column[Option[String]]("ShipPostalCode", O.Length(10, true))
    def country = column[Option[String]]("ShipCountry", O.Length(15, true))

    def * = (customerId, employeeId, orderDate, requiredDate, shippedDate, shipVia, freight, shipName,
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

  lazy val orders = EntityTableQuery[Order, Orders](cons = tag => new Orders(tag),
                                                    idLens = lens[Order] >> 'id)


  class Products(tag: Tag) extends EntityTable[Product](tag, "Products") {
    def id = column[Int]("ProductID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("ProductName", O.Length(40, true))
    def supplierId = column[Option[Int]]("SupplierID")
    def categoryId = column[Option[Int]]("CategoryID")
    def quantityPerUnit = column[Option[String]]("QuantityPerUnit", O.Length(20, true))
    def unitPrice = column[Option[BigDecimal]]("UnitPrice", O.Default(Some(0)))
    def unitsInStock = column[Option[Short]]("UnitsInStock", O.Default(Some(0)))
    def unitsOnOrder = column[Option[Short]]("UnitsOnOrder", O.Default(Some(0)))
    def reorderLevel = column[Option[Short]]("ReorderLevel", O.Default(Some(0)))
    def discontinued = column[Boolean]("Discontinued", O.Default(false))

    def * = (name, supplierId, categoryId,
              quantityPerUnit, unitPrice, unitsInStock, unitsOnOrder, reorderLevel,
              discontinued, id.?) <>(Product.tupled, Product.unapply)

    def idxName = index("ProductName", name)
    def categoryFK = foreignKey("FK_Products_Categories", categoryId, categories)(_.id, onDelete = ForeignKeyAction.Cascade)
    def supplierFK = foreignKey("FK_Products_Suppliers", supplierId, suppliers)(_.id, onDelete = ForeignKeyAction.Cascade)
  }

  lazy val products = new EntityTableQuery[Product, Products](cons = tag => new Products(tag),
                                                              idLens = lens[Product] >> 'id)

  class Regions(tag: Tag) extends Table[Region](tag, "Region") {
    def id = column[Int]("RegionID", O.NotNull)
    def description = column[String]("RegionDescription", O.Length(50, true))

    def * = (id, description) <>(Region.tupled, Region.unapply)

    def getTerritories = territories.filter(_.regionId === id)

    def getEmployees = {
      for {
        t <- territories if t.regionId === id
        et <- employeeTerritories if et.territoryId === t.id
        emp <- employees if emp.id === et.employeeId
      } yield emp
    }
  }
  lazy val regions = TableQuery[Regions]

  class Shippers(tag: Tag) extends EntityTable[Shipper](tag, "Shippers") {
    def id = column[Int]("ShipperID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("CompanyName", O.Length(40, true))
    def phone = column[Option[String]]("Phone", O.Length(254, true))

    def * = (name, phone, id.?) <>(Shipper.tupled, Shipper.unapply)
  }
  lazy val shippers = EntityTableQuery[Shipper, Shippers](cons = tag => new Shippers(tag),
                                                          idLens = lens[Shipper] >> 'id)

  class Suppliers(tag: Tag) extends EntityTable[Supplier](tag, "Suppliers") {
    def id = column[Int]("SupplierID", O.PrimaryKey, O.AutoInc)
    def companyName = column[String]("CompanyName", O.Length(40, true))
    def contactName = column[Option[String]]("ContactName", O.Length(30, true))
    def contactTitle = column[Option[String]]("ContactTitle", O.Length(30, true))
    def address = column[Option[String]]("Address", O.Length(60, true))
    def city = column[Option[String]]("City", O.Length(15, true))
    def region = column[Option[String]]("Region", O.Length(15, true))
    def postalCode = column[Option[String]]("PostalCode", O.Length(10, true))
    def country = column[Option[String]]("Country", O.Length(15, true))
    def phone = column[Option[String]]("Phone", O.Length(24, true))
    def fax = column[Option[String]]("Fax", O.Length(24, true))
    def homepage = column[Option[String]]("Homepage", O.Length(15, true))

    def * = (
              companyName, contactName, contactTitle,
              (address, city, region, postalCode, country),
              phone, fax, homepage, id.?).shaped <>( {
      case (companyName, contactName, contactTitle, addrComponent, phone, fax, homepage, id) =>
        Supplier(companyName, contactName, contactTitle, Some(AddressComponent.tupled.apply(addrComponent)), phone, fax, homepage, id)
    }, { s: Supplier =>
      Some((s.companyName, s.contactName, s.contactTitle, addressUnapply(s.address.get), s.phone, s.fax, s.homepage, s.id))
    })
  }

  lazy val suppliers = EntityTableQuery[Supplier, Suppliers](cons = tag => new Suppliers(tag),
                                                             idLens = lens[Supplier] >> 'id)

  class Territories(tag: Tag) extends EntityTable[Territory](tag, "Territories") {
    def id = column[String]("TerritoryID", O.PrimaryKey)
    def description = column[String]("TerritoryDescription")
    def regionId = column[Int]("RegionID", O.NotNull)

    def * = (description, regionId, id.?) <>(Territory.tupled, Territory.unapply)

    def regionFK = foreignKey("FK_Territories_Region", regionId, regions)(_.id)

    def getEmployees = for {
      et <- employeeTerritories if et.territoryId === id
      emp <- et.employeeFK
    } yield emp
  }

  lazy val territories = EntityTableQuery[Territory, Territories](cons = tag => new Territories(tag),
                                                                  idLens = lens[Territory] >> 'id)

}
