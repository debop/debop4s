package debop4s.data.slick3.tests

import java.sql.{Blob, Date, Time, Timestamp}
import java.util.UUID
import javax.sql.rowset.serial.SerialBlob

import debop4s.core.concurrent._
import debop4s.core.io.Serializers
import debop4s.data.slick3._
import debop4s.data.slick3.TestDatabase.driver.api._

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
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts +=(1, Array[Byte](1, 2, 3))) >>
      (ts +=(2, Array[Byte](4, 5)))
    }
    db.exec(ts.result.map(_.map { case (id, data) => (id, data.mkString) }.toSet)) shouldEqual Set((1, "123"), (2, "45"))
    ts.exec.map { case (id, data) => (id, data.mkString) }.toSet shouldBe Set((1, "123"), (2, "45"))

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

    if (implicitly[ColumnType[Array[Byte]]].hasLiteralForm) {
      ts.filter(_.data === Array[Byte](4, 5))
      .map(_.data)
      .to[Set]
      .exec
      .map(_.mkString) shouldEqual Set("45")
    }

    ts.schema.drop.exec
  }

  test("byte array option") {
    class T(tag: Tag) extends Table[(Int, Option[Array[Byte]])](tag, "jdbctype_ba_opt") {
      def id = column[Int]("id")
      def data = column[Option[Array[Byte]]]("data")
      def * = (id, data)
    }
    lazy val ts = TableQuery[T]

    db.exec {
      ts.schema.drop.asTry >>
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
    if (SlickContext.isPostgres) {
      cancel("Postgres 에서는 Blob 수형을 지원하지 않습니다.")
    }

    ifNotCapF(rcap.typeBlob) {
      cancel("Blob 수형을 지원하지 않는 Driver 입니다.")
      Future {}
    }

    class T(tag: Tag) extends Table[(Int, Blob)](tag, "jdbctype_blob") {
      def id = column[Int]("id")
      def data = column[Blob]("data")
      def * = (id, data)
    }
    lazy val ts = TableQuery[T]

    val a1 = (
             ts.schema.drop.asTry >>
             ts.schema.create >>
             (ts +=(1, new SerialBlob(Array[Byte](1, 2, 3)))) >>
             (ts +=(2, new SerialBlob(Array[Byte](4, 5)))) >>
             ts.result
             ).transactionally

    val p1 = a1.stream.mapResult { case (id, data) => (id, data.getBytes(1, data.length.toInt).mkString) }

    val f1 = p1.materialize.map(_.toSet shouldEqual Set((1, "123"), (2, "45"))) flatMap { _ =>
      val f = db.stream(ts.result.transactionally, bufferNext = false)
              .materializeAsync[(Int, String)](
              { case (id, data) => db.io((id, data.getBytes(1, data.length.toInt).mkString)) })

      f.map(_.toSet shouldEqual Set((1, "123"), (2, "45")))
    }
    f1.await

    ts.schema.drop.exec
  }

  test("mapped blob") {
    if (SlickContext.isPostgres) {
      cancel("Postgres 에서는 Blob 수형을 지원하지 않습니다.")
    }

    case class Serialized[T](value: T)
    implicit def serializedType[T] = MappedColumnType.base[Serialized[T], Blob](
    { serialized =>
      val ba = Serializers.serializeObject(serialized.value)
      new SerialBlob(ba)
    }, { blob =>
      val obj = Serializers.deserializeObject(blob.getBytes(1L, blob.length.toInt), classOf[Any]).asInstanceOf[T]
      Serialized[T](obj)
    })

    class T(tag: Tag) extends Table[(Int, Serialized[List[Int]])](tag, "mapped_blob_t") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[Serialized[List[Int]]]("b")
      def * = (id, b)
    }
    lazy val ts = TableQuery[T]

    db.exec {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts.map(_.b) ++= Seq(Serialized(List(1, 2, 3)), Serialized(List(4, 5)))) >>
      ts.to[Set].result.map(_ shouldEqual Set((1, Serialized(List(1, 2, 3))), (2, Serialized(List(4, 5))))) >>
      /*
      ┇ select x2.x3
      ┇ from (
      ┇   select count(1) as x3
      ┇   from (
      ┇     select x4."id" as x5, x4."b" as x6
      ┇     from "mapped_blob_t" x4
      ┇     where x4."b" = ?
      ┇   ) x7
      ┇ ) x2
       */
      ts.filter(_.b === Serialized(List(1, 2, 3))).length.result.map(_ shouldEqual 1) >>
      ts.schema.drop
    }
  }

  private def roundtrip[T: BaseColumnType](tn: String, v: T) = {
    class T1(tag: Tag) extends Table[(Int, T)](tag, tn) {
      def id = column[Int]("id")
      def data = column[T]("data")
      def * = (id, data)
    }
    lazy val ts = TableQuery[T1]

    db.exec {
      ts.schema.drop.asTry >>
      ts.schema.create >>
      (ts +=(1, v)) >>
      ts.map(_.data).result.head.map(_ shouldEqual v) >>
      ts.filter(_.data === v).map(_.id).result.headOption.map(_ shouldBe Some(1)) >>
      ts.filter(_.data =!= v).map(_.id).result.headOption.map(_ shouldBe None) >>
      ts.filter(_.data === v.bind).map(_.id).result.headOption.map(_ shouldBe Some(1)) >>
      ts.filter(_.data =!= v.bind).map(_.id).result.headOption.map(_ shouldBe None) >>
      ts.schema.drop
    }
  }

  test("date test") {
    roundtrip("date_t", Date.valueOf("2012-12-24"))
  }
  test("time test") {
    roundtrip("time_t", Time.valueOf("17:53:48"))
  }
  test("timestamp test") {
    roundtrip("timestamp_t", Timestamp.valueOf("2012-12-24 17:53:48"))

    class T2(tag: Tag) extends Table[Option[Timestamp]](tag, "timestamp_t2") {
      def t = column[Option[Timestamp]]("t")
      def * = t
    }
    val t2 = TableQuery[T2]

    db.exec {
      t2.schema.drop.asTry >>
      t2.schema.create >>
      (t2 += None) >>
      t2.result.head.map(_ shouldBe None) >>
      t2.schema.drop
    }
  }

  test("uuid test") {
    roundtrip[UUID]("uuid_t", UUID.randomUUID())
  }

  test("override identity type") {
    class T1(tag: Tag) extends Table[Int](tag, "t1") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc, O.SqlType("_FOO_BAR_"))
      def * = id
    }
    val ts = TableQuery[T1]

    ts.schema.createStatements.mkString should include("_FOO_BAR_")
  }
}
