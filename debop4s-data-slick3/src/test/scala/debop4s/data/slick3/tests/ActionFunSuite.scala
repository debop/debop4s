package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * ActionFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ActionFunSuite extends AbstractSlickFunSuite {

  test("simple action as future") {
    class T(tag: Tag) extends Table[Int](tag, "t") {
      def a = column[Int]("a")
      def * = a
    }
    lazy val ts = TableQuery[T]

    db.exec {
      ts.schema.create >>
      (ts ++= Seq(2, 3, 1, 5, 4))
    }
    val q1 = ts.sortBy(_.a).map(_.a)
    val f1 = db.run(q1.result)
    f1.await shouldEqual Seq(1, 2, 3, 4, 5)

    db.exec(ts.schema.drop)

    // for 구문 방식 : 비동기 방식 작업 시 andThen 과 같은 역할을 수행합니다.
    for {
      _ <- db.run {
        ts.schema.create >>
        (ts ++= Seq(2, 3, 1, 5, 4))
      }
      q1 = ts.sortBy(_.a).map(_.a)
      f1 = db.run(q1.result)
      r1 <- f1
      _ = r1 shouldEqual List(1, 2, 3, 4, 5)
      _ <- db.run(ts.schema.drop)
    } yield ()
  }

  test("session pinning") {
    //    class T(tag: Tag) extends Table[Int](tag, "t") {
    //      def a = column[Int]("a")
    //      def * = a
    //    }
    //    lazy val ts = TableQuery[T]
    //
    //    val aSetup = ts.schema.create andThen ( ts ++= Seq(2, 3, 1, 5, 4) )
    //    val aCleanup = ts.schema.drop
    //
    //    val aFused = for {
    //      ((s1, l), s2) <- GetSession zip ts.length.result zip GetSession
    //    }
    //
    //    aSetup andThen aFused andThen aCleanup
  }

  test("streaming") {
    class T(tag: Tag) extends Table[Int](tag, "t") {
      def a = column[Int]("a")
      def * = a
    }
    lazy val ts = TableQuery[T]

    val q1 = ts.sortBy(_.a).map(_.a)

    val p1 = db.stream {
      ts.schema.create >>
      (ts ++= Seq(2, 3, 1, 5, 4)) >>
      q1.result
    }

    for {
      r1 <- p1.materialize
      _ = r1 shouldEqual Seq(1, 2, 3, 4, 5)
      r2 <- db.run(q1.result.head)
      _ = r2 shouldEqual 1
      r3 <- db.run(q1.result.headOption)
      _ = r3 shouldEqual Some(1)
    } yield ()
  }
}
