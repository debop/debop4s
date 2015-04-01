package debop4s.data.slick3.tests

import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

import debop4s.core.concurrent._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * JdbcTypeFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcTypeFunSuite extends AbstractSlickFunSuite {

  test("byte array") {
    class T(tag: Tag) extends Table[(Int, Array[Byte])](tag, "jdbctype_ba") {
      def id = column[Int]("id")
      def data = column[Array[Byte]]("data")
      def * = (id, data)
    }
    lazy val ts = TableQuery[T]

    db.exec {
      ts.schema.create >>
      (ts +=(1, Array[Byte](1, 2, 3))) >>
      (ts +=(2, Array[Byte](4, 5)))
    }
    db.exec(ts.result.map(_.map { case (id, data) => (id, data.mkString) }.toSet)) shouldEqual Set((1, "123"), (2, "45"))
    db.exec(ts.result).map { case (id, data) => (id, data.mkString) }.toSet shouldBe Set((1, "123"), (2, "45"))

    if (implicitly[ColumnType[Array[Byte]]].hasLiteralForm) {
      db.exec(ts.filter(_.data === Array[Byte](4, 5)).map(_.data).to[Set].result).map(_.mkString) shouldEqual Set("45")
    }

    db.exec { ts.schema.drop }
  }

  test("byte array option") {
    class T(tag: Tag) extends Table[(Int, Option[Array[Byte]])](tag, "jdbctype_ba_opt") {
      def id = column[Int]("id")
      def data = column[Option[Array[Byte]]]("data")
      def * = (id, data)
    }
    lazy val ts = TableQuery[T]

    db.exec {
      ts.schema.create >>
      (ts +=(1, Some(Array[Byte](6, 7)))) >>
      ifCap(rcap.setByteArrayNull) { ts +=(2, None) } >>
      ifNotCap(rcap.setByteArrayNull) { ts.map(_.id) += 2 } >>
      ts.result.map(_.map { case (id, data) => (id, data.map(_.mkString).getOrElse("")) }.toSet).map(_ shouldEqual Set((1, "67"), (2, ""))) >>
      ts.schema.drop
    }
  }

  /**
   * Blob 추가
   */
  test("blob") {
    ifCapF(rcap.typeBlob) {
      class T(tag: Tag) extends Table[(Int, Blob)](tag, "jdbctype_blob") {
        def id = column[Int]("id")
        def data = column[Blob]("data")
        def * = (id, data)
      }
      lazy val ts = TableQuery[T]

      val a1 = (
                 ts.schema.create >>
                 (ts +=(1, new SerialBlob(Array[Byte](1, 2, 3)))) >>
                 (ts +=(2, new SerialBlob(Array[Byte](4, 5)))) >>
                 ts.result
                 ).transactionally

      val p1 = db.stream(a1).mapResult { case (id, data) => (id, data.getBytes(1, data.length.toInt).mkString) }
      val f1 = p1.materialize.map(_.toSet shouldEqual Set((1, "123"), (2, "45"))) flatMap { _ =>
        val f = db.stream(ts.result.transactionally, bufferNext = false)
                .materializeAsync[(Int, String)](
                { case (id, data) => db.io((id, data.getBytes(1, data.length.toInt).mkString)) })

        f.map(_.toSet shouldEqual Set((1, "123"), (2, "45")))
      }

      f1.await
      db.exec(ts.schema.drop)
      Future {}
    }
  }
}
