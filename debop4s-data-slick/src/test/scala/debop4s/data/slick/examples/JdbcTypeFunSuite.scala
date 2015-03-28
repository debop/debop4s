package debop4s.data.slick.examples

import java.sql.{ Blob, Date, Time, Timestamp }
import java.util.UUID
import javax.sql.rowset.serial.SerialBlob

import debop4s.core.io.Serializers
import debop4s.core.utils.Streams
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._
import debop4s.data.slick.{ AbstractSlickFunSuite, SlickContext }

import scala.util.Try

/**
 * JdbcTypeFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcTypeFunSuite extends AbstractSlickFunSuite {

  test("byte array") {
    class T(tag: Tag) extends Table[(Int, Array[Byte])](tag, "type_byte_array") {
      def id = column[Int]("id")
      def data = column[Array[Byte]]("data")
      def * = (id, data)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.createStatements foreach println
      ts.ddl.create

      ts.insert(1, Array[Byte](1, 2, 3))
      ts +=(2, Array[Byte](4, 5))

      ts.list.map { case (id, data) => (id, data.mkString) }.toSet shouldEqual Set((1, "123"), (2, "45"))

      if (implicitly[ColumnType[Array[Byte]]].hasLiteralForm) {
        LOG.debug("Array[Byte] 가 Literal 로 표현됨")
        ts.filter(_.data === Array[Byte](4, 5)).map(_.data).run.map(_.mkString) shouldEqual "45"
      }
    }
  }

  test("byte array optional") {
    class T(tag: Tag) extends Table[(Int, Option[Array[Byte]])](tag, "type_byte_array_option") {
      def id = column[Int]("id")
      def data = column[Option[Array[Byte]]]("data")
      def * = (id, data)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.insert(1, Some(Array[Byte](6, 7)))
      // DB 가 null 설정을 할 수 없을 때에는 다른 컬럼만 값을 설정하게 합니다.
      ifCap(rcap.setByteArrayNull) { ts.insert(2, None) }
      ifNotCap(rcap.setByteArrayNull) { ts.map(_.id).insert(2) }

      ts.list
      .map { case (id, data) => (id, data.map(_.mkString).getOrElse("")) }
      .toSet shouldEqual Set((1, "67"), (2, ""))
    }
  }

  test("blob") {
    // NOTE: PostgreSQL은 Blob 수형을 지원하지 않습니다.
    if (SlickContext.isPostgres) {
      cancel("PostgreSQL은 Blob를 지원하지 않습니다.")
    }

    class T(tag: Tag) extends Table[(Int, Blob)](tag, "type_blob") {
      def id = column[Int]("id")
      def data = column[Blob]("data")
      def * = (id, data)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.createStatements foreach println
      ts.ddl.create

      ts.insert(1, new SerialBlob(Array[Byte](1, 2, 3)))
      ts.insert(2, new SerialBlob(Array[Byte](4, 5)))

      ts.mapResult { case (id, data) =>
        (id, data.getBytes(1, data.length.toInt).mkString)
      }.buildColl[Set] shouldEqual Set((1, "123"), (2, "45"))

      ts.list
      .map { case (id, data) => (id, data.getBytes(1, data.length.toInt).mkString) }
      .toSet shouldEqual Set((1, "123"), (2, "45"))
    }
  }

  /**
   * 특정 수형에 대해 Mapped Type (Converter)을 제공
   */
  test("mapped blob") {
    // NOTE: PostgreSQL은 Blob 수형을 지원하지 않습니다.
    if (SlickContext.isPostgres) {
      cancel("PostgreSQL은 Blob를 지원하지 않습니다.")
    }

    case class Serialized[T](value: T)

    implicit def serializedType[T]: driver.BaseColumnType[Serialized[T]] =
      MappedColumnType.base[Serialized[T], Blob](
      {
        (s: Serialized[T]) =>
          val b: Array[Byte] = Serializers.serializeObject(s.value)
          new SerialBlob(b)
      }, {
        b =>
          val bytes = Streams.toByteArray(b.getBinaryStream)
          val value = Serializers.deserializeObject(bytes, classOf[Any]).asInstanceOf[T]
          Serialized(value)
      }
                                                )

    class T(tag: Tag) extends Table[(Int, Serialized[List[Int]])](tag, "type_mapped_blob") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[Serialized[List[Int]]]("b")
      def * = (id, b)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.map(_.b).insertAll(Serialized(List(1, 2, 3)), Serialized(List(4, 5)))
      ts.list.toSet shouldEqual Set((1, Serialized(List(1, 2, 3))), (2, Serialized(List(4, 5))))
    }
  }

  private def roundtrip[T: BaseColumnType](tableName: String, v: T): Unit = {
    class T1(tag: Tag) extends Table[(Int, T)](tag, tableName) {
      def id = column[Int]("id")
      def data = column[T]("data")
      def * = (id, data)
    }
    val ts = TableQuery[T1]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.insert((1, v))
      ts.map(_.data).first shouldEqual v
      ts.filter(_.data === v).map(_.id).firstOption shouldEqual Some(1)
      ts.filter(_.data =!= v).map(_.id).firstOption shouldEqual None
      ts.filter(_.data === v.bind).map(_.id).firstOption shouldEqual Some(1)
      ts.filter(_.data =!= v.bind).map(_.id).firstOption shouldEqual None
    }
  }

  test("date") {
    roundtrip("type_roundtrip_date", Date.valueOf("2012-12-24"))
  }
  test("time") {
    roundtrip("type_roundtrip_time", Time.valueOf("17:53:48"))
  }
  test("timestamp") {
    roundtrip("type_roundtrip_timestamp", Timestamp.valueOf("2012-12-24 17:53:48"))
    // Option[Timestamp]는 BaseColumnType 이 아니므로 이렇게 할 수 없다.
    // roundtrip("type_roundtrip_timestamp_option", Some(Timestamp.valueOf("2012-12-24 17:53:48")))

    class T2(tag: Tag) extends Table[Option[Timestamp]](tag, "type_roundtrip_timestamp_option_2") {
      def t = column[Option[Timestamp]]("t")
      def * = t
    }
    val ts = TableQuery[T2]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.insert(None)
      ts.insert(Some(Timestamp.valueOf("2012-12-24 17:53:48")))
      ts.first shouldEqual None
      ts.drop(1).take(1).run should not be None
    }
  }

  test("uuid") {
    if (SlickContext.isH2) {
      cancel("H2 는 UUID를 지원하지 않습니다.")
    }

    roundtrip[UUID]("type_roundtrip_uuid", UUID.randomUUID())
  }

  test("O.DBType으로 DB 수형 재정의하기") {
    class T(tag: Tag) extends Table[Int](tag, "type_override") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc, O.DBType("_FOO_BAR_"))
      def * = id
    }
    val ts = TableQuery[T]

    ts.ddl.createStatements.mkString should include("_FOO_BAR_")
  }

}
