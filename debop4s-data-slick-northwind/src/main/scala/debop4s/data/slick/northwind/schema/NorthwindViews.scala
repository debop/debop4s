package debop4s.data.slick.northwind.schema

import debop4s.data.slick.northwind.model._
import debop4s.data.slick.schema.SlickComponent
import debop4s.data.slick.SlickContext._
import debop4s.data.slick.SlickContext.driver.simple._
import debop4s.timeperiod.utils.Times
import org.joda.time.DateTime

/**
 * NorthwindViews
 * @author sunghyouk.bae@gmail.com
 */
trait NorthwindViews {self: SlickComponent with NorthwindTables =>

  lazy val alphabeticalListOfProducts: Query[(Products, Column[String]), (Product, String), Seq] = {
    val q = for {
      (c, p) <- categories innerJoin products on (_.id === _.categoryId) if p.discontinued === false.bind
    } yield (p, c.name)

    q.sortBy(_._2.asc)
  }

  lazy val categorySalesFor1997 = {
    for {
      (categoryName, productSales) <- productSalesFor1997.groupBy(x => x._1)
    } yield (categoryName, productSales.map(x => x._3).sum)
  }

  lazy val currentProductList =
    products.filter(_.discontinued === false.bind).map(x => (x.id, x.name))

  lazy val customerAndSuppliersByCity = {
    val qc = customers.map(x => (x.city, x.companyName, x.contactName, LiteralColumn("Customers")))
    val qs = suppliers.map(x => (x.city, x.companyName, x.contactName, LiteralColumn("Suppliers")))

    (qc union qs).sortBy(x => (x._1, x._2))
  }

  lazy val invoices =
    for {
      od <- orderDetails
      o <- od.orderFK
      p <- od.productFK
      e <- o.shipperFK
    } yield (od, o, p, e)


  lazy val orderDetailsExtended =
    for {
      od <- orderDetails
      p <- od.productFK
      o <- od.orderFK
    } yield (
      o.id,
      p.id,
      p.name,
      od.unitPrice,
      od.quantity,
      od.discount,
      (od.unitPrice.asColumnOf[Double] * od.quantity.asColumnOf[Double] * (LiteralColumn(1.0) - od.discount) / LiteralColumn(100.0)) * LiteralColumn(100.0)
      )

  lazy val orderDetailsExtended2 =
    for {
      od <- orderDetails
      p <- od.productFK
      o <- od.orderFK
    } yield (p.name, od)

  lazy val orderSubtotals =
    for {
      (oid, ods) <- orderDetails.groupBy(_.orderId)
    } yield (
      oid,
      ods.map { x =>
        ((x.unitPrice.asColumnOf[Double] * x.quantity.asColumnOf[Double] * (LiteralColumn(1.0) - x.discount) / LiteralColumn(100.0)) * LiteralColumn(100.0)).asColumnOf[BigDecimal]
      }.sum
      )

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

    orders innerJoin customers on (_.customerId === _.id)
  }

  // TODO : Parmeter 로 Compiled Query 로 만들기!!!
  lazy val productSalesFor1997 = {
    val start = Times.startTimeOfYear(1997)
    val end = Times.endTimeOfYear(1997)

    val salesFor1997 =
      for {
        od <- orderDetails
        o <- od.orderFK if o.shippedDate.between(start.bind, end.bind)
        p <- od.productFK
        c <- p.categoryFK
      } yield (c.name, p.name, od)

    for {
      ((categoryName, productName), sales) <- salesFor1997.groupBy(x => (x._1, x._2))
    } yield {
      (
        categoryName,
        productName,
        sales.map(x => ((x._3.unitPrice.asColumnOf[Double] *
                         x._3.quantity.asColumnOf[Double] *
                         (LiteralColumn(1.0) - x._3.discount) / LiteralColumn(100.0)) * LiteralColumn(100.0))
                       .asColumnOf[BigDecimal]).sum
        )
    }
  }

  lazy val productUnitAveragePrice = products.map(_.unitPrice).avg

  lazy val productsAboveAveragePrice = {
    for {
      product <- products if product.unitPrice > productUnitAveragePrice
    } yield (product.name, product.unitPrice)
  }

  lazy val productsAboveAveragePrice2 = {
    products.filter(_.unitPrice > productUnitAveragePrice).map(x => (x.name, x.unitPrice))
  }


  lazy val productByCategoryWithInnerJoin = {
    for {
      (c, p) <- categories innerJoin products on (_.id === _.categoryId) if p.discontinued =!= true.bind
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
      (c, o) <- customers innerJoin orders on (_.id === _.customerId) if o.orderDate.between(yr.start.bind, yr.end.bind)
    } yield (c.id, c.companyName, c.city, c.country)
  }

  def quarterlyOrdersByParams(start: DateTime, end: DateTime) = {
    for {
      (c, o) <- customers innerJoin orders on (_.id === _.customerId) if o.orderDate.between(start.bind, end.bind)
    } yield (c.id, c.companyName, c.city, c.country)
  }

  lazy val quarterlyOrdersByParamsCompiled = Compiled { (start: Column[DateTime], end: Column[DateTime]) =>
    for {
      (c, o) <- customers innerJoin orders on (_.id === _.customerId) if o.orderDate.between(start, end)
    } yield (c.id, c.companyName, c.city, c.country)
  }

  lazy val salesByCategory = {
    val q = for {
      (((c, p), od), o) <- categories join products on (_.id === _.categoryId) join orderDetailsExtended on (_._2.id === _._2) join orders on (_._2._1 === _.id)
    } yield (c, p, od, o)

    val yr = Times.yearRange(Times.asDate(1997, 1, 1))

    for {
      ((cid, cname, pname), sq) <- q.filter(x => x._4.orderDate.between(yr.start.bind, yr.end.bind)).groupBy(x => (x._1.id, x._1.name, x._2.name))
    } yield (cid, cname, pname, sq.map(_._3._7).sum)
  }

  lazy val salesTotalsByAmount = {
    val yr = Times.yearRange(Times.asDate(1997, 1, 1))

    val q = for {
      (oid, subTotal) <- orderSubtotals
      o <- orders if o.id === oid
      c <- customers if c.id === o.customerId
    } yield (subTotal, o.id, c.companyName, o.shippedDate)

    q.filter {
      case (subTotal, _, _, shippedDate) => subTotal > BigDecimal(2500.0) && shippedDate.between(yr.start, yr.end)
    }
  }

  lazy val summaryOfSalesByQuarter =
    for {
      (o, (oId, subtotal)) <- orders join orderSubtotals on (_.id === _._1) if o.shippedDate.isNotNull
    } yield (o.shippedDate, oId, subtotal)

  lazy val summaryOfSalesByYear =
    for {
      (o, (oId, subtotal)) <- orders join orderSubtotals on (_.id === _._1) if o.shippedDate.isNotNull
    } yield (o.shippedDate, o.id, subtotal)

}
