package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._

/**
 * InsertFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class InsertFunSuite extends AbstractSlickFunSuite {

  test("simple") {

    class T(tag: Tag, tname: String) extends Table[(Int, String)](tag, tname) {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name)
    }

    lazy val src1 = TableQuery(new T(_, "src1_q"))
    lazy val dst1 = TableQuery(new T(_, "dst1_q"))
    lazy val dst2 = TableQuery(new T(_, "dst2_q"))
    lazy val dst3 = TableQuery(new T(_, "dst3_q"))

    lazy val q2 = for (s <- src1 if s.id <= 2) yield s
    LOG.debug("Insert 2:" + dst2.forceInsertStatementFor(q2))

    lazy val q3 = (42, "X".bind)
    LOG.debug("Insert 3:" + dst2.forceInsertStatementFor(q3))

    lazy val q4comp = Compiled {dst2.filter(_.id < 10)}
    lazy val dst3comp = Compiled {dst3}

    lazy val schema = src1.schema ++ dst1.schema ++ dst2.schema ++ dst3.schema

    db.seq(
      schema.drop.asTry,
      schema.create,
      src1 +=(1, "A"),
      src1.map(_.ins) ++= Seq((2, "B"), (3, "C")),
      dst1.forceInsertQuery(src1),
      dst1.to[Set].result.map(_ shouldEqual Set((1, "A"), (2, "B"), (3, "C"))),
      dst2.forceInsertQuery(q2),
      dst2.to[Set].result.map(_ shouldEqual Set((1, "A"), (2, "B"))),
      dst2.forceInsertExpr(q3),
      dst2.to[Set].result.map(_ shouldEqual Set((1, "A"), (2, "B"), (42, "X"))),
      dst3comp.forceInsertQuery(q4comp),
      dst3comp.result.map(v => v.to[Set] shouldEqual Set((1, "A"), (2, "B"))),
      schema.drop
    )
  }

  test("returning - 자동증가 id 값") {
    class A(tag: Tag) extends Table[(Int, String, String)](tag, "insert_a") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def s1 = column[String]("s1")
      def s2 = column[String]("s2")
      def * = (id, s1, s2)
      def ins = (s1, s2)
    }
    val as = TableQuery[A]

    def ins1 = as.map(_.ins) returning as.map(_.id)
    def ins2 = as.map(_.ins) returning as.map(a => (a.id, a.s1))
    def ins3 = as.map(_.ins) returning as.map(_.id) into ((v, i) => (i, v._1, v._2))
    def ins4 = as.map(_.ins) returning as.map(a => a) // map(identity)

    db.seq(
      as.schema.drop.asTry,
      as.schema.create,

      (ins1 +=("a", "b")) map { id1: Int => id1 shouldBe 1 },

      as.schema.drop
    )
  }

  test("force insert") {
    class T(tname: String)(tag: Tag) extends Table[(Int, String)](tag, tname) {
      def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name) // id 에도 값을 지정하기 위해 forceInsert 를 사용합니다.
    }
    lazy val ts = TableQuery(new T("ins_forced_t")(_))
    lazy val src = TableQuery(new T("ins_forced_src")(_))

    lazy val schema = ts.schema ++ src.schema

    db.seq(
      schema.drop.asTry,
      schema.create,
      ts +=(101, "A"),
      ts.map(_.ins) ++= Seq((102, "B"), (103, "C")),
      ts.filter(_.id > 100).length.result.map(_ shouldEqual 0),
      ifCap(jcap.forceInsert) {
        DBIO.seq(
          ts.forceInsert(104, "A"),
          ts.map(_.ins).forceInsertAll(Seq((105, "B"), (106, "C"))),
          ts.filter(_.id > 100).length.result.map(_ shouldEqual 3),
          ts.map(_.ins).forceInsertAll(Seq((111, "D"))),
          ts.filter(_.id > 100).length.result.map(_ shouldEqual 4),
          src.forceInsert(90, "X"),
          ts.forceInsertQuery(src).map(_ shouldEqual 1),
          ts.filter(_.id.between(90, 99)).map(_.name).result.map(_ shouldEqual Seq("X"))
        )
      },
      schema.drop
    )
  }

  test("insert or update plain") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "merge_t") {
      def id = column[Int]("id", O.PrimaryKey)
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name) // id 에도 값을 지정하기 위해 forceInsert 를 사용합니다.
    }
    lazy val ts = TableQuery[T]

    db.seq(
      ts.schema.drop.asTry,
      ts.schema.create,
      ts ++= Seq((1, "a"), (2, "b")),
      ts.insertOrUpdate((3, "c")).map(_ shouldEqual 1),
      ts.insertOrUpdate((1, "d")).map(_ shouldEqual 1),
      ts.sortBy(_.id).result.map(_ shouldEqual Seq((1, "d"), (2, "b"), (3, "c"))),
      ts.schema.drop
    )
  }

  test("insert or update with autoinc") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "merge_t2") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name) // id 에도 값을 지정하기 위해 forceInsert 를 사용합니다.
    }
    lazy val ts = TableQuery[T]

    db.seq(
      ts.schema.drop.asTry,
      ts.schema.create,
      ts ++= Seq((1, "a"), (2, "b")),
      ts.insertOrUpdate((0, "c")).map(_ shouldEqual 1), // 새로 값이 들어간다하더라도, 0 이 아니라 3이 들어간다.
      ts.insertOrUpdate((1, "d")).map(_ shouldEqual 1),
      ts.sortBy(_.id).result.map(_ shouldEqual Seq((1, "d"), (2, "b"), (3, "c"))),
      ifCap(jcap.returnInsertKey) {
        val q = ts returning ts.map(_.id)
        //        DBIO.seq(
        //          q.insertOrUpdate((0, "e")).map(_ shouldEqual Some(4)),
        //          q.insertOrUpdate((1, "f")).map(_ shouldEqual None),
        //          ts.sortBy(_.id).result.map(_ shouldEqual Seq((1, "f"), (2, "b"), (3, "c"), (4, "e")))
        //        )
        for {
          _ <- q.insertOrUpdate((0, "e")).map(_ shouldEqual Some(4))    // 새로 발급된 auto inc 값
          _ <- q.insertOrUpdate((1, "f")).map(_ shouldEqual None)       // 새로운 Id 값이 아니므로 None이 반환된다!!!
          _ <- ts.sortBy(_.id).result.map(_ shouldEqual Seq((1, "f"), (2, "b"), (3, "c"), (4, "e")))
        } yield ()
      },
      ts.schema.drop
    )
  }
}
