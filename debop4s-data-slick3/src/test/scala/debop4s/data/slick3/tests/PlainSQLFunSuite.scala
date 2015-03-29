package debop4s.data.slick3.tests

import debop4s.data.slick3.AbstractSlickFunSuite
import slick.jdbc.GetResult

/**
 * PlainSQLFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PlainSQLFunSuite extends AbstractSlickFunSuite {

  case class User(id: Int, name: String)

  implicit val getUserResult = GetResult(r => new User(r.<<, r.<<))

  test("simple") {
    // TODO
  }

  test("interpolation") {
    // TODO

  }

}
