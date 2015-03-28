package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * AggregateFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 23.
 */
class AggregateFunSuite extends AbstractSlickFunSuite {

  test("aggregates") {

    class T(tag: Tag) extends Table[(Int, Option[Int])](tag, "aggregate_t2") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b)
    }
    lazy val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq((1, Some(1)), (1, Some(2)), (1, Some(3)), (2, Some(3)))
    }

    def q1(i: Int) = for {t <- ts; if t.a === i.bind} yield t
    def q2(i: Int) = (q1(i).length, q1(i).map(_.a).sum, q1(i).map(_.b).sum, q1(i).map(_.b).avg)
    val q2_0 = q2(0).shaped
    val q2_1 = q2(1).shaped

    withReadOnly { implicit session =>
      LOG.debug(q2_0.run.toString)
      LOG.debug(q2_1.run.toString)

      q2_0.run shouldEqual(0, None, None, None)
      q2_1.run shouldEqual(3, Some(3), Some(6), Some(2))
    }
  }

  test("group by") {
    class T(tag: Tag) extends Table[(Int, Option[Int])](tag, "aggregate_t3") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b)
    }
    class U(tag: Tag) extends Table[Int](tag, "aggregate_u") {
      def id = column[Int]("id")
      def * = id
    }

    val ts = TableQuery[T]
    val us = TableQuery[U]

    lazy val tData = Seq(
                          (1, Some(1)), (1, Some(2)), (1, Some(3)),
                          (2, Some(1)), (2, Some(2)), (2, Some(5)),
                          (3, Some(1)), (3, Some(9))
                        )

    val ddl = ts.ddl ++ us.ddl
    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create
      ts ++= tData
    }

    withSession { implicit session =>

      // select * from (select count(b) as length from aggregate_t3 group by a) as t order by length
      val q0 = ts.groupBy(_.a)
      val q1 = q0.map(_._2.length).sortBy(identity)
      val r0 = q1.run

      r0 shouldEqual Vector(2, 3, 3)

      // select * from (select a, count(b), sum(a), sum(b) from aggregate_t3 group by a) order by a
      val q = (
                for {
                  (k, v) <- ts.groupBy(_.a)
                } yield {
                  (k, v.length, v.map(_.a).sum, v.map(_.b).sum)
                }
                ).sortBy(_._1)
      val r: Seq[(Int, Int, Option[Int], Option[Int])] = q.run
      LOG.debug(s"r=$r")
      r shouldEqual Seq((1, 3, Some(3), Some(6)),
                         (2, 3, Some(2 + 2 + 2), Some(1 + 2 + 5)),
                         (3, 2, Some(3 + 3), Some(1 + 9)))


      us ++= Seq(1, 2, 3)

      // select t.id, count(t.*), sum(t.a), sum(t.b)
      //   from ts inner join us on (ts.id = us.id)
      //  group by t.id
      val q2 = (
                 for {
                   u <- us
                   t <- ts if t.a === u.id
                 } yield (u, t)
                 ).groupBy(_._1.id)
               .map { case (id, q) => (id, q.length, q.map(_._2.a).sum, q.map(_._2.b).sum) }
      val r2 = q2.run
      LOG.debug(s"r2=$r2")
      r2 shouldEqual Seq((1, 3, Some(1 + 1 + 1), Some(1 + 2 + 3)),
                          (2, 3, Some(2 + 2 + 2), Some(1 + 2 + 5)),
                          (3, 2, Some(3 + 3), Some(1 + 9)))


      // select st.a, sum(st.b)
      // from (select (t.a + 10) as a, t.b as b from t3) st
      // groupBy st.a
      // order by st.a
      val q3 = (
                 for {
                   (x, q) <- ts.map(t => (t.a + 10, t.b)).groupBy(_._1)
                 } yield (x, q.map(_._2).sum)
                 ).sortBy(_._1)
      val r3 = q3.run
      LOG.debug(s"r3=$r3")
      r3 shouldEqual Seq((11, Some(1 + 2 + 3)), (12, Some(1 + 2 + 5)), (13, Some(1 + 9)))

      // select a, b, count(*) from t3
      // group by a, b
      // order by a, b
      val q4 = for {
        (x, q) <- ts.groupBy(t => (t.a, t.b))
      } yield (x, q.length)

      val r4 = q4.sortBy(_._1).run
      LOG.debug(s"r4=$r4")
      r4 shouldEqual Seq(
                          ((1, Some(1)), 1), ((1, Some(2)), 1), ((1, Some(3)), 1),
                          ((2, Some(1)), 1), ((2, Some(2)), 1), ((2, Some(5)), 1),
                          ((3, Some(1)), 1), ((3, Some(9)), 1)
                        )

      // select a, b
      // from (select a, b from t3 where a = 1 order by b)
      // group by a, b
      val q5 = ts
               .filter(_.a === 1.bind)
               .map(t => (t.a, t.b))
               .sortBy(_._2)
               .groupBy(x => (x._1, x._2))
               .map { case (a, _) => (a._1, a._2) }
      val r5 = q5.run.toSet
      r5 shouldEqual Set((1, Some(1)), (1, Some(2)), (1, Some(3)))


      us += 4

      // select x2.x3, count(1), count(x2.x3), count(x4.x5)
      // from (select x6."id" as x3 from "aggregate_u" x6) x2
      //  left outer join (select x7."a" as x5, x7."b" as x8 from "aggregate_t3" x7) x4 on x2.x3 = x4.x5
      // group by x2.x3
      val q6 = (
                 for {
                   (u, t) <- us leftJoin ts on ( _.id === _.a )
                 } yield (u, t)
                 ).groupBy(_._1.id)
               .map { case (id, q) => (id, q.length, q.map(_._1).length, q.map(_._2).length) }

      val r6 = q6.run.toSet
      r6 shouldEqual Set((1, 3, 3, 3), (2, 3, 3, 3), (3, 2, 2, 2), (4, 1, 1, 0))


      // select x2."a", sum(x2."b"), min(x2."b"), max(x2."b"), avg(x2."b") from "aggregate_t3" x2 group by x2."a"
      val q7 = ts.groupBy(_.a).map { case (a, ts) =>
        (a, ts.map(_.b).sum, ts.map(_.b).min, ts.map(_.b).max, ts.map(_.b).avg)
      }
      val r7 = q7.run.toSet

      r7 shouldEqual Set(
                          (1, Some(1 + 2 + 3), Some(1), Some(3), Some(2)),
                          (2, Some(1 + 2 + 5), Some(1), Some(5), Some(2)),
                          (3, Some(1 + 9), Some(1), Some(9), Some(5))
                        )

      // select max('test') from "aggregate_u" x2
      val q8 = us.map(_ => "test").groupBy(identity).map(_._2.max)
      q8.run shouldEqual Seq(Some("test"))

      // select 'x', max('x') from "aggregate_u" x2
      val q8b =
        for {(key, group) <- us.map(_ => "x").groupBy(identity)}
          yield (key, group.map(identity).max)
      q8b.run shouldEqual Seq(("x", Some("x")))

      // select 5, sum(5 + 5) from "aggregate_u" x2
      val q8c =
        for {(key, group) <- us.map(_ => 5).groupBy(identity)}
          yield (key, group.map(x => x + x).sum)
      q8c.run shouldEqual Seq((5, Some(( 5 + 5 ) * 4)))


      // select x2."a", x2."b" from "aggregate_t3" x2 group by x2."a", x2."b"
      val r9 = tData.toSet
      val q9 = ts.groupBy(identity).map(_._1)
      q9.run.toSet shouldEqual r9
      LOG.debug(s"r9=${ q9.run.toSet.mkString }")


      // 기본적으로 max, min, avg, sum 이 컬럼과 같은 수형인데, asColumnOf 를 이용하여 다른 수형으로 casting 한다.
      // select x2."a", max(cast(x2."b" as DOUBLE)) from "aggregate_t3" x2 group by x2."a"
      val q10 = ts.groupBy(_.a) map { case (id, data) =>
        (id, data.map(_.b.asColumnOf[Option[Double]]).max)
      }
      q10.run.toSet shouldEqual Set((1, Some(3.0)), (2, Some(5.0)), (3, Some(9.0)))


      case class Pair(a: Int, b: Option[Int])
      class T4(tag: Tag) extends Table[Pair](tag, "aggregate_t4") {
        def a = column[Int]("a")
        def b = column[Option[Int]]("b")
        def * = (a, b) <>(Pair.tupled, Pair.unapply)
      }
      val t4s = TableQuery[T4]
      Try { t4s.ddl.drop }
      t4s.ddl.create
      t4s ++= Seq(
                   Pair(1, Some(1)), Pair(1, Some(2)),
                   Pair(1, Some(1)), Pair(1, Some(2)),
                   Pair(1, Some(1)), Pair(1, Some(2))
                 )

      val expected1 = Set(Pair(1, Some(1)), Pair(1, Some(2)))

      val q12 = t4s
      val res12 = q12.run
      res12.size shouldEqual 6
      res12.toSet shouldEqual expected1

      val q13 = t4s.map(identity)
      val res13 = q13.run
      res13.size shouldEqual 6
      res13.toSet shouldEqual expected1

      val q11 = t4s.groupBy(identity).map(_._1)
      val res11 = q11.run
      res11.toSet shouldEqual expected1
      res11.size shouldEqual 2

    }
  }

  test("int length") {

    class A(tag: Tag) extends Table[Int](tag, "aggregate_int_length_a") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val as = TableQuery[A]

    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create
      as += 1
    }

    val q1 = as.groupBy(_.id) map {
      case (_, q) => (q.map(_.id).min.asColumnOf[Int], q.length)
    }

    withReadOnly { implicit session =>
      q1.run shouldEqual Seq((1, 1))
    }
  }

  test("group 3") {
    case class Tab(col1: String, col2: String, col3: String, col4: Int)

    class Tabs(tag: Tag) extends Table[Tab](tag, "aggregate_group_tab") {
      def col1 = column[String]("col1")
      def col2 = column[String]("col2")
      def col3 = column[String]("col3")
      def col4 = column[Int]("col4")
      def * = (col1, col2, col3, col4) <>(Tab.tupled, Tab.unapply)
    }
    lazy val Tabs = TableQuery[Tabs]
    withSession { implicit session =>
      Try { Tabs.ddl.drop }
      Tabs.ddl.create
      Tabs ++= Seq(
                    Tab("foo", "bar", "bat", 1),
                    Tab("foo", "bar", "bat", 2),
                    Tab("foo", "quux", "bat", 3),
                    Tab("baz", "quux", "bat", 4)
                  )

      withReadOnly { implicit session =>
        val q1 = Tabs.groupBy(t => (t.col1, t.col2, t.col3)).map {
          case (grp, t) => (grp._1, grp._2, t.map(_.col4).sum.asColumnOf[Option[Double]])
        }
        q1.run.toSet shouldEqual Set(("baz", "quux", Some(4.0)),
                                      ("foo", "quux", Some(3.0)),
                                      ("foo", "bar", Some(3.0)))
      }
    }
  }

  test("multi map aggregates") {
    class B(tag: Tag) extends Table[(Long, String, String)](tag, "aggregate_multimap_b") {
      def id = column[Long]("id", O.PrimaryKey)
      def b = column[String]("b")
      def d = column[String]("d")
      def * = (id, b, d)
    }
    lazy val bs = TableQuery[B]

    class A(tag: Tag) extends Table[(Long, String, Long, Long)](tag, "aggregate_multimap_a") {
      def id = column[Long]("id", O.PrimaryKey)
      def a = column[String]("a")
      def c = column[Long]("c")
      def fkId = column[Long]("fkId")
      def * = (id, a, c, fkId)
    }
    lazy val as = TableQuery[A]

    val ddl = as.ddl ++ bs.ddl
    withTransaction { implicit session =>
      Try { ddl.drop }
      ddl.create
    }


    withReadOnly { implicit session =>
      // select min(a.a) from a group by a.id
      val q1 = as.groupBy(_.id).map(_._2.map(identity).map(x => x.a).min)
      q1.run.toList.isEmpty shouldEqual true

      // select min(x2.x3), min(x2.x3), x2.x4, count(x5.x6)
      // from (select x7."id" as x6, x7."a" as x8, x7."c" as x9, x7."fkId" as x10 from "aggregate_multimap_a" x7) x5
      //   left outer join (select x11."id" as x4, x11."b" as x3, x11."d" as x12 from "aggregate_multimap_b" x11) x2 on x5.x6 = x2.x4
      // group by x2.x4
      val q2 =
        ( as leftJoin bs on ( _.id === _.id ) )
        .map { case (c, s) =>
          (c, s, s.b)
        }.groupBy { prop =>
          val c = prop._1
          val s = prop._2
          val name = prop._3
          s.id // bs.id
        }.map { prop =>
          val supId = prop._1
          val c = prop._2.map(x => x._1)
          val s = prop._2.map(x => x._2)
          val name = prop._2.map(x => x._3)
          (name.min, s.map(_.b).min, supId, c.length)
        }
      q2.run.isEmpty shouldEqual true

      // select x2."a", x3."b", max(x2."c")
      // from "aggregate_multimap_a" x2, "aggregate_multimap_b" x3
      // where (x2."fkId" = x3."id") and (x3."d" = ?)
      // group by x2."a", x3."b"
      val q4 =
        as.flatMap { t1 =>
          bs.withFilter { t2 => t1.fkId === t2.id && t2.d === "".bind }.map(t2 => (t1, t2))
        }.groupBy { prop =>
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

      q4.run.isEmpty shouldEqual true
    }

  }
}
