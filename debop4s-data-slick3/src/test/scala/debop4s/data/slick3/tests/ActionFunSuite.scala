package debop4s.data.slick3.tests

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

import scala.concurrent.Future

/**
 * ActionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ActionFunSuite extends AbstractSlickFunSuite {

  test("simple action as future") {
    class T(tag: Tag) extends Table[Int](tag, "action_t") {
      def a = column[Int]("a")
      def * = a
    }
    lazy val ts = TableQuery[T]

    // 내가 만든 extensions 로 구현한 예제
    commit {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts ++= Seq(2, 3, 1, 5, 4))
    }
    val q1 = ts.sortBy(_.a).map(_.a)
    readonly { q1.result } shouldEqual Seq(1, 2, 3, 4, 5)
    commit { ts.schema.drop }
  }

  //  test("session pinning") {
  //    class T(tag: Tag) extends Table[Int](tag, "t") {
  //      def a = column[Int]("a")
  //      def * = a
  //    }
  //    lazy val ts = TableQuery[T]
  //
  //    val aSetup = ts.schema.create andThen (ts ++= Seq(2, 3, 1, 5, 4))
  //    val aCleanup = ts.schema.drop
  //
  //    val aFused = for {
  //      ((s1, l), s2) <- GetSession zip ts.length.result zip GetSession
  //    }
  //
  //    commit { aSetup >> aFused >> aCleanup }
  //  }

  test("streaming") {
    class T(tag: Tag) extends Table[Int](tag, "streaming_t") {
      def a = column[Int]("a")
      def * = a
    }
    lazy val ts = TableQuery[T]

    val q1 = ts.sortBy(_.a).map(_.a)

    commit {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts ++= Seq(2, 3, 1, 5, 4))
    }

    using(SlickContext.createMasterDB()) { db =>
      val p1 = db.stream(q1.result)
      // debop4s.data.slick3._ 에 있는 extensions 를 사용한 예
      p1.materialize.await shouldEqual Seq(1, 2, 3, 4, 5)
    }
    val qr1 = readonly(q1.result)
    qr1.head shouldEqual 1
    qr1.headOption shouldEqual Some(1)
  }

  test("deep recursion") {
    val a1 = DBIO.sequence((1 to 5000).toSeq.map(i => LiteralColumn(i).result))
    val a2 = DBIO.sequence((1 to 20).toSeq.map(i => if (i % 2 == 0) LiteralColumn(i).result else DBIO.from(Future.successful(i))))
    val a3 = DBIO.sequence((1 to 20).toSeq.map(i => if ((i / 4) % 2 == 0) LiteralColumn(i).result else DBIO.from(Future.successful(i))))

    readonly {
      DBIO.seq(a1.map(_ shouldBe (1 to 5000).toSeq),
               a2.map(_ shouldBe (1 to 20).toSeq),
               a3.map(_ shouldBe (1 to 20).toSeq))
    }
  }
}
