package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.slick.ast.NumericTypedType
import scala.util.Try

/**
 * RelationalTypeFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 27.
 */
class RelationalTypeFunSuite extends AbstractSlickFunSuite {

  test("numeric") {
    withSession { implicit session =>
      def testStore[T](values: T*)(implicit tm: BaseColumnType[T] with NumericTypedType): Unit = {
        class A(tag: Tag) extends Table[(Int, T)](tag, "type_numeric_a") {
          def id = column[Int]("id")
          def data = column[T]("data")
          def * = (id, data)
        }
        val as = TableQuery[A]
        Try { as.ddl.drop }
        as.ddl.create

        val data = values.zipWithIndex.map { case (d, i) => (i + 1, d) }
        as ++= data

        val q = as.sortBy(_.id)
        q.run shouldEqual data

        as.ddl.drop
      }

      testStore[Int](-1, 0, 1, Int.MinValue, Int.MaxValue)
      ifCap(rcap.typeLong) {
        testStore[Long](-1L, 0L, 1L, Long.MinValue, Long.MaxValue)
      }
      testStore[Short](-1, 0, 1, Short.MinValue, Short.MaxValue)
      testStore[Byte](-1, 0, 1, Byte.MinValue, Byte.MaxValue)
      testStore[Double](-1.0, 0, 1.0)
      testStore[Float](-1.0f, 0f, 1.0f)
      ifCap(rcap.typeBigDecimal) {
        testStore[BigDecimal](BigDecimal(-1), BigDecimal(0), BigDecimal(1), BigDecimal(Long.MinValue), BigDecimal(Long.MaxValue))
      }
    }
  }

  private def roundtrip[T: BaseColumnType](tablename: String, v: T): Unit = {
    class A(tag: Tag) extends Table[(Int, T)](tag, tablename) {
      def id = column[Int]("id")
      def data = column[T]("data")
      def * = (id, data)
    }
    val as = TableQuery[A]

    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create

      as +=(1, v)
      as.map(_.data).run.head shouldEqual v
      as.filter(_.data === v).map(_.id).run.headOption shouldEqual Some(1)
      as.filter(_.data =!= v).map(_.id).run.headOption shouldEqual None
      as.filter(_.data === v.bind).map(_.id).run.headOption shouldEqual Some(1)
      as.filter(_.data =!= v.bind).map(_.id).run.headOption shouldEqual None

      as.ddl.drop
    }
  }

  test("boolean roundtrip") {
    roundtrip[Boolean]("boolean_true", true)
    roundtrip[Boolean]("boolean_false", false)
    roundtrip[String]("string_aaa", "aaa")
  }

  test("unit test") {
    class T(tag: Tag) extends Table[Int](tag, "rt_unit_t") {
      def id = column[Int]("id")
      def * = id
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts += 42

      ts.map(_ => ()).run shouldEqual Seq(())

      ts.map(a => ((), a)).run shouldEqual Seq(((), 42))
      ts.map(a => (a, ())).run shouldEqual Seq((42, ()))

      ts.map(a => ((), a)).first shouldEqual((), 42)
      ts.map(a => (a, ())).first shouldEqual(42, ())
    }
  }
}
