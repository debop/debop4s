package debop4s.data.slick3.tests

import debop4s.data.slick3._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.AbstractSlickFunSuite

import scala.concurrent.Future

/**
 * SequenceFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class SequenceFunSuite extends AbstractSlickFunSuite {

  test("sequence 1") {
    ifNotCapF(scap.sequence) {
      cancel("Sequence 를 지원하지 않습니다.")
      Future {}
    }
    if (SlickContext.isMySQL) {
      cancel("MySQL은 지원하지 않습니다.")
    }
    if (SlickContext.isHqlDB) {
      cancel("HqlDB를 지원하지 않습니다.")
    }
    case class User(id: Int, first: String, last: String)

    class Users(tag: Tag) extends Table[Int](tag, "sequence_users_1") {
      def id = column[Int]("id", O.PrimaryKey)
      def * = id
    }
    val users = TableQuery[Users]

    // 사용자의 Sequence
    val mySequence = Sequence[Int]("s1") start 200 inc 10

    val schema = users.schema ++ mySequence.schema

    db.withPinnedSession(
      schema.drop.asTry,
      schema.create,
      users ++= Seq(1, 2, 3)
    )

    val q1 = for (u <- users) yield (mySequence.next, u.id)
    q1.to[Set].exec shouldEqual Set((200, 1), (210, 2), (220, 3))

    schema.drop.exec
  }

  test("sequence 2") {
    ifNotCapF(scap.sequence) {
      cancel("Sequence 를 지원하지 않습니다.")
      Future {}
    }
    if (SlickContext.isMySQL) {
      cancel("MySQL은 지원하지 않습니다.")
    }

    val s1 = Sequence[Int]("s1")
    val s2 = Sequence[Int]("s2") start 3
    val s3 = Sequence[Int]("s3") start 3 inc 2
    val s4 = Sequence[Int]("s4").cycle start 3 min 2 max 5
    val s5 = Sequence[Int]("s5").cycle start 3 min 2 max 5 inc -1
    val s6 = Sequence[Int]("s6") start 3 min 2 max 5

    def values(s: Sequence[Int], count: Int = 5, create: Boolean = true) = {
      val q = Query(s.next)

      (
      if (create) { s.schema.drop.asTry >> s.schema.create }
      else DBIO.successful()
      ) >>
      DBIO.sequence((1 to count).map { _ => q.result.map(_.head) })
    }

    db.withPinnedSession(
      values(s1).map(_ shouldEqual Seq(1, 2, 3, 4, 5)),
      values(s2).map(_ shouldEqual Seq(3, 4, 5, 6, 7)),
      values(s3).map(_ shouldEqual Seq(3, 5, 7, 9, 11)),
      ifCap(scap.sequenceMin, scap.sequenceMax, scap.sequenceCycle) {
        DBIO.seq(
          values(s4).map(_ shouldEqual Seq(3, 4, 5, 2, 3)),
          values(s5).map(_ shouldEqual Seq(3, 2, 5, 4, 3))
        )
      },
      ifCap(scap.sequenceMin, scap.sequenceMax, scap.sequenceLimited) {
        DBIO.seq(
          values(s6, 3).map(_ shouldEqual List(3, 4, 5)),
          values(s6, 1, false).failed
        )
      }
    )
  }

}
