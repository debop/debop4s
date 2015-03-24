package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * InsertFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class InsertFunSuite extends AbstractSlickFunSuite {

  test("simple insert") {
    class TestTable(tag: Tag, tableName: String) extends Table[(Int, String)](tag, tableName) {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)

      def ins = (id, name)
    }

    val src1 = TableQuery(new TestTable(_, "insert_src1_q"))
    val dst1 = TableQuery(new TestTable(_, "insert_dst1_q"))
    val dst2 = TableQuery(new TestTable(_, "insert_dst2_q"))

    val ddl = src1.ddl ++ dst1.ddl ++ dst2.ddl

    withSession { implicit session =>
      Try {ddl.drop}
      ddl.create

      src1.insert(1, "A")
      src1.map(_.ins).insertAll((2, "B"), (3, "C"))

      // insert into dst1 select * from src1
      dst1.insert(src1)
      dst1.list.toSet shouldEqual Set((1, "A"), (2, "B"), (3, "C"))

      // insert into dst2 select src1
      // 이런기능이 ORM 보다 좋은 거지요^^
      val q2 = for (s <- src1 if s.id <= 2) yield s
      println(s"Insert 2: ${dst2.insertStatementFor(q2)}")
      dst2.insert(q2)
      dst2.list.toSet shouldEqual Set((1, "A"), (2, "B"))

      // insert into dst2 select dummy
      val q3 = (42, "X".bind)
      println(s"Insert 3: ${dst2.insertStatementFor(q3)}")
      dst2.insertExpr(q3)
      dst2.list.toSet shouldEqual Set((1, "A"), (2, "B"), (42, "X"))
    }
  }

  test("returning") {
    class A(tag: Tag) extends Table[(Int, String, String)](tag, "insert_return_a") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def s1 = column[String]("s1")
      def s2 = column[String]("s2")
      def * = (id, s1, s2)
    }
    lazy val as = TableQuery[A]

    def ins1 = as.map(a => (a.s1, a.s2)) returning as.map(_.id)
    def ins2 = as.map(a => (a.s1, a.s2)) returning as.map(a => (a.id, a.s1))
    def ins3 = as.map(a => (a.s1, a.s2)) returning as.map(_.id) into ((v, i) => (i, v._1, v._2))
    def ins4 = as.map(a => (a.s1, a.s2)) returning as.map(identity)

    withSession { implicit session =>
      Try {as.ddl.drop}
      as.ddl.create

      ifCap(jcap.returnInsertKey) {
        val id1 = ins1.insert("a", "b")
        id1 shouldEqual 1

        ifCap(jcap.returnInsertOther) {
          val id2: (Int, String) = ins2.insert("c", "d")
          id2 shouldEqual(2, "c")
        }
        ifNotCap(jcap.returnInsertOther) {
          val id2: Int = ins1.insert("c", "d")
          id2 shouldEqual 2
        }

        val ids3 = ins1.insertAll(("e", "f"), ("g", "h"))
        ids3 shouldEqual Seq(3, 4)

        val id4 = ins3.insert("i", "j")
        id4 shouldEqual(5, "i", "j")

        ifCap(jcap.returnInsertOther) {
          val id5 = ins4.insert("k", "l")
          id5 shouldEqual(6, "k", "l")
        }
      }
    }
  }

  // Auto Increment 값을 사용자가 지정하려면 force insert 를 수행할 수 있어야 합니다.
  test("forced insert") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "insert_forced_t") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def name = column[String]("name")

      def * = (id, name)
      def ins = (id, name)
    }
    lazy val ts = TableQuery[T]

    withSession { implicit session =>
      Try {ts.ddl.drop}
      ts.ddl.create
    }

    withReadOnly { implicit session =>
      ts.insert(101, "A")
      ts.map(_.ins).insertAll((102, "B"), (103, "C"))
      ts.filter(_.id > 100).length.run shouldEqual 0

      ifCap(jcap.forceInsert) {
        // insert into "insert_forced_t" ("id","name")  values (?,?)
        ts.forceInsert(104, "A")
        ts.map(_.ins).forceInsertAll((105, "B"), (106, "C"))
        ts.filter(_.id > 100).length.run shouldEqual 3
      }
    }
  }
}
