package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}
import slick.util.TupleMethods._

import scala.concurrent.Future

/**
 * NestingFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class NestingFunSuite extends AbstractSlickFunSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  test("nested tuples") {
    class T(tag: Tag) extends Table[(Int, String, String)](tag, "T") {
      def a = column[Int]("A")
      def b = column[String]("B")
      def c = column[String]("C")
      def * = (a, b, c)
    }
    lazy val ts = TableQuery[T]

    val res1 = List(
      (1, "1", "a", 5), (2, "2", "a", 5), (3, "3", "a", 5),
      (1, "1", "b", 5), (2, "2", "b", 5), (3, "3", "b", 5),
      (1, "1", "c", 5), (2, "2", "c", 5), (3, "3", "c", 5)
    )
    val res1b = res1.map { case (a, b, c, d) => ((a, b), (c, d)) }

    val q1a = (for {
      (a, b) <- ts.map(t => (t.a, t.b))
      c <- ts.map(_.c)
    } yield a ~ b ~ c ~ 5).sortBy(t => t._3 ~ t._1)

    val q1c = (for {
      a ~ b <- ts.map(t => (t.a, t.b))
      c <- ts.map(t => t.c)
    } yield (a, b, c, LiteralColumn(5))).sortBy(t => t._3 ~ t._1)

    val q1d = (for {
      (a, b) <- ts.map(t => (t.a, t.b))
      c <- ts.map(t => t.c)
    } yield ((a, b), (c, 5))).sortBy(t => t._2._1 ~ t._1._1)

    val res2 = Set((1, "1", 8), (2, "2", 10))

    val q2a = for {
      a ~ b ~ c <- ts.filter(_.a === 1).map(t => t.a ~ t.b ~ 4) unionAll ts.filter(_.a === 2).map(t => t.a ~ t.b ~ 5)
    } yield a ~ b ~ (c * 2)

    val q2b = for {
      (a, b, c) <- ts.filter(_.a === 1).map(t => (t.a, t.b, LiteralColumn(4))) unionAll ts.filter(_.a === 2).map(t => (t.a, t.b, LiteralColumn(5)))
    } yield a ~ b ~ (c * 2)

    val q2c = for {
      (a, b, c) <- ts.filter(_.a === 1).map(t => (t.a, t.b, 4)) unionAll ts.filter(_.a === 2).map(t => (t.a, t.b, 5))
    } yield a ~ b ~ (c * 2)

    db.seq(
      ts.schema.create,
      ts ++= Seq((1, "1", "a"), (2, "2", "b"), (3, "3", "c")),
      q1a.result.map(_ shouldBe res1),
      q1c.result.map(_ shouldBe res1),
      q1d.result.map(_ shouldBe res1b),
      q2a.result.map(v => v.toSet shouldBe res2),
      q2b.result.map(v => v.toSet shouldBe res2),
      q2c.result.map(v => v.toSet shouldBe res2),
      ts.schema.drop
    )
  }

  test("nested options") {
    class X(tag: Tag) extends Table[(Int, String, Option[Int])](tag, "X_OPT") {
      def a = column[Int]("a")
      def b = column[String]("b")
      def c = column[Option[Int]]("c")
      def * = (a, b, c)
    }
    lazy val xs = TableQuery[X]

    val q = xs.sortBy(_.a)
    val r = Vector((1, "1", Some(1)), (2, "2", Some(2)), (3, "3", None))

    val setup = xs.schema.create >> (xs ++= r)

    // implicitly 는 implicit 로 전달받는 인자를 정의한 함수를 implicit 변수를 다른 방법으로 표현하는 것입니다.
    //
    /*
    class Pair[T: Ordering](val first: T, val second: T) {
      def smaller(implicit ord:Ordering[T]) =
        if(ord.compare(first, second) < 0) first else second

      def smaller2 =
        if(implicitly[Ordering[T]].compare(first, second) < 0) first else second
    }
    */

    // Construct all kind of Option Shapes
    implicitly[Shape[_, Rep[Int], _, _]]
    implicitly[Shape[_, Rep[Option[Int]], _, _]]
    implicitly[Shape[_, Rep[Option[Option[Int]]], _, _]]
    implicitly[Shape[_, Rep[Option[(Rep[Int], Rep[String])]], _, _]]
    implicitly[Shape[_, Rep[Option[X]], _, _]]

    // Construct all different kinds of Options
    val q1 = q.map(t => Rep.Some(t))
    val q1a2 = q.map(t => Rep.Some(Rep.Some(t)))
    val q2 = q.map(t => Rep.Some(t.a))
    val q2a2 = q.map(t => Rep.Some(Rep.Some(t.a)))
    val q3 = q.map(t => t.c)
    val q4 = q.map(t => Rep.Some(t.c))
    val q5 = q.map(t => (t.c, Rep.Some(t.b)))
    val q1t: Query[Rep[Option[X]], _, Seq] = q1
    val q1a2t: Query[Rep[Option[Option[X]]], _, Seq] = q1a2
    val q2t: Query[Rep[Option[Int]], _, Seq] = q2
    val q2a2t: Query[Rep[Option[Option[Int]]], _, Seq] = q2a2
    val q3t: Query[Rep[Option[Int]], _, Seq] = q3
    val q4t: Query[Rep[Option[Option[Int]]], _, Seq] = q4
    val q5t: Query[(Rep[Option[Int]], Rep[Option[String]]), _, Seq] = q5

    val t1 = DBIO.seq(
      q1.result.map(_ shouldBe r.map(t => Some(t))),
      q1a2.result.map(_ shouldBe r.map(t => Some(Some(t)))),
      q2.result.map(_ shouldBe r.map(t => Some(t._1))),
      q2a2.result.map(_ shouldBe r.map(t => Some(Some(t._1)))),
      q3.result.map(_ shouldBe r.map(t => t._3)),
      q4.result.map(_ shouldBe r.map(t => Some(t._3))),
      q5.result.map(_ shouldBe r.map(t => (t._3, Some(t._2))))
    )

    // Get plain values out
    val q1b = q1.map(_.map(x => (x.a, x.b, x.c)).getOrElse((0, "", None: Option[Int])))
    val q2b = q2.map(_.get)
    val q3b = q3.filter(_.isDefined).map(_.get)
    val q4b = q4.map(_.getOrElse(None: Option[Int]))
    val q1bt: Query[(Rep[Int], Rep[String], Rep[Option[Int]]), (Int, String, Option[Int]), Seq] = q1b
    val q2bt: Query[Rep[Int], Int, Seq] = q2b
    val q3bt: Query[Rep[Int], Int, Seq] = q3b
    val q4bt: Query[Rep[Option[Int]], Option[Int], Seq] = q4b

    val t2 = DBIO.seq(
      q1b.result.map(_ shouldBe r.map(t => Some(t)).map(_.getOrElse((0, "", None: Option[String])))),
      q2b.result.map(_ shouldBe r.map(t => Some(t._1)).map(_.get)),
      q3b.result.map(_ shouldBe r.map(t => t._3).filter(_.isDefined).map(_.get)),
      q4b.result.map(_ shouldBe r.map(t => Some(t._3)).map(_.getOrElse(None: Option[String])))
    )

    // Unpack result types
    def r1: Future[Seq[Option[(Int, String, Option[Int])]]] = db.run(q1.result)
    def r2: Future[Seq[Option[Int]]] = db.run(q2.result)
    def r3: Future[Seq[Option[Int]]] = db.run(q3.result)
    def r2b: Future[Seq[Int]] = db.run(q2b.result)
    def r3b: Future[Seq[Int]] = db.run(q3b.result)

    // Perform Option-mapped operations
    val q2c = q2.map(io => io + 42)
    val q3c = q3.map(so => so + 10)

    val t3 = DBIO.seq(
      q2c.result.map(_ shouldBe r.map(t => Some(t._1)).map(_.map(_ + 42))),
      q3c.result.map(_ shouldBe r.map(t => t._3).map(_.map(_ + 10)))
    )

    // Use Option.map
    val q1d = q1.map(_.map(_.a))
    val q1d2 = q1.map(_.map(x => (x.a, x.b, x.c)))
    val q2d = q2.map { io: Rep[Option[Int]] =>
      io.map { i: Rep[Int] =>
        i + 1
      }
    }
    val q3d = q3.map(_.map(s => (s, s, 1)))
    val q4d = q4.map(_.filter(_.isDefined).map(_.getOrElse(0)))
    val q1dt: Query[Rep[Option[Int]], _, Seq] = q1d
    val q1d2t: Query[Rep[Option[(Rep[Int], Rep[String], Rep[Option[Int]])]], _, Seq] = q1d2
    val q2dt: Query[Rep[Option[Int]], Option[Int], Seq] = q2d
    val q3dt: Query[Rep[Option[(Rep[Int], Rep[Int], ConstColumn[Int])]], Option[(Int, Int, Int)], Seq] = q3d
    val q4dt: Query[Rep[Option[Int]], Option[Int], Seq] = q4d

    val t4 = DBIO.seq(
      q1d.result.map(_ shouldBe r.map(t => Some(t)).map(_.map(_._1))),
      q1d2.result.map(_ shouldBe r.map(t => Some(t)).map(_.map(x => (x._1, x._2, x._3)))),
      q2d.result.map(_ shouldBe r.map(t => Some(t._1)).map(_.map(_ + 1))),
      q3d.result.map(_ shouldBe r.map(t => t._3).map(_.map(s => (s, s, 1)))),
      q4d.result.map(_ shouldBe r.map(t => Some(t._3)).map(_.filter(_.isDefined).map(_.get)))
    )

    // Use Option.flatMap
    val q1e1 = q1.map { to => to.flatMap { t => Rep.Some(t.b) } }
    val q1e2 = q1.map { to => to.flatMap { t => t.c } }
    val q1e3 = q1.map(to => Rep.Some(to)).map(_.flatMap(identity))
    val q2e = q2.map { io => io.flatMap { i => Rep.Some(i) } }
    val q1e1t: Query[Rep[Option[String]], _, Seq] = q1e1
    val q1e2t: Query[Rep[Option[Int]], _, Seq] = q1e2
    val q2et: Query[Rep[Option[Int]], _, Seq] = q2e

    val t5 = DBIO.seq(
      q1e1.result.map(_ shouldBe r.map(t => Some(t)).map { to => to.flatMap { t => Some(t._2) } }),
      q1e2.result.map(_ shouldBe r.map(t => Some(t)).map { to => to.flatMap { t => t._3 } }),
      q1e3.result.map(_ shouldBe r.map(t => Some(t)).map(to => Some(to)).map(_.flatMap(identity))),
      q2e.result.map(_ shouldBe r.map(t => Some(t._1)).map { io => io.flatMap { i => Some(i) } })
    )

    // Use Option.flatten
    val q1f1 = q1.map { to => Rep.Some(to) }
    val q1f2 = q1.map { to => Rep.Some(to).flatten }
    val q1f3 = q1.map { to => Rep.Some(to) }.map(_.flatten)
    val q2f1 = q2.map { io => Rep.Some(io) }
    val q2f2 = q2.map { io => Rep.Some(io).flatten }
    val q2f3 = q2.map { io => Rep.Some(io) }.map(_.flatten)
    val q1f1t: Query[Rep[Option[Option[X]]], _, Seq] = q1f1
    val q1f2t: Query[Rep[Option[X]], _, Seq] = q1f2
    val q1f3t: Query[Rep[Option[X]], _, Seq] = q1f3
    val q2f1t: Query[Rep[Option[Option[Int]]], _, Seq] = q2f1
    val q2f2t: Query[Rep[Option[Int]], _, Seq] = q2f2
    val q2f3t: Query[Rep[Option[Int]], _, Seq] = q2f3

    val t6 = DBIO.seq(
      q1f1.result.map(_ shouldBe Vector(Some(Some((1, "1", Some(1)))), Some(Some((2, "2", Some(2)))), Some(Some(3, "3", None)))),
      q1f2.result.map(_ shouldBe r.map(t => Some(t)).map { to => Some(to).flatten }),
      q1f3.result.map(_ shouldBe r.map(t => Some(t)).map { to => Some(to) }.map(_.flatten)),
      q2f1.result.map(_ shouldBe r.map(t => Some(t._1)).map { io => Some(io) }),
      q2f2.result.map(_ shouldBe r.map(t => Some(t._1)).map { io => Some(io).flatten }),
      q2f3.result.map(_ shouldBe r.map(t => Some(t._1)).map { io => Some(io) }.map(_.flatten))
    )

    val drop = xs.schema.drop

    db.exec {
      setup >>
      t1 >> t2 >> t3 >> t4 >> t5 >> t6 >>
      drop
    }
  }
}
