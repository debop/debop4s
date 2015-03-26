package debop4s.data.slick.examples

import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._
import debop4s.data.slick.{AbstractSlickFunSuite, SlickContext}

import scala.util.Try

/**
 * MutateFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class MutateFunSuite extends AbstractSlickFunSuite {

  test("mutatable") {
    if (SlickContext.isMariaDB || SlickContext.isMySQL) {
      cancel("MariaDB는 mutate 기능을 제공하지 않습니다.")
    }

    class Users(tag: Tag) extends Table[(Int, String, String)](tag, "mutate_users") {
      def id = column[Int]("id", O.PrimaryKey)
      def first = column[String]("first")
      def last = column[String]("last")
      def * = (id, first, last)
    }
    val users = TableQuery[Users]

    withSession { implicit session =>
      Try { users.ddl.drop }
      users.ddl.create

      users ++= Seq((1, "Marge", "Bouvier"),
                     (2, "Homer", "Simpson"),
                     (3, "Bart", "Simpson"),
                     (4, "Carl", "Carlson"))

      LOG.debug("Before mutating:")
      users.foreach { u => LOG.debug(s"  $u") }

      val q1 = for (u <- users if u.last === "Simpson".bind || u.last === "Bouvier") yield u

      /**
       * ResultSet fetch 하면서, row 단위로 DB에 적업이 가능합니다.
       */
      q1.mutate { m =>
        LOG.debug(s"***** ROow: ${ m.row }")
        if (m.row._3 == "Bouvier") m.row = m.row.copy(_3 = "Simpson")
        else if (m.row._2 == "Homer") m.delete()
        else if (m.row._2 == "Bart") m.insert(42, "Lisa", "Simpson")
      }

      LOG.debug("After mutating:")
      users.foreach { u => LOG.debug(s"  $u") }
    }
  }

  test("delete mutate") {
    if (SlickContext.isMariaDB || SlickContext.isMySQL) {
      cancel("MariaDB는 mutate 기능을 제공하지 않습니다.")
    }

    ifCap(jcap.mutable) {
      class T(tag: Tag) extends Table[(Int, Int)](tag, "mutate_delete_t") {
        def a = column[Int]("A")
        def b = column[Int]("B", O.PrimaryKey)
        def * = (a, b)
      }
      val ts = TableQuery[T]
      def tsByA(a: Int) = ts.filter(_.a === a.bind)

      withTransaction { implicit session =>
        Try { ts.ddl.drop }
        ts.ddl.create

        ts.insertAll((1, 1), (1, 2), (1, 3), (1, 4))
        ts.insertAll((2, 5), (2, 6), (2, 7), (2, 8))
      }
      withTransaction { implicit session =>
        tsByA(1).mutate(_.delete())
      }
      withReadOnly { implicit session =>
        ts.buildColl[Set] shouldEqual Set((2, 5), (2, 6), (2, 7), (2, 8))
        ts.list.toSet shouldEqual Set((2, 5), (2, 6), (2, 7), (2, 8))
      }
    }
  }
}
