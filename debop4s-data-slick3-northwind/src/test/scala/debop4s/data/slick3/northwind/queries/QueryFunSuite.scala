package debop4s.data.slick3.northwind.queries

import debop4s.data.slick3.northwind.AbstractNorthwindFunSuite
import debop4s.data.slick3.northwind.NorthwindDatabase._
import debop4s.timeperiod.utils.Times
import slick.jdbc.{GetResult, StaticQuery => Q}

/**
 * QueryFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class QueryFunSuite extends AbstractNorthwindFunSuite {

  import driver.api._

  case class ProductIdAndName(id: Int, name: String)
  implicit val getProductIdAndName = GetResult(r => ProductIdAndName(r.<<, r.<<))

  test("call views by query") {
    val r1 = readonly { sql"select * from `current product list`".as[ProductIdAndName] }
    r1 foreach { r => log.debug(r.toString) }
    r1.size should be > 0
  }

  test("load categories") {
    val q = categories.sortBy(_.name).map(c => (c.id, c.name))

    val r = readonly { q.result }
    r.foreach { x => log.debug(x.toString) }
    r.size should be > 0
  }

  test("products in same category") {
    val q = for {
      (c, p) <- categories join products on (_.id === _.categoryId)
    } yield (c.name, p.name)

    val rs = readonly { q.result }
    rs foreach {
      case (cname, pname) => log.debug(s"category name=$cname, product name=$pname")
    }
    rs.size should be > 0
  }

  test("simple query for customers") {
    val q1 = customers
    val q2 = customers.filter(_.region.isEmpty)

    val rss = readonly { DBIO.sequence(Seq(q1.result, q2.result)) }

    rss foreach { rs =>
      rs foreach { c => log.debug(s"customer=$c") }
      rs.size should be > 0
    }
  }

  test("국가별 고객 수 (groupBy country, sort by customerCount desc)") {
    val q = customers
            .groupBy(_.country)
            .map(x => (x._1, x._2.length)) // groupBy country
            .sortBy(x => (x._2.desc, x._1)) // order by count(*) desc, country asc

    val r = readonly { q.result }

    r foreach {
      case (country, customerCount) => log.debug(s"country=$country, customer count=$customerCount")
    }
    r.size should be > 0
    r.head shouldEqual(Some("USA"), 13)
  }

  test("find all employees") {
    val q = employees.sortBy(_.birthDate.asc)
    val emps = readonly { q.result }

    emps foreach { emp => log.debug(s"employee=$emp") }
    emps.size shouldEqual 9
  }

  test("filtering employees") {
    val hireDate = Times.asDate(1993, 1, 1)
    val q = employees.filter(_.hireDate >= hireDate.bind)

    val emps = readonly { q.result }
    emps foreach { emp => log.debug(s"employee=$emp") }
    emps.size shouldEqual 6
  }

  test("employee null / not null") {

    // is null
    val emps = readonly { employees.filter(_.region.isEmpty).result }
    emps foreach { emp => log.debug(s"employee=$emp") }
    emps.size shouldEqual 4

    // is not null
    val emps2 = readonly { employees.filter(_.region.isDefined).result }
    emps2 foreach { emp => log.debug(s"employee=$emp") }
    emps2.size shouldEqual 5
  }

  test("employee - group by") {
    val qCountByCountry = employees.groupBy(_.country).map(_._2.length)
    val qGroupByCountry = (
                          for {
                            (country, emps) <- employees.groupBy(_.country)
                          } yield (country, emps.length, emps.map(_.hireDate).max)
                          ).sortBy(_._1)
    val qGroupByTitleOfCourtesy = (
                                  for {
                                    (titleOfCourtesy, emps) <- employees.groupBy(_.titleOfCourtesy)
                                  } yield (titleOfCourtesy, emps.length)
                                  ).sortBy(_._1)

    val (countByCountry, groupByCountry, groupByTitleOfCourtesy) =
      readonly(qCountByCountry.result,
                        qGroupByCountry.result,
               qGroupByTitleOfCourtesy.result)

    countByCountry foreach { c => log.debug(s"count by country=$c") }
    countByCountry.size shouldBe 2

    groupByCountry foreach {
      case (country, empCount, latestHireDate) =>
        log.debug(s"country=$country, emp count=$empCount, latest hiredate=$latestHireDate")
    }
    groupByCountry.size shouldBe 2

    groupByTitleOfCourtesy foreach {
      case (Some(tc), count) => log.debug(s"titleOfCourtesy=$tc, count=$count")
    }
    groupByTitleOfCourtesy.size shouldBe 4
  }

  test("load order details") {
    val q = orderDetails.sortBy(_.orderId)
    val q2 = orderDetails.filter(_.discount > 0.0.bind)

    val (ods, ods2) = readonly(q.take(10).result, q2.result)

    ods foreach { od => log.debug(s"order details=$od") }
    ods.size shouldBe 10

    ods2 foreach { od => log.debug(s"order details=$od") }
    ods2.size shouldBe 0
  }

  test("load products") {
    val all = readonly { products.result }
    all foreach { p => log.debug(s"product=$p") }
    all.size should be > 0
  }

  test("products with category name") {
    val q = for {
      p <- products
      c <- p.categoryFK
    } yield (p, c.name)

    val implicitJoin = q.sortBy(_._1.name) // order by product.name

    val q2 = for {
      (p, c) <- products join categories on (_.categoryId === _.id)
    } yield (p, c.name)

    val explicitJoin = q.sortBy(_._1.name) // order by product.name

    val (rs1, rs2) = readonly(implicitJoin.result, explicitJoin.result)

    rs1 foreach { case (product, categoryName) =>
      log.debug(s"product=$product, category name=$categoryName")
    }
    rs1.size should be > 0

    rs2 foreach { case (product, categoryName) =>
      log.debug(s"product=$product, category name=$categoryName")
    }
    rs2.size should be > 0

    rs1 shouldEqual rs2
  }
}
