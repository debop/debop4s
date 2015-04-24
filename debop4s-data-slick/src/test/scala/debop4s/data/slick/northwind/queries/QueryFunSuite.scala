package debop4s.data.slick.northwind.queries

import debop4s.data.slick.northwind.AbstractNorthwindFunSuite
import debop4s.data.slick.northwind.schema.NorthwindDatabase._

import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

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

}
