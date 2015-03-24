package debop4s.data.slick.examples

import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._
import debop4s.data.slick.{AbstractSlickFunSuite, SlickContext}

import scala.collection.mutable.ArrayBuffer
import scala.slick.util.CloseableIterator
import scala.util.Try

/**
 * InvokerFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class InvokerFunSuite extends AbstractSlickFunSuite {

  test("collections") {
    class T(tag: Tag) extends Table[Int](tag, "invoker_collections_t") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq(2, 3, 1, 5, 4)

      val q = ts.map(_.a).sorted

      q.list shouldEqual Seq(1, 2, 3, 4, 5)
      q.buildColl[Seq] shouldEqual Seq(1, 2, 3, 4, 5)
      q.buildColl[Set] shouldEqual Set(1, 2, 3, 4, 5)
      q.buildColl[IndexedSeq] shouldEqual IndexedSeq(1, 2, 3, 4, 5)
      q.buildColl[ArrayBuffer] shouldEqual ArrayBuffer(1, 2, 3, 4, 5)
      q.buildColl[Array].toList shouldEqual List(1, 2, 3, 4, 5)

      ts ++= (6 to 100).toSeq

      val it = q.iterator
      // it.use 는 Closer.using 과 같은 기능ㅇ르 수행합니다.
      val sum = it.use { it.reduceLeft(_ + _) }
      sum shouldEqual 5050
    }
  }

  test("to map") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "invoker_map_t") {
      def k = column[Int]("k")
      def v = column[String]("v")
      def * = (k, v)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create
      ts.insertAll(2 -> "b", 3 -> "c", 1 -> "a")
      ts.toMap shouldEqual Map(1 -> "a", 2 -> "b", 3 -> "c")
    }
  }

  test("lazy") {
    class T(tag: Tag) extends Table[Int](tag, "invoker_lazy_t") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T]

    // select x2."a" from "invoker_lazy_t" x2 order by x2."a"
    val q = ts.sortBy(_.a)

    def setUp(implicit session: Session): Unit = {
      Try { ts.ddl.drop }
      ts.ddl.create
      for (g <- 1 to 1000 grouped 100)
        ts.insertAll(g: _*)
    }

    def f() = CloseableIterator close SlickContext.defaultDB.createSession after { implicit session =>
      setUp
      q.iterator
    }

    def g() = CloseableIterator close SlickContext.defaultDB.createSession after { implicit session =>
      setUp
      sys.error("make sure it gets closed")
    }

    // q.iterator 를 이용하여 정보를 가져온다. toStream 은 원하는 만큼만 로딩하여 작업할 수 있다.
    val it = f()
    it.use {
      it.toStream.toList shouldEqual (1 to 1000).toList
    }

    // g() 메소드를 수행하면 예외가 발생하므로
    intercept[RuntimeException] { g() }

    withSession { implicit session =>
      val it2 = f()
      it2.use {
        it2.toStream.toList shouldEqual (1 to 1000).toList
      }
    }
  }
}
