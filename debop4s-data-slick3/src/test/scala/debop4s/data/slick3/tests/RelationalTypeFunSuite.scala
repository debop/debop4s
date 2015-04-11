package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}
import slick.ast.NumericTypedType
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * RelationalTypeFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RelationalTypeFunSuite extends AbstractSlickFunSuite {

  test("numeric") {
    def store[T](values: T*)(implicit tm: BaseColumnType[T] with NumericTypedType) = {
      class Tbl(tag: Tag) extends Table[(Int, T)](tag, "numeric_t") {
        def id = column[Int]("id")
        def data = column[T]("data")
        def * = (id, data)
      }
      lazy val tbl = TableQuery[Tbl]
      val data = values.zipWithIndex.map { case (d, i) => (i + 1, d) }
      val q = tbl.sortBy(_.id)

      DBIO.seq(
        tbl.schema.drop.asTry,
        tbl.schema.create,
        tbl ++= data,
        q.result.map(_ shouldEqual data),
        tbl.schema.drop
      )
    }

    db.seq(
      store[Int](-1, 0, 1, Int.MinValue, Int.MaxValue),
      ifCap(rcap.typeLong) { store[Long](-1L, 0L, 1L, Long.MinValue, Long.MaxValue) },
      store[Short](-1, 0, 1, Short.MinValue, Short.MaxValue),
      store[Byte](-1, 0, 1, Byte.MinValue, Byte.MaxValue),
      store[Double](-1.0, 0.0, 1.0),
      store[Float](-1.0f, 0.0f, 1.0f),
      ifCap(rcap.typeBigDecimal) {
        store[BigDecimal](BigDecimal(-1), BigDecimal(0), BigDecimal(1), BigDecimal(Long.MinValue), BigDecimal(Long.MaxValue))
      }
    )
  }

  private def roundtrip[T: BaseColumnType](tn: String, v: T) = {
    class A(tag: Tag) extends Table[(Int, T)](tag, tn) {
      def id = column[Int]("id")
      def data = column[T]("data")
      def * = (id, data)
    }
    lazy val as = TableQuery[A]

    db.seq(
      as.schema.drop.asTry,
      as.schema.create,
      as +=(1, v),
      as.map(_.data).result.map(_ shouldEqual Seq(v)),
      as.filter(_.data === v).map(_.id).result.map(_ shouldEqual Seq(1)),
      as.filter(_.data =!= v).map(_.id).result.map(_ shouldEqual Nil),
      as.filter(_.data === v.bind).map(_.id).result.map(_ shouldEqual Seq(1)),
      as.filter(_.data =!= v.bind).map(_.id).result.map(_ shouldEqual Nil),
      as.schema.drop
    )
  }

  test("boolean") {
    roundtrip[Boolean]("boolean_true", true)
    roundtrip[Boolean]("boolean_false", false)
  }
  test("string") {
    roundtrip[String]("roundtrip_string", "aaa")
  }

  test("unit test") {
    class T(tag: Tag) extends Table[Int](tag, "unit_t") {
      def id = column[Int]("id")
      def * = id
    }
    val ts = TableQuery[T]

    db.seq(
      ts.schema.drop.asTry,
      ts.schema.create,
      ts += 42,
      ts.map(_ => ()).result.map(_ shouldEqual Seq(())),
      ts.map(a => ((), a)).result.map(_ shouldEqual Seq(((), 42))),
      ts.map(a => (a, ())).result.map(_ shouldEqual Seq((42, ()))),
      ts.schema.drop
    )
  }
}
