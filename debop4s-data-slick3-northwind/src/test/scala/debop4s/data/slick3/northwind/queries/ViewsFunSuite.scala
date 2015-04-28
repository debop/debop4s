package debop4s.data.slick3.northwind.queries

import debop4s.core.concurrent._
import debop4s.data.slick3.northwind.AbstractNorthwindFunSuite
import debop4s.data.slick3.northwind.NorthwindDatabase._
import debop4s.data.slick3.northwind.NorthwindDatabase.driver.api._
import slick.jdbc.{StaticQuery => Q}

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Northwind DB에 정의된 View 를 쿼리로 구현한 내용을 테스트합니다.
 * @author sunghyouk.bae@gmail.com
 */
class ViewsFunSuite extends AbstractNorthwindFunSuite {

  test("alphabetical list of Products") {
    val categoryName = "Seafood"

    val (p1, p2) = readonly(
      alphabeticalListOfProducts.result,
      alphabeticalListOfProducts.filter(_._2 === categoryName.bind).result
    )

    p1 foreach { p => log.debug(s"product=$p") }
    p1.size should be > 0

    p2 foreach { p => log.debug(s"product=$p") }
    p2.size should be > 0
  }

  test("category sales for 1997") {
    val rs = readonly { categorySalesFor1997.result }
    rs foreach {
      case (cname, Some(sum)) => log.debug(s"category=$cname, sum=$sum")
    }
    rs.size should be > 0
  }

  test("current product list") {
    val rs = readonly { currentProductList.result }
    rs foreach {
      case (id, name) => log.debug(s"product id=$id, name=$name")
    }
    rs.size should be > 0
  }

  test("customer and suppliers by city") {
    val rs = readonly { customerAndSuppliersByCity.result }
    rs foreach {
      case (city, companyName, contactName, typeName) =>
        log.debug(s"city=$city, companyName=$companyName, contactName=$contactName, $typeName=$typeName")
    }
    rs.size should be > 0
  }

  test("orderDetails extended") {
    val rs = readonly { orderDetailsExtended.result }
    rs foreach {
      case (_, _, productName, _, _, _, extendedPrice) =>
        log.debug(s"productName=$productName, extendedPrice=$extendedPrice")
    }
    rs.size should be > 0
  }

  test("Order SubTotals") {
    val rs = readonly { orderSubtotals.result }
    rs foreach {
      case (orderId, subTotal) => log.debug(s"orderId=$orderId, subTotal=$subTotal")
    }
    rs.size should be > 0
  }

  test("order with price") {
    val rs = readonly { orderWithPrices.result }
    rs foreach { case (orderId, _, _, _, price, pp) =>
      log.debug(s"orderId=$orderId, price=$price, pp=$pp")
    }
    rs.size should be > 0
  }

  test("order and customer join") {
    val rs = readonly { ordersQry.result }
    rs foreach { case (order, customer) =>
      log.debug(s"order=$order, customer=$customer")
    }
    rs.size should be > 0
  }

  test("product sales for 1997") {
    val rs = readonly { productSalesFor1997.result }
    rs foreach { case (pname, cname, amount) =>
      log.debug(s"product=$pname, customer=$cname, amount=$amount")
    }
    rs.size should be > 0
  }

  test("products above average price") {
    val rs1 = readonly { productsAboveAveragePrice.result }
    val rs2 = readonly { productsAboveAveragePrice2.result }

    rs1 shouldEqual rs2
  }

  test("product by category") {
    val rs1 = readonly { productByCategoryWithInnerJoin.result }
    val rs2 = readonly { productByCategoryWithImplicitJoin.result }

    rs1 foreach { case (cname, pname, quantityPerUnit, unitsInStock, _) =>
      log.debug(s"category=$cname, product=pname, quantityPerUnit=$quantityPerUnit, unitsInStock=$unitsInStock")
    }
    rs1.size should be > 0

    rs2 foreach { case (cname, pname, quantityPerUnit, unitsInStock, _) =>
      log.debug(s"category=$cname, product=pname, quantityPerUnit=$quantityPerUnit, unitsInStock=$unitsInStock")
    }
    rs2.size should be > 0
  }

  test("quarterly orders") {

    val (rs1, rs2, rs3) = readonly(quarterlyOrders.result,
                                   quarterlyOrdersByParams(yr.start, yr.end).result,
                                   quarterlyOrdersByParamsCompiled(yr.start, yr.end).result
    )

    rs1 foreach { case (cid, cname, city, country) =>
      log.debug(s"cid=$cid, cname=$cname, city=$city, country=$country")
    }
    rs1.size should be > 0

    rs2 foreach { case (cid, cname, city, country) =>
      log.debug(s"cid=$cid, cname=$cname, city=$city, country=$country")
    }
    rs2.size should be > 0

    rs3 foreach { case (cid, cname, city, country) =>
      log.debug(s"cid=$cid, cname=$cname, city=$city, country=$country")
    }
    rs3.size should be > 0

    rs1 shouldEqual rs2
    rs2 shouldEqual rs3
    rs3 shouldEqual rs1
  }

  test("sales by category") {
    val rs = readonly { salesTotalsByAmount.result }
    rs foreach { case (subTotal, oid, companyName, shippedDate) =>
      log.debug(s"subTotal=$subTotal, oid=$oid, companyName=$companyName, shippedDate=$shippedDate")
    }
    rs.size should be > 0
  }

  test("summary of sales by quarter") {
    val rs = readonly { summaryOfSalesByQuarter.result }
    rs foreach { case (shippedDate, oid, subtotal) =>
      log.debug(s"shipped date=$shippedDate, oid=$oid, subtotal=$subtotal")
    }
    rs.size should be > 0
  }

  test("summary of sales by year") {
    val rs = readonly { summaryOfSalesByYear.result }
    rs foreach { case (shippedDate, oid, subtotal) =>
      log.debug(s"shipped date=$shippedDate, oid=$oid, subtotal=$subtotal")
    }
    rs.size should be > 0
  }

}
