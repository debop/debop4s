package debop4s.data.slick.northwind.queries

import debop4s.data.slick.northwind.AbstractNorthwindFunSuite
import debop4s.data.slick.northwind.NorthwindDatabase._
import debop4s.data.slick.northwind.NorthwindDatabase.driver.simple._
import debop4s.timeperiod.utils.Times

import scala.slick.jdbc.{StaticQuery => Q}

/**
 * ViewsFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ViewsFunSuite extends AbstractNorthwindFunSuite {

  test("alphabetical list of Products") {
    withReadOnly { implicit session =>
      val p1 = alphabeticalListOfProducts.run
      p1 foreach { p => log.debug(s"product=$p") }
      p1.size should be > 0

      val categoryName = "Seafood"
      val p2 = alphabeticalListOfProducts.filter(_._2 === categoryName.bind).run
      p2 foreach { p => log.debug(s"product=$p") }
      p2.size should be > 0
    }
  }

  test("category sales for 1997") {
    withReadOnly { implicit session =>
      val r = categorySalesFor1997.run
      r foreach { case (cname, Some(sum)) => log.debug(s"category=$cname, sum=$sum") }
      r.size should be > 0
    }
  }

  test("current product list") {
    withReadOnly { implicit session =>
      val r = currentProductList.run
      r foreach { case (id, name) => log.debug(s"product id=$id, name=$name") }
      r.size should be > 0
    }
  }

  test("customer and suppliers by city") {
    withReadOnly { implicit session =>
      val r = customerAndSuppliersByCity.run

      r foreach {
        case (city, companyName, contactName, typeName) =>
          log.debug(s"city=$city, companyName=$companyName, contactName=$contactName, $typeName=$typeName")
      }
      r.size should be > 0
    }
  }

  test("orderDetails extended") {
    withReadOnly { implicit session =>
      val r = orderDetailsExtended.run

      // extended prices 를 SQL로 계산
      r foreach {
        case (_, _, productName, _, _, _, extendedPrice) =>
          log.debug(s"productName=$productName, extendedPrice=$extendedPrice")
      }
      r.size should be > 0
    }
  }

  test("order SubTotals") {
    withReadOnly { implicit session =>
      val r = orderSubtotals.run

      r foreach {
        case (orderId, subTotal) => log.debug(s"orderId=$orderId, subTotal=$subTotal")
      }
      r.size should be > 0
    }
  }

  test("order with price") {
    withReadOnly { implicit session =>
      log.debug(s"${ orderWithPrices.selectStatement }")
      val r = orderWithPrices.run
      r foreach {
        case (oid, _, _, _, price, pp) =>
          log.debug(s"orderId=$oid, price=$price, pp=$pp")
      }
    }
  }

  test("order and customer join") {
    withReadOnly { implicit session =>
      val r = ordersQry.run

      r foreach { case (order, customer) =>
        log.debug(s"order=$order, customer=$customer")
      }
    }
  }

  test("product sales for 1997") {
    withReadOnly { implicit session =>
      val r = productSalesFor1997.run

      r.foreach { case (pname, cname, amount) =>
        log.debug(s"product=$pname, customer=$cname, amount=$amount")
      }
      r.size should be > 0
    }
  }

  test("products above average price") {
    withReadOnly { implicit session =>

      productsAboveAveragePrice.selectStatement shouldEqual productsAboveAveragePrice2.selectStatement

      val r = productsAboveAveragePrice.run
      val r2 = productsAboveAveragePrice2.run

      r.size shouldEqual r2.size
    }
  }

  test("product by category") {
    withReadOnly { implicit session =>
      val r = productByCategoryWithInnerJoin.run

      r foreach { case (cname, pname, quantityPerUnit, unitsInStock, _) =>
        log.debug(s"category=$cname, product=pname, quantityPerUnit=$quantityPerUnit, unitsInStock=$unitsInStock")
      }
      r.size should be > 0

      val r2 = productByCategoryWithImplicitJoin.run
      r2 foreach { case (cname, pname, quantityPerUnit, unitsInStock, _) =>
        log.debug(s"category=$cname, product=pname, quantityPerUnit=$quantityPerUnit, unitsInStock=$unitsInStock")
      }
      r2.size should be > 0
    }
  }

  test("quarterly orders") {
    withReadOnly { implicit session =>
      val r = quarterlyOrders.run
      r foreach { case (cid, cname, city, country) =>
        log.debug(s"cid=$cid, cname=$cname, city=$city, country=$country")
      }
      r.size should be > 0

      val yr = Times.yearRange(Times.asDate(1997, 1, 1))

      val r2 = quarterlyOrdersByParams(yr.start, yr.end).run
      r2 foreach { case (cid, cname, city, country) =>
        log.debug(s"cid=$cid, cname=$cname, city=$city, country=$country")
      }
      r2.size should be > 0

      val r3 = quarterlyOrdersByParamsCompiled(yr.start, yr.end).run
      r3 foreach { case (cid, cname, city, country) =>
        log.debug(s"cid=$cid, cname=$cname, city=$city, country=$country")
      }
      r3.size should be > 0
    }
  }

  test("sales by category") {
    withReadOnly { implicit session =>
      val r = salesTotalsByAmount.run

      r foreach { case (subTotal, oid, companyName, shippedDate) =>
        log.debug(s"subTotal=$subTotal, oid=$oid, companyName=$companyName, shippedDate=$shippedDate")
      }
      r.size should be > 0
    }
  }

  test("summary of sales by quarter") {
    withReadOnly { implicit session =>
      val r = summaryOfSalesByQuarter.run

      r.foreach { case (shippedDate, oId, subtotal) =>
        log.debug(s"shippedDate=$shippedDate, oId=$oId, subtotal=$subtotal")
      }
      r.size should be > 0
    }
  }

  test("summary of sales by year") {
    withReadOnly { implicit session =>
      val r = summaryOfSalesByYear.run

      r.foreach { case (shippedDate, oId, subtotal) =>
        log.debug(s"shippedDate=$shippedDate, oId=$oId, subtotal=$subtotal")
      }
      r.size should be > 0
    }
  }
}
