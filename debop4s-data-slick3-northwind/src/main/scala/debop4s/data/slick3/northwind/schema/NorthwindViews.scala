package debop4s.data.slick3.northwind.schema

import debop4s.data.slick3.schema.SlickComponent
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * NorthwindViews
 * @author sunghyouk.bae@gmail.com
 */
trait NorthwindViews {self: SlickComponent with NorthwindTables =>

  import driver.api._

  lazy val yr = Times.yearRange(Times.asDate(1997, 1, 1))

  lazy val alphabeticalListOfProducts = {
    val q = for {
      (c, p) <- categories join products on (_.id === _.categoryId) if p.discontinued === false.bind
    } yield (p, c.name)

    q.sortBy(_._2.asc)
  }

  lazy val categorySalesFor1997 = {
    for {
      (categoryName, productSales) <- productSalesFor1997.groupBy(_._1)
    } yield (categoryName, productSales.map(_._3).sum)
  }

  lazy val currentProductList = {
    products.filter(_.discontinued === false.bind).map(p => (p.id, p.name))
  }

  lazy val customerAndSuppliersByCity = {
    val qc = customers.map { c => (c.city, c.companyName, c.contactName, LiteralColumn("Customers")) }
    val qs = suppliers.map { s => (s.city, s.companyName, s.contactName, LiteralColumn("Suppliers")) }

    // sort by city, companyName
    (qc union qs).sortBy(x => (x._1, x._2))
  }

  lazy val invocies = {
    for {
      od <- orderDetails
      o <- od.orderFK
      p <- od.productFK
      s <- o.shipperFK
    } yield (od, o, p, s)
  }

  lazy val orderDetailsExtended = {
    for {
      od <- orderDetails
      p <- od.productFK
      o <- od.orderFK
    } yield (o.id,
      p.id,
      p.name,
      od.unitPrice,
      od.quantity,
      od.discount,
      (od.unitPrice.asColumnOf[Double] * od.quantity.asColumnOf[Double] * (LiteralColumn(1.0) - od.discount) / LiteralColumn(100.0)) * LiteralColumn(100.0)
      )
  }

  lazy val orderDetailsExtends2 = {
    for {
      od <- orderDetails
      p <- od.productFK
    } yield (p.name, od)
  }

  lazy val orderSubtotals = {
    for {
      (oid, ods) <- orderDetails.groupBy(_.orderId)
    } yield (oid,
      ods.map { x =>
        ((x.unitPrice.asColumnOf[Double] * x.quantity.asColumnOf[Double] * (LiteralColumn(1.0) - x.discount) / LiteralColumn(100.0)) * LiteralColumn(100.0)).asColumnOf[BigDecimal]
      }.sum
      )
  }

  lazy val orderWithPrices = {
    orderDetails.map { x =>
      (
        x.orderId,
        x.unitPrice,
        x.quantity,
        x.discount,
        ((x.unitPrice.asColumnOf[Double] * x.quantity.asColumnOf[Double] * (LiteralColumn(1.0) - x.discount) / LiteralColumn(100.0)) * LiteralColumn(100.0)).asColumnOf[BigDecimal],
        (x.unitPrice.asColumnOf[Double] * x.quantity.asColumnOf[Double]).asColumnOf[BigDecimal]
        )
    }
  }

  lazy val ordersQry = {
    //    for {
    //      o <- orders
    //      c <- o.customerFK
    //    } yield (o, c)
    orders join customers on (_.customerId === _.id)
  }

  // TODO : Parmeter 로 Compiled Query 로 만들기!!!
  lazy val productSalesFor1997 = {
    val start = Times.startTimeOfYear(1997)
    val end = Times.startTimeOfYear(1997)

    val salesFor1997 = for {
      od <- orderDetails
      o <- od.orderFK if o.shippedDate.between(start.bind, end.bind)
      p <- od.productFK
      c <- p.categoryFK
    } yield (c.name, p.name, od)

    for {
      ((categoryName, productName), sales) <- salesFor1997.groupBy(x => (x._1, x._2))
    } yield
    (
      categoryName,
      productName,
      sales.map(x => ((x._3.unitPrice.asColumnOf[Double] * x._3.quantity.asColumnOf[Double] * (LiteralColumn(1.0) - x._3.discount) / LiteralColumn(100.0)) * LiteralColumn(100.0)).asColumnOf[BigDecimal]).sum
      )
  }

  lazy val productUnitAveragePrice = products.map(_.unitPrice).avg

  lazy val productsAboveAveragePrice = {
    for {
      p <- products if p.unitPrice > productUnitAveragePrice
    } yield (p.name, p.unitPrice)
  }

  lazy val productsAboveAveragePrice2 = {
    products.filter(_.unitPrice > productUnitAveragePrice).map(p => (p.name, p.unitPrice))
  }

  lazy val productByCategoryWithInnerJoin = {
    for {
      (c, p) <- categories join products on (_.id === _.categoryId) if p.discontinued =!= true.bind
    } yield (c.name, p.name, p.quantityPerUnit, p.unitsInStock, p.discontinued)
  }

  lazy val productByCategoryWithImplicitJoin = {
    for {
      p <- products if p.discontinued =!= true.bind
      c <- p.categoryFK
    } yield (c.name, p.name, p.quantityPerUnit, p.unitsInStock, p.discontinued)
  }

  lazy val quarterlyOrders = {
    val yr = Times.yearRange(Times.asDate(1997, 1, 1))
    for {
      (c, o) <- customers join orders on (_.id === _.customerId) if o.orderDate.between(yr.start.bind, yr.end.bind)
    } yield (c.id, c.companyName, c.city, c.country)
  }

  def quarterlyOrdersByParams(start: DateTime, end: DateTime) = {
    for {
      (c, o) <- customers join orders on (_.id === _.customerId) if o.orderDate.between(start.bind, end.bind)
    } yield (c.id, c.companyName, c.city, c.country)
  }

  lazy val quarterlyOrdersByParamsCompiled = Compiled { (start: Rep[DateTime], end: Rep[DateTime]) =>
    for {
      (c, o) <- customers join orders on (_.id === _.customerId) if o.orderDate.between(start, end)
    } yield (c.id, c.companyName, c.city, c.country)
  }

  lazy val salesByCategory = {
    val q = for {
      (((c, p), od), o) <- categories join products on (_.id === _.categoryId) join orderDetailsExtended on (_._2.id === _._2) join orders on (_._2._1 === _.id)
    } yield (c, p, od, o)

    for {
      ((cid, cname, pname), sq) <- q.filter(x => x._4.orderDate.between(yr.start.bind, yr.end.bind)).groupBy(x => (x._1.id, x._1.name, x._2.name))
    } yield (cid, cname, pname, sq.map(_._3._7).sum) // total amount
  }

  lazy val salesTotalsByAmount = {
    val q = for {
      (oid, subTotal) <- orderSubtotals
      o <- orders if o.id === oid
      c <- customers if c.id === o.customerId
    } yield (subTotal, o.id, c.companyName, o.shippedDate)

    q.filter {
      case (subTotal, _, _, shippedDate) => subTotal > BigDecimal(2500.0).bind && shippedDate.between(yr.start.bind, yr.end.bind)
    }
  }

  lazy val summaryOfSalesByQuarter = {
    for {
      (o, (oid, subTotal)) <- orders join orderSubtotals on (_.id === _._1) if o.shippedDate.isDefined
    } yield (o.shippedDate, oid, subTotal)
  }

  lazy val summaryOfSalesByYear = {
    for {
      (o, (oid, subTotal)) <- orders join orderSubtotals on (_.id === _._1) if o.shippedDate.isDefined
    } yield (o.shippedDate, oid, subTotal)
  }


}
