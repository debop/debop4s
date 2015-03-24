package debop4s.data.slick.customtypes

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._
import debop4s.data.slick.model.IntEntity

import scala.util.Try

/**
 * Enum 값을 String 또는 Int 값으로 DB에 저장할 수 있도록 변환해주는 [[EnumMapStringSupport]] 에 대한 테스트
 * @author sunghyouk.bae@gmail.com
 */
class EnumMappingFunSuite extends AbstractSlickFunSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()

    val ddl = Devices.ddl ++ Device2s.ddl

    withTransaction { implicit session =>
      Try {ddl.drop}
      ddl.create
    }
  }

  /**
   * Scala enum 을 매핑하려면 [[EnumMapStringSupport]] 나 [[EnumMapIntSupport]] 를 추가해야 한다.
   */
  object OSType extends Enumeration with EnumMapStringSupport {
    type OSType = Value
    val Unknown, Android, iOS = Value
  }

  import OSType._

  case class Device(var id: Option[Int] = None, name: String, osType: Option[OSType] = None) extends IntEntity
  case class Device2(var id: Option[Int] = None, name: String, osType: OSType = Unknown) extends IntEntity

  class DeviceT(tag: Tag) extends IdTable[Device, Int](tag, "enum_device_option") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.NotNull, O.Length(128, true))

    // Option[Enum] 은 컬럼이 Nullable 이어야 한다. 그냥 Enum 이라면 NotNull 이어야 하고
    def osType = column[Option[OSType]]("osType", O.Length(32, true))

    def * = (id.?, name, osType) <>(Device.tupled, Device.unapply)
  }
  lazy val Devices = TableQuery[DeviceT]
  implicit class DeviceQueryExt(query: TableQuery[DeviceT]) extends IdTableExtensions[Device, Int](query)

  class Device2T(tag: Tag) extends IdTable[Device2, Int](tag, "enum_device2") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.NotNull, O.Length(128, true))
    def osType = column[OSType]("osType", O.NotNull, O.Length(32, true))

    def * = (id.?, name, osType) <>(Device2.tupled, Device2.unapply)
  }
  lazy val Device2s = TableQuery[Device2T]
  implicit class Device2QueryExt(query: TableQuery[Device2T]) extends IdTableExtensions[Device2, Int](query)


  test("Device with Optional Enum <-> String") {
    withSession { implicit session =>
      val nexus = Devices.save(Device(None, "Nexus", Some(OSType.Android)))
      val iphone5 = Devices.save(Device(None, "iPhone5", Some(OSType.iOS)))
      val noneType = Devices.save(Device(None, "None", None))

      val nexusLoaded = Devices.filter(_.name === "Nexus".bind).firstOption
      val iphone5Loaded = Devices.filter(_.name === "iPhone5".bind).firstOption
      val noneTypeLoaded = Devices.filter(_.name === "None".bind).firstOption

      nexusLoaded shouldBe defined
      nexusLoaded.get shouldEqual nexus

      iphone5Loaded shouldBe defined
      iphone5Loaded.get shouldEqual iphone5

      noneTypeLoaded shouldBe defined
      noneTypeLoaded.get shouldEqual noneType
    }
  }

  test("Device with Enum <-> String") {
    withSession { implicit session =>
      val nexus = Device2s.save(Device2(None, "Nexus", OSType.Android))
      val iphone5 = Device2s.save(Device2(None, "iPhone5", OSType.iOS))
      val noneType = Device2s.save(Device2(None, "None"))

      val nexusLoaded = Device2s.filter(_.name === "Nexus".bind).firstOption
      val iphone5Loaded = Device2s.filter(_.name === "iPhone5".bind).firstOption
      val noneTypeLoaded = Device2s.filter(_.name === "None".bind).firstOption

      nexusLoaded shouldBe defined
      nexusLoaded.get shouldEqual nexus

      iphone5Loaded shouldBe defined
      iphone5Loaded.get shouldEqual iphone5

      noneTypeLoaded shouldBe defined
      noneTypeLoaded.get shouldEqual noneType
    }
  }
}
