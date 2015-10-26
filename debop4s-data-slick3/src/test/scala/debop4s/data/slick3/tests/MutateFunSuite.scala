package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.{AbstractSlickFunSuite, SlickContext}

import debop4s.data.slick3.SlickContext
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._

import slick.backend.DatabasePublisher

import scala.concurrent.Future


/**
 * MutateFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class MutateFunSuite extends AbstractSlickFunSuite {

  test("mutate") {
    ifNotCapF(jcap.mutable) {
      cancel("not support mutate")
      Future()
    }
    if (SlickContext.isMySQL) {
      cancel("MySQL은 update mutate 를 지원하지 않습니다.")
    }

    class Data(tag: Tag) extends Table[(Int, String)](tag, "DATA") {
      def id = column[Int]("id", O.PrimaryKey)
      def data = column[String]("data")
      def * = (id, data)
    }
    val data = TableQuery[Data]

    var seenEndMarker = false
    commit {
      data.schema.drop.asTry >>
      data.schema.create >>
      (data ++= Seq((1, "a"), (2, "b"), (3, "c"), (4, "d")))
    }

    // BUG: DBIOStreamActionExtensions 에 대한 인자가 모호하다는 버그 (commit 함수가 새로 추가된 후에)
    val publisher = db.stream(data.mutate /*.transactionally*/)

    foreach(publisher) { m =>
      if (!m.end) {
        if (m.row._1 == 1) m.row = m.row.copy(_2 = "aa")
        else if (m.row._1 == 2) m.delete
        else if (m.row._1 == 3) m += ((5, "ee"))
      } else {
        seenEndMarker = true
      }
    }.stay

    seenEndMarker shouldBe false
    readonly { data.sortBy(_.id).result } shouldEqual Seq((1, "aa"), (3, "c"), (4, "d"), (5, "ee"))

    commit { data.schema.drop }
  }

  test("delete mutate") {
    if (SlickContext.isMySQL) {
      cancel("MySQL은 delete mutate 를 지원하지 않습니다.")
    }
    class T(tag: Tag) extends Table[(Int, Int)](tag, "del_mutate_t") {
      def a = column[Int]("A")
      def b = column[Int]("B", O.PrimaryKey)
      def * = (a, b)
    }
    val ts = TableQuery[T]
    def tsByA = ts.findBy(_.a)

    // BUG: DBIOStreamActionExtensions 에 대한 인자가 모호하다는 버그 (commit 함수가 새로 추가된 후에)

    var seenEndMarker = false
    val a = {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts ++= Seq((1, 1), (1, 2), (1, 3), (1, 4))) >>
      (ts ++= Seq((2, 5), (2, 6), (2, 7), (2, 8))) //>>
      //runnableStreamableCompiledQueryActionExtensionMethods(tsByA(1)).mutate(sendEndMarker = true) /*.transactionally*/
    } andThen tsByA(1).mutate(sendEndMarker = true)

    foreach(db.stream(a)) { m =>
      if (!m.end) m.delete
      else {
        seenEndMarker = true
        m += ((3, 9))
      }
    }.stay

    seenEndMarker shouldBe true
    readonly { ts.to[Set].result } shouldBe Set((2, 5), (2, 6), (2, 7), (2, 8), (3, 9))

    readonly { ts.schema.drop }
  }

}
