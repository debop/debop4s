package debop4s.data.slick3.tests

import debop4s.core.concurrent._
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
    db.exec {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts ++= Seq(2, 3, 1, 5, 4))
    }
    val q1 = ts.sortBy(_.a).map(_.a)
    q1.exec shouldEqual Seq(1, 2, 3, 4, 5)
    ts.schema.drop.exec

    // for 구문 방식 : 비동기 방식 작업 시 andThen 과 같은 역할을 수행합니다.
    (
    for {
      _ <- db.run {
        ts.schema.drop.asTry >>
        ts.schema.create >>
        (ts ++= Seq(2, 3, 1, 5, 4))
      }
      q1 = ts.sortBy(_.a).map(_.a)
      f1 = db.run(q1.result)
      r1 <- f1: Future[Seq[Int]]
      _ = r1 shouldEqual List(1, 2, 3, 4, 5)
      _ <- db.run(ts.schema.drop)
    } yield ()
    ).await
  }

  test("session pinning") {
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
    //    { aSetup >> aFused >> aCleanup }.run
  }

  test("streaming") {
    class T(tag: Tag) extends Table[Int](tag, "streaming_t") {
      def a = column[Int]("a")
      def * = a
    }
    lazy val ts = TableQuery[T]

    val q1 = ts.sortBy(_.a).map(_.a)

    val p1 = {
               ts.schema.drop.asTry >>
               ts.schema.create >>
               (ts ++= Seq(2, 3, 1, 5, 4)) >>
               q1.result
             }.stream

    // debop4s.data.slick3._ 에 있는 extensions 를 사용한 예
    p1.materialize.await shouldEqual Seq(1, 2, 3, 4, 5)
    q1.exec.head shouldEqual 1
    q1.exec.headOption shouldEqual Some(1)

    // 기존 예제
    val r: Future[Unit] = for {
      r1 <- p1.materialize
      _ = r1 shouldEqual Seq(1, 2, 3, 4, 5)
      r2 <- db.run(q1.result.head)
      _ = r2 shouldEqual 1
      r3 <- db.run(q1.result.headOption)
      _ = r3 shouldEqual Some(1)
    } yield ()

    r.await

    ts.schema.drop.exec
  }

  test("deep recursion") {
    val a1 = DBIO.sequence((1 to 5000).toSeq.map(i => LiteralColumn(i).result))
    val a2 = DBIO.sequence((1 to 20).toSeq.map(i => if (i % 2 == 0) LiteralColumn(i).result else DBIO.from(Future.successful(i))))
    val a3 = DBIO.sequence((1 to 20).toSeq.map(i => if ((i / 4) % 2 == 0) LiteralColumn(i).result else DBIO.from(Future.successful(i))))

    db.seq(
      a1.map(_ shouldBe (1 to 5000).toSeq),
      a2.map(_ shouldBe (1 to 20).toSeq),
      a3.map(_ shouldBe (1 to 20).toSeq)
    )
  }
}
