package debop4s.data.slick.examples

import java.sql.{ Date, Time, Timestamp }

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

/**
 * JdbcScalaFunctionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcScalaFunctionFunSuite extends AbstractSlickFunSuite {

  test("scala function") {
    withSession { implicit session =>
      def check[T](q: Rep[T], exp: T) = q.run shouldEqual exp
      def checkLit[T: ColumnType](v: T) = check(LiteralColumn(v), v)

      checkLit(Date.valueOf("2011-07-15"))
      checkLit(Time.valueOf("14:53:21"))
      checkLit(Timestamp.valueOf("2011-07-15 15:53:31"))

      val myExpr = SimpleExpression.binary[Int, Int, Int] { (left, right, queryBuilder) =>
        queryBuilder.sqlBuilder += '('
        queryBuilder.expr(left)
        queryBuilder.sqlBuilder += '+'
        queryBuilder.expr(right)
        queryBuilder.sqlBuilder += "+1)"
      }
      check(myExpr(4, 5), 10)
    }
  }

}
