package debop4s.data.slick3.tests

import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.core.concurrent._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * AggregateFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class AggregateFunSuite extends AbstractSlickFunSuite {

  test("aggregates") {
    class T(tag: Tag) extends Table[(Int, Option[Int])](tag, "aggregate_t2") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T]
    def q1(i: Int) = for {t <- ts if t.a === i} yield t
    def q2(i: Int) = (q1(i).length, q1(i).map(_.a).sum, q1(i).map(_.b).sum, q1(i).map(_.b).avg)
    val q2_0 = q2(0).shaped
    val q2_1 = q2(1).shaped

    ts.schema.create >>
    (ts ++= Seq((1, Some(1)), (1, Some(2)), (1, Some(3)))) >>
    q2_0.result.map(_ shouldEqual(0, None, None, None)) >>
    q2_1.result.map(_ shouldEqual(3, Some(3), Some(6), Some(2))) >>
    ts.schema.drop
  }

  test("group by") {
    class T(tag: Tag) extends Table[(Int, Option[Int])](tag, "aggregate_t3") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b)
    }
    lazy val ts = TableQuery[T]
    class U(tag: Tag) extends Table[Int](tag, "aggregate_u3") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val us = TableQuery[U]

    db.exec {
      ts.schema.create >>
      (ts ++= Seq(
        (1, Some(1)), (1, Some(2)), (1, Some(3)),
        (2, Some(1)), (2, Some(2)), (2, Some(5)),
        (3, Some(1)), (3, Some(9))
      ))
    }
    val q0 = ts.groupBy(_.a)
    val q1 = q0.map(_._2.length).sortBy(identity)
    db.result(q1) shouldEqual Vector(2, 3, 3)

    val q = (for {
      (k, v) <- ts.groupBy(_.a)
    } yield (k, v.length, v.map(_.a).sum, v.map(_.b).sum)).sortBy(_._1)
    db.result(q) shouldEqual Seq((1, 3, Some(3), Some(6)), (2, 3, Some(6), Some(8)), (3, 2, Some(6), Some(10)))

    db.exec {
      us.schema.create >> (us ++= Seq(1, 2, 3))
    }
    val q2 = (for {
      u <- us
      t <- ts if t.a === u.id
    } yield (u, t))
             .groupBy(_._1.id)
             .map {
               case (id, q) => (id, q.length, q.map(_._2.a).sum, q.map(_._2.b).sum)
             }
    db.result(q2).toSet shouldEqual Set((1, 3, Some(3), Some(6)), (2, 3, Some(6), Some(8)), (3, 2, Some(6), Some(10)))

    /*
      ┇ select x2.x3, x2.x4
      ┇ from (
      ┇   select x5.x6 as x3, sum(x5.x7) as x4
      ┇   from (
      ┇     select x8."a" + 10 as x6, x8."b" as x7
      ┇     from "aggregate_t3" x8
      ┇   ) x5
      ┇   group by x5.x6
      ┇ ) x2
      ┇ order by x2.x3
     */
    val q3 = (for {
      (x, q) <- ts.map(t => (t.a + 10, t.b)).groupBy(_._1)
    } yield (x, q.map(_._2).sum)).sortBy(_._1)
    db.result(q3) shouldEqual Seq((11, Some(6)), (12, Some(8)), (13, Some(10)))

    /*
    ┇ select x2.x3, x2.x4, x2.x5
    ┇ from (
    ┇   select x6."a" as x3, x6."b" as x4, count(1) as x5
    ┇   from "aggregate_t3" x6
    ┇   group by x6."a", x6."b"
    ┇ ) x2
    ┇ order by x2.x3, x2.x4
     */
    val q4 = (for {
      (x, q) <- ts.groupBy(t => (t.a, t.b))
    } yield (x, q.length)).sortBy(_._1)

    db.result(q4) shouldEqual Seq(
      ((1, Some(1)), 1), ((1, Some(2)), 1), ((1, Some(3)), 1),
      ((2, Some(1)), 1), ((2, Some(2)), 1), ((2, Some(5)), 1),
      ((3, Some(1)), 1), ((3, Some(9)), 1)
    )
    /*
    ┇ select x2.x3, x2.x4
    ┇ from (
    ┇   select x5.x6 as x3, x5.x7 as x4
    ┇   from (
    ┇     select x8."a" as x6, x8."b" as x7
    ┇     from "aggregate_t3" x8
    ┇     where x8."a" = 1
    ┇     order by x8."b"
    ┇   ) x5
    ┇   group by x5.x6, x5.x7
    ┇ ) x2
     */
    val q5 = ts.filter(_.a === 1).map(t => (t.a, t.b)).sortBy(_._2).groupBy(x => (x._1, x._2)).map { case (a, _) => (a._1, a._2) }.to[Set]
    db.exec(q5.result) shouldEqual Set((1, Some(1)), (1, Some(2)), (1, Some(3)))

    db.exec(us += 4)

    val q6 =
      (for {
        (u, t) <- us joinLeft ts on (_.id === _.a)
      } yield (u, t))
      .groupBy(_._1.id)
      .map {
        case (id, q) => (id, q.length, q.map(_._1).length, q.map(_._2).length)
      }
    db.exec(q6.to[Set].result) shouldEqual Set((1, 3, 3, 3), (2, 3, 3, 3), (3, 2, 2, 2), (4, 1, 1, 0))

    val q7 = ts.groupBy(_.a).map { case (a, ts) =>
      (a, ts.map(_.b).sum, ts.map(_.b).min, ts.map(_.b).max, ts.map(_.b).avg)
    }
    db.result(q7).to[Set] shouldEqual Set(
      (1, Some(6), Some(1), Some(3), Some(2)),
      (2, Some(8), Some(1), Some(5), Some(2)),
      (3, Some(10), Some(1), Some(9), Some(5))
    )

    val q8 = us.map(_ => "test").groupBy(x => x).map(_._2.max)
    val q8b = for {(key, group) <- us.map(_ => "x").groupBy(co => co)} yield (key, group.map(identity).max)
    val q8c = for {(key, group) <- us.map(_ => 5).groupBy(identity)} yield (key, group.map(co => co + co).sum)
    db.exec {
      for {
        _ <- q8.result.map(_ shouldEqual Seq(Some("test")))
        _ <- q8b.result.map(_ shouldEqual Seq(("x", Some("x"))))
        _ <- q8c.result.map(_ shouldEqual Seq((5, Some(4 * 10))))
      } yield ()
    }

    val res9 = Set(
      (1, Some(1)), (1, Some(2)), (1, Some(3)),
      (2, Some(1)), (2, Some(2)), (2, Some(5)),
      (3, Some(1)), (3, Some(9))
    )

    val q9 = ts.groupBy(x => x).map(_._1).to[Set]
    val q9b = ts.map(x => x).groupBy(_.*).map(_._1).to[Set]
    val q9c = ts.map(x => x).groupBy(x => x).map(_._1).to[Set]

    db.exec(q9.result) shouldEqual res9
    db.exec(q9b.result) shouldEqual res9
    db.exec(q9c.result) shouldEqual res9

    val q10 =
      (for {m <- ts} yield m)
      .groupBy(_.a)
      .map { case (id, data) => (id, data.map(_.b.asColumnOf[Option[Double]]).max) } // cast double
      .to[Set]
    db.exec(q10.result) shouldEqual Set((1, Some(3.0)), (2, Some(5.0)), (3, Some(9.0)))


    case class Pair(a: Int, b: Option[Int])
    class T4(tag: Tag) extends Table[Pair](tag, "group_t4") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b) <>(Pair.tupled, Pair.unapply)
    }
    val t4s = TableQuery[T4]

    db.exec {
      t4s.schema.create >>
      (t4s ++= Seq(Pair(1, Some(1)), Pair(1, Some(2)))) >>
      (t4s ++= Seq(Pair(1, Some(1)), Pair(1, Some(2)))) >>
      (t4s ++= Seq(Pair(1, Some(1)), Pair(1, Some(2))))
    }
    val expected11 = Set(Pair(1, Some(1)), Pair(1, Some(2)))
    val q12 = t4s
    val q13 = t4s.map(identity)
    val q11 = t4s.groupBy(identity).map(_._1)

    db.result(q12).size shouldEqual 6
    val res13 = db.result(q13)
    res13.size shouldEqual 6
    res13.toSet shouldEqual expected11
    val res11 = db.result(q11)
    res11.size shouldEqual 2
    res11.toSet shouldEqual expected11

    db.exec((ts.schema ++ us.schema ++ t4s.schema).drop)
  }

  test("Int Length") {
    class A(tag: Tag) extends Table[Int](tag, "aggregate_int_length_a") {
      def id = column[Int]("ID")
      def * = id
    }
    lazy val as = TableQuery[A]
    db.exec {
      as.schema.create >>
      (as += 1)
    }
    val q1 = as.groupBy(_.id).map { case (_, q) => (q.map(_.id).min, q.length) }
    db.result(q1) shouldEqual Seq((Some(1), 1))

    db.exec {as.schema.drop}
  }

  test("group 3") {
    case class Tab(c1: String, c2: String, c3: String, c4: Int, c5: Int)
    class Tabs(tag: Tag) extends Table[Tab](tag, "aggregate_tab3") {
      def c1 = column[String]("c1")
      def c2 = column[String]("c2")
      def c3 = column[String]("c3")
      def c4 = column[Int]("c4")
      def c5 = column[Int]("c5")

      def * = (c1, c2, c3, c4, c5) <>(Tab.tupled, Tab.unapply)
    }
    lazy val tabs = TableQuery[Tabs]

    db.exec {
      tabs.schema.create >>
      (tabs ++= Seq(
        Tab("foo", "bar", "bat", 1, 5),
        Tab("foo", "bar", "bat", 2, 6),
        Tab("foo", "quux", "bat", 3, 7),
        Tab("baz", "quux", "bat", 4, 8)
      ))
    }
    val q1 = tabs
             .groupBy(t => (t.c1, t.c2, t.c3))
             .map { case (grp, t) => (grp._1, grp._2, t.map(_.c4).sum) }
             .to[Set]

    db.exec(q1.result) shouldEqual Set(
      ("baz", "quux", Some(4)),
      ("foo", "quux", Some(3)),
      ("foo", "bar", Some(3)))

    val q2 = tabs
             .groupBy(t => ((t.c1, t.c2), t.c3))
             .map { case (grp, t) => (grp._1._1, grp._1._2, t.map(_.c4).sum) }
             .to[Set]
    db.exec(q2.result) shouldEqual Set(
      ("baz", "quux", Some(4)),
      ("foo", "quux", Some(3)),
      ("foo", "bar", Some(3)))

    val q3 = tabs.groupBy(_.c1).map { case (grp, t) => (grp, t.map(x => x.c4 + x.c5).sum) }.to[Set]
    db.exec(q3.result) shouldEqual Set(("baz", Some(12)), ("foo", Some(24)))

    db.exec {tabs.schema.drop}
  }

  test("multi map aggregates") {
    class B(tag: Tag) extends Table[(Long, String, String)](tag, "agg_multimap_b") {
      def id = column[Long]("id", O.PrimaryKey)
      def b = column[String]("b")
      def d = column[String]("d")
      def * = (id, b, d)
    }
    class A(tag: Tag) extends Table[(Long, String, Long, Long)](tag, "agg_multimap_a") {
      def id = column[Long]("id", O.PrimaryKey)
      def a = column[String]("a")
      def c = column[Long]("c")
      def fkId = column[Long]("fkId")
      def * = (id, a, c, fkId)
    }
    lazy val bs = TableQuery[B]
    lazy val as = TableQuery[A]

    val schema = bs.schema ++ as.schema

    db.exec {schema.create}

    /*
    ┇ select min(x2."a")
    ┇ from "agg_multimap_a" x2
    ┇ group by x2."id"
     */
    val q1 = as.groupBy(_.id).map { case (g, a) => a.map(identity).map(_.a).min }
    db.result(q1) shouldEqual Nil

    /*
    ┇ select min(x2.x3), min((case when (x2.x4 = 1) then x2.x5 else null end)), (case when (x2.x4 = 1) then x2.x6 else null end), count(x2.x7)
    ┇ from (
    ┇   select x8.x9 as x7, x8.x10 as x11, x8.x12 as x13, x8.x14 as x15, x16.x17 as x4, x16.x18 as x6, x16.x19 as x5, x16.x20 as x21, (case when (x16.x17 = 1) then x16.x19 else null end) as x3
    ┇   from (
    ┇     select x22."id" as x9, x22."a" as x10, x22."c" as x12, x22."fkId" as x14
    ┇     from "agg_multimap_a" x22
    ┇   ) x8
    ┇   left outer join (
    ┇     select 1 as x17, x23."id" as x18, x23."b" as x19, x23."d" as x20
    ┇     from "agg_multimap_b" x23
    ┇   ) x16
    ┇   on x8.x9 = x16.x18
    ┇ ) x2
    ┇ group by (case when (x2.x4 = 1) then x2.x6 else null end)
     */
    val q2 =
      (as joinLeft bs on (_.id === _.id))
      .map { case (c, so) =>
        val nameo = so.map(_.b)
        (c, so, nameo)
      }.groupBy { prop => prop._2.map(_.id) }
      .map { case (grp, prop) =>
        val supId = grp
        val c = prop.map(x => x._1)
        val s = prop.map(x => x._2)
        val name = prop.map(x => x._3)
        (name.min, s.map(_.map(_.b)).min, supId, c.length)
      }
    db.result(q2) shouldEqual Nil

    /*
    ┇ select x2."a", x3."b", max(x2."c")
    ┇ from "agg_multimap_a" x2, "agg_multimap_b" x3
    ┇ where (x2."fkId" = x3."id") and (x3."d" = '')
    ┇ group by x2."a", x3."b"
     */
    val q4 =
      as
      .flatMap { t1 => bs.filter { t2 => t1.fkId === t2.id && t2.d === "" } map { t2 => (t1, t2) } }
      .groupBy { prop =>
        val t1 = prop._1
        val t2 = prop._2
        (t1.a, t2.b)
      }.map { prop =>
        val a = prop._1._1
        val b = prop._1._2
        val t1 = prop._2.map(_._1)
        val t2 = prop._2.map(_._2)
        val c3 = t1.map(_.c).max
        (a, b, c3)
      }

    db.result(q4) shouldEqual Nil

    db.exec {schema.drop}
  }
}
