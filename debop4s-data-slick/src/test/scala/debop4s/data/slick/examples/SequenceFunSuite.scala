package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * SequenceFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 27.
 */
class SequenceFunSuite extends AbstractSlickFunSuite {

  test("sequence 1") {
    withSession { implicit session =>
      if (isMySQL) {
        cancel("MySQL은 sequence 를 지원하지 않습니다.")
      }

      case class User(id: Int, first: String, last: String)

      class Users(tag: Tag) extends Table[Int](tag, "sequence_users_1") {
        def id = column[Int]("id", O.PrimaryKey)
        def * = id
      }
      val users = TableQuery[Users]
      // 사용자의 Sequence
      val mySequence = Sequence[Int]("mysequence") start 200 inc 10

      val ddl = users.ddl ++ mySequence.ddl
      ddl.createStatements.foreach(println)
      Try { ddl.drop }
      ddl.create

      users.insertAll(1, 2, 3)

      val q1 = for (u <- users) yield (mySequence.next, u.id)
      LOG.debug("q1: " + q1.selectStatement)
      q1.list.toSet shouldEqual Set((200, 1), (210, 2), (220, 3))

      ifCap(scap.sequenceCurr) {
        mySequence.curr.run shouldEqual 220
      }

      ddl.drop
    }
  }

  test("sequence 2") {
    withSession { implicit session =>
      if (isMySQL) {
        cancel("MySQL은 Sequence 를 지원하지 않습니다.")
      }

      ifCap(scap.sequence) {
        val s1 = Sequence[Int]("s1")
        val s2 = Sequence[Int]("s2") start 3
        val s3 = Sequence[Int]("s3") start 3 inc 2
        val s4 = ( Sequence[Int]("s4") start 3 min 2 max 5 ).cycle
        val s5 = ( Sequence[Int]("s5") start 3 min 2 max 5 inc -1 ).cycle
        val s6 = Sequence[Int]("s6") start 3 min 2 max 5

        def values(s: Sequence[Int], count: Int = 5, create: Boolean = true) = {
          if (create) {
            val ddl = s.ddl
            Try { ddl.drop }
            ddl.createStatements foreach println
            ddl.create
          }
          val q = Query(s.next)
          println(q.selectStatement)
          1 to count map ( _ => q.first )
        }

        values(s1) shouldEqual Seq(1, 2, 3, 4, 5)
        values(s2) shouldEqual Seq(3, 4, 5, 6, 7)
        values(s3) shouldEqual Seq(3, 5, 7, 9, 11)

        ifCap(scap.sequenceMin, scap.sequenceMax) {
          ifCap(scap.sequenceCycle) {
            values(s4) shouldEqual Seq(3, 4, 5, 2, 3)
            values(s5) shouldEqual Seq(3, 2, 5, 4, 3)
          }
          ifCap(scap.sequenceLimited) {
            values(s6, 3) shouldEqual Seq(3, 4, 5)
            // Cycle이 아니므로 끝
            intercept[Exception] { values(s6, 1, false) }
          }
        }
      }
    }
  }

}
