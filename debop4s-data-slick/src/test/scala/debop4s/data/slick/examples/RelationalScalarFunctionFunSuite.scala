package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

/**
 * RelationalScalaFunctionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RelationalScalarFunctionFunSuite extends AbstractSlickFunSuite {

  test("scalar") {
    withSession { implicit session =>

      def check[T](q: Rep[T], expected: T) = q.run shouldEqual expected
      def checkLit[T: ColumnType](v: T) = check(LiteralColumn(v), v)

      // Literals
      checkLit(false)
      checkLit(true)
      checkLit(42: Byte)
      checkLit(-42: Byte)
      checkLit(42)
      checkLit(-42)
      checkLit(17.5)
      checkLit(-17.5)
      checkLit(17.5f)
      checkLit(-17.5f)
      checkLit(42L)
      checkLit(-42L)
      checkLit("foo")

      check("42".asColumnOf[Int], 42)
      check(LiteralColumn("foo").length, 3)
      check(LiteralColumn("foo") ++ "bar", "foobar")
      check(LiteralColumn(1) ifNull 42, 1)
      check(LiteralColumn[Option[Int]](None) ifNull 42, 42)
      check(LiteralColumn("Foo").toUpperCase, "FOO")
      check(LiteralColumn("Foo").toLowerCase, "foo")
      check(LiteralColumn("  foo  ").ltrim, "foo  ")
      check(LiteralColumn("  foo  ").rtrim, "  foo")

      Functions.database.toLowerCase.run
      Functions.user.toLowerCase.run

      check(LiteralColumn(8) % 3, 2)
      check(LiteralColumn(-12.5).abs, 12.5)
      check(LiteralColumn(1.9).ceil, 2.0)
      check(LiteralColumn(1.5).ceil, 2.0)
      check(LiteralColumn(1.4).ceil, 2.0)
      check(LiteralColumn(-1.9).ceil, -1.0)
      check(LiteralColumn(-1.5).ceil, -1.0)
      check(LiteralColumn(-1.4).ceil, -1.0)
      check(LiteralColumn(1.5).floor, 1.0)
      check(LiteralColumn(1.4).floor, 1.0)
      check(LiteralColumn(-1.5).floor, -2.0)
      check(LiteralColumn(-10.0).sign, -1)

      Functions.pi.toDegrees.run shouldEqual 180.0

      (Functions.pi.toDegrees.toRadians - Functions.pi).abs.run should be <= 0.00001

      val s = "abcdefghijklmnopqrstuvwxyz"
      check(LiteralColumn(s).substring(3, 5), s.substring(3, 5))
      check(LiteralColumn(s).substring(3), s.substring(3))
      check(LiteralColumn(s).take(3), s.take(3))
      check(LiteralColumn(s).drop(3), s.drop(3))

      ifCap(rcap.replace) {
        check(LiteralColumn(s).replace("cd", "XXX"), s.replace("cd", "XXX"))
      }
      ifCap(rcap.reverse) {
        check(LiteralColumn(s).reverseString, s.reverse)
      }
      ifCap(rcap.indexOf) {
        check(LiteralColumn(s).indexOf("o"), s.indexOf("o"))
        check(LiteralColumn(s).indexOf("7"), s.indexOf("7"))
      }
    }
  }

}
