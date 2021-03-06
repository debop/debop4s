package debop4s.data.slick3.tests

import java.sql._

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._


/**
 * JdbcScalarFunctionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcScalarFunctionFunSuite extends AbstractSlickFunSuite {

  test("scalar function") {
    def check[T](q: Rep[T], exp: T) = q.result.map(_ shouldBe exp)
    def checkLiteral[T: ColumnType](v: T) = check(LiteralColumn(v), v)

    commit {
      DBIO.seq(
      checkLiteral(Date.valueOf("2011-07-15")),
      checkLiteral(Time.valueOf("15:53:31")),
      checkLiteral(Timestamp.valueOf("2011-07-15 15:53:31")), {
        val myExpr = SimpleExpression.binary[Int, Int, Int] { (l, r, qb) =>
          qb.sqlBuilder += '('
          qb.expr(l)
          qb.sqlBuilder += '+'
          qb.expr(r)
          qb.sqlBuilder += " + 1)"
        }
        log.debug(s"myExpr=${ myExpr.toString() }")
        check(myExpr(4, 5), 10)
      }
      )
    }
  }

}
