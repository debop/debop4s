package debop4s.data.slick.northwind.queries

import debop4s.data.slick.northwind.AbstractNorthwindFunSuite
import debop4s.data.slick.northwind.NorthwindDatabase._
import debop4s.data.slick.northwind.NorthwindDatabase.driver.simple._
import debop4s.timeperiod.utils.Times

import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.{StaticQuery => Q, GetResult}

/**
 * QueryFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class QueryFunSuite extends AbstractNorthwindFunSuite {

  case class ProductIdAndName(id: Int, name: String)
  implicit val getProductIdAndName = GetResult(r => ProductIdAndName(r.<<, r.<<))

  test("call views by query") {
    withReadOnly { implicit session =>

      // VIEW 실행
      val r1 = sql"select * from `current product list`".as[ProductIdAndName].list

      r1 foreach { r => log.debug(r.toString) }
      r1.size should be > 0
    }
  }

  test("load categories") {
    val q = categories.sortBy(_.name).map(c => (c.id, c.name))

    withReadOnly { implicit session =>
      val r = q.run
      r.foreach { x => log.debug(x.toString()) }
      r.size should be > 0
    }
  }

  test("products in same category") {

    val q = for {
      (c, p) <- categories join products on (_.id === _.categoryId)
    } yield (c.name, p.name)

    withReadOnly { implicit session =>
      val rs = q.run
      rs foreach {
        case (cname, pname) => log.debug(s"category name=$cname, product name=$pname")
      }
      rs.size should be > 0
    }
  }

  test("simple query for customers") {
    val q1 = customers
    val q2 = customers.filter(_.region.isEmpty)

    withReadOnly { implicit session =>
      val rs1 = q1.run
      rs1 foreach { customer => log.debug(s"customer=$customer") }
      rs1.size should be > 0

      val rs2 = q2.run
      rs2 foreach { customer => log.debug(s"customer=$customer") }
      rs2.size should be > 0
    }
  }

  test("국가별 고객 수 (groupBy country, sort by customerCount desc)") {

    val q = customers
            .groupBy(_.country)
            .map(x => (x._1, x._2.length))
            .sortBy(x => (x._2.desc, x._1))

    withReadOnly { implicit session =>
      val r = q.run
      r foreach {
        case (country, customerCount) => log.debug(s"country=$country, customerCount=$customerCount")
      }
      r.size should be > 0
      r.head shouldEqual(Some("USA"), 13)
    }
  }

  test("find all employees") {
    val q = employees.sortBy(_.birthDate.asc)
    withReadOnly { implicit session =>
      val emps = q.list
      emps foreach { emp => log.debug(s"employee=$emp") }
      emps.size shouldEqual 9
    }
  }

  test("filtering employees") {
    val hireDate = Times.asDate(1993, 1, 1)
    val q = employees.filter(_.hireDate >= hireDate.bind)

    withReadOnly { implicit session =>
      val emps = q.run
      emps foreach { emp => log.debug(s"employee=$emp") }
      emps.size shouldEqual 6
    }
  }

  test("employee null / not null") {
    withReadOnly { implicit sesion =>

      // is null
      val emps = employees.filter(_.region.isEmpty).run
      emps foreach { emp => log.debug(s"employee=$emp") }
      emps.size shouldEqual 4

      // is not null
      val emps2 = employees.filter(_.region.isDefined).run
      emps2 foreach { emp => log.debug(s"employee=$emp") }
      emps2.size shouldEqual 5
    }
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
                                    (titleCourtesy, emps) <- employees.groupBy(_.titleOfCourtesy)
                                  } yield (titleCourtesy, emps.length)
                                  ).sortBy(_._1)

    withReadOnly { implicit session =>
      val countByCountry = qCountByCountry.run
      countByCountry foreach { r => log.debug(s"count by country=$r") }
      countByCountry.size shouldEqual 2

      val groupByCountry = qGroupByCountry.run
      groupByCountry foreach {
        case (country, empCount, lastestHireDate) =>
          log.debug(s"country=$country, empCount=$empCount, lastestHireDate=$lastestHireDate")
      }
      groupByCountry.size shouldEqual 2

      val groupByTitleOfCourtesy = qGroupByTitleOfCourtesy.run
      groupByTitleOfCourtesy foreach {
        case (Some(tc), count) => log.debug(s"tc=$tc, count=$count")
      }
      groupByTitleOfCourtesy.size shouldEqual 4
    }
  }

  test("load order details") {
    withReadOnly { implicit session =>
      val q = orderDetails.sortBy(_.orderId)
      val ods = q.take(10).run
      ods foreach { od => log.debug(s"order details=$od") }
      ods.size shouldEqual 10
    }
    withReadOnly { implicit session =>
      val q = orderDetails.filter(_.discount > 0.0.bind)
      val ods = q.run
      ods.size shouldEqual 0
    }
  }

  test("load products") {
    withSession { implicit session =>
      val all = products.list
      all foreach { p => log.debug(s"product=$p") }
      all.size should be > 0
    }
  }

  test("products with category name") {
    val q = for {
      p <- products
      c <- p.categoryFK
    } yield (p, c.name)

    val implicitJoin = q.sortBy(_._1.name)

    withSession { implicit session =>
      val rs = implicitJoin.run
      rs foreach { case (product, categoryName) => log.debug(s"product=$product, categoryName=$categoryName") }
      rs.size should be > 0
    }

    val q2 = for {
      (p, c) <- products innerJoin categories on (_.categoryId === _.id)
    } yield (p, c.name)

    val explicitJoin = q2.sortBy(_._1.name)

    withSession { implicit session =>
      val rs = explicitJoin.run
      rs foreach { case (product, categoryName) => log.debug(s"product=$product, categoryName=$categoryName") }
      rs.size should be > 0
    }

    withReadOnly { implicit session =>
      implicitJoin.run.size shouldEqual explicitJoin.run.size
    }
  }
}