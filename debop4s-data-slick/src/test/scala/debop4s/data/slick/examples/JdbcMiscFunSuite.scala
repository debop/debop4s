package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * 컬럼 수형이 Option[T] 인 경우와 Nullable 인 경우의 비교
 * @author sunghyouk.bae@gmail.com
 */
class JdbcMiscFunSuite extends AbstractSlickFunSuite {

  test("nullability") {

    class T1(tag: Tag) extends Table[String](tag, "misc_nullable_t1") {
      def a = column[String]("a")
      def * = a
    }
    val t1 = TableQuery[T1]

    class T2(tag: Tag) extends Table[String](tag, "misc_nullable_t2") {
      def a = column[String]("a", O.Nullable)
      def * = a
    }
    val t2 = TableQuery[T2]

    class T3(tag: Tag) extends Table[Option[String]](tag, "misc_nullable_t3") {
      def a = column[Option[String]]("a")
      def * = a
    }
    val t3 = TableQuery[T3]

    class T4(tag: Tag) extends Table[Option[String]](tag, "misc_nullable_t4") {
      def a = column[Option[String]]("a", O.NotNull)
      def * = a
    }
    val t4 = TableQuery[T4]

    val ddl = t1.ddl ++ t2.ddl ++ t3.ddl ++ t4.ddl

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      t1.insert("a")
      t2.insert("a")
      t3.insert(Some("a"))
      t4.insert(Some("a"))

      t2.insert(null.asInstanceOf[String])
      t3.insert(None)

      // 컬럼 기본은 not null 이다.
      intercept[Exception] { t1.insert(null.asInstanceOf[String]) }
      // None을 null로 인식하므로, not null column 에 None 을 넣을 수는 없다.
      intercept[Exception] { t4.insert(None) }
    }
  }

  test("column option") {
    class Foo(tag: Tag) extends Table[String](tag, "misc_otpion_posts") {
      // NOTE: Length 와 DBType을 동시에 정의하면 안됩니다.
      def bar = column[String]("s", O.Length(20, true), O.DBType("VARCHAR(20)"))
      def * = bar
    }
    val foos = TableQuery[Foo]

    intercept[SlickException] {
      withSession { implicit session =>
        Try { foos.ddl.drop }
        foos.ddl.create
        fail("Length 와 DBType 을 동시에 정의할 수 없습니다.")
      }
    }
  }

}
