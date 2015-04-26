package debop4s.data.slick3.tests

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._


/**
 * PrimaryKeyFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PrimaryKeyFunSuite extends AbstractSlickFunSuite {

  test("primary key") {

    class A(tag: Tag) extends Table[(Int, Int, String)](tag, "primarykey_a") {
      def k1 = column[Int]("k1")
      def k2 = column[Int]("k2")
      def s = column[String]("s")
      def * = (k1, k2, s)
      def pk = primaryKey("pk_primarykey_a", (k1, k2))
    }
    val as = TableQuery[A]

    as.baseTableRow.primaryKeys.map(_.name).toSet shouldBe Set("pk_primarykey_a")

    // asTry 는 Try {} 구문과 같고,
    // failed 는 실패 해야 하는 action을 뜻 함.
    commit {
      DBIO.seq(as.schema.drop.asTry,
               as.schema.create,
               as ++= Seq(
                 (1, 1, "a11"),
                 (1, 2, "a12"),
                 (2, 1, "a21"),
                 (2, 2, "a22")
               ),
               (as +=(1, 1, "a11-confilict")).failed,
               as.schema.drop
      )
    }
  }
}
