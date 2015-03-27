package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * PrimaryKeyFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PrimaryKeyFunSuite extends AbstractSlickFunSuite {

  test("primary key") {
    class A(tag: Tag) extends Table[(Int, Int, String)](tag, "primary_key_a") {
      def k1 = column[Int]("k1")
      def k2 = column[Int]("k2")
      def s = column[String]("s")
      def * = (k1, k2, s)
      def pk = primaryKey("pk_primary_key_a", (k1, k2))
    }
    val as = TableQuery[A]

    as.baseTableRow.primaryKeys foreach println
    as.baseTableRow.primaryKeys.map(_.name).toSet shouldEqual Set("pk_primary_key_a")

    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create

      as ++= Seq(
                  (1, 1, "a11"),
                  (1, 2, "a12"),
                  (2, 1, "a21"),
                  (2, 2, "a22")
                )

      intercept[Exception] {
        as +=(1, 1, "all-conflict")
      }
    }
  }

}
