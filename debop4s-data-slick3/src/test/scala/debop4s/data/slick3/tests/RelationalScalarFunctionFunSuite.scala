package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.AbstractSlickFunSuite

/**
 * RelationalScalarFunctionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RelationalScalarFunctionFunSuite extends AbstractSlickFunSuite {

  def check[T](q: Rep[T], exp: T) = q.result.map(_ shouldEqual exp)
  def checkLiteral[T: ColumnType](v: T) = check(LiteralColumn(v), v)

  test("scalar function") {
    val s = "abcdefghijklmnopqrstuvwxyz"

    autoCommit {
      DBIO.seq(
        // Literals
        checkLiteral(false),
        checkLiteral(true),
        checkLiteral(42: Byte),
        checkLiteral(-42: Byte),
        checkLiteral(42),
        checkLiteral(-42),
        checkLiteral(17.5),
        checkLiteral(-17.5),
        checkLiteral(17.5f),
        checkLiteral(-17.5f),
        checkLiteral(42L),
        checkLiteral(-42L),
        checkLiteral("foo"),

        check("42".asColumnOf[Int], 42),
        check(LiteralColumn("foo").length, 3),
        check(LiteralColumn("foo") ++ "bar", "foobar"),
        check(LiteralColumn(1) ifNull 42, 1),
        check(LiteralColumn[Option[Int]](None) ifNull 42, 42),
        check(LiteralColumn("Foo").toUpperCase, "FOO"),
        check(LiteralColumn("Foo").toLowerCase, "foo"),
        check(LiteralColumn("  foo  ").ltrim, "foo  "),
        check(LiteralColumn("  foo  ").rtrim, "  foo"),

        Functions.database.toLowerCase.result,
        Functions.user.toLowerCase.result,

        check(LiteralColumn(8) % 3, 2),
        check(LiteralColumn(-12.5).abs, 12.5),
        check(LiteralColumn(1.9).ceil, 2.0),
        check(LiteralColumn(1.5).ceil, 2.0),
        check(LiteralColumn(1.4).ceil, 2.0),
        check(LiteralColumn(-1.9).ceil, -1.0),
        check(LiteralColumn(-1.5).ceil, -1.0),
        check(LiteralColumn(-1.4).ceil, -1.0),
        check(LiteralColumn(1.5).floor, 1.0),
        check(LiteralColumn(1.4).floor, 1.0),
        check(LiteralColumn(-1.5).floor, -2.0),
        check(LiteralColumn(-10.0).sign, -1),

        Functions.pi.toDegrees.result.map(_ shouldEqual 180.0),

        (Functions.pi.toDegrees.toRadians - Functions.pi).abs.result.map(_ should be <= 0.00001),

        check(LiteralColumn(s).substring(3, 5), s.substring(3, 5)),
        check(LiteralColumn(s).substring(3), s.substring(3)),
        check(LiteralColumn(s).take(3), s.take(3)),
        check(LiteralColumn(s).drop(3), s.drop(3)),

        ifCap(rcap.replace) {
          check(LiteralColumn(s).replace("cd", "XXX"), s.replace("cd", "XXX"))
        },
        ifCap(rcap.reverse) {
          check(LiteralColumn(s).reverseString, s.reverse)
        },
        ifCap(rcap.indexOf) {
          DBIO.seq(
            check(LiteralColumn(s).indexOf("o"), s.indexOf("o")),
            check(LiteralColumn(s).indexOf("7"), s.indexOf("7"))
          )
        },
        ifCap(rcap.repeat) {
          check(LiteralColumn(s) * 2, s * 2)
        }
      )
    }
  }
}
