package debop4s.data.slick3.customtypes

import debop4s.data.slick3.customtypes.EnumMapStringSupport
import debop4s.data.slick3._
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.model.IntEntity

/**
 * Enum 값을 String 또는 Int 값으로 DB에 저장할 수 있도록 변환해주는 [[EnumMapStringSupport]] 에 대한 테스트
 * @author sunghyouk.bae@gmail.com
 */
class EnumMappingFunSuite extends AbstractSlickFunSuite {

  object OSType extends Enumeration with EnumMapStringSupport {
    type OSType = Value
    val Unknown, Android, iOS = Value
  }

  import OSType._

  case class Device(var id: Option[Int] = None, name: String, osType: Option[OSType] = None) extends IntEntity
  case class Device2(var id: Option[Int] = None, name: String, osType: OSType = Unknown) extends IntEntity

  class Devices(tag: Tag) extends IdTable[Device, Int](tag, "enum_device_option") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.Length(128))

    def osType = column[Option[OSType]]("osType", O.Length(32))

    def * = (id.?, name, osType) <>(Device.tupled, Device.unapply)
  }
  lazy val devices = TableQuery[Devices]
  implicit class DeviceQueryExt(query: TableQuery[Devices]) extends IdTableExtensions[Device, Int](query)

  class Device2s(tag: Tag) extends IdTable[Device2, Int](tag, "enum_device2") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.Length(128))
    def osType = column[OSType]("osType", O.Length(32))

    def * = (id.?, name, osType) <>(Device2.tupled, Device2.unapply)
  }
  lazy val device2s = TableQuery[Device2s]
  implicit class Device2QueryExt(query: TableQuery[Device2s]) extends IdTableExtensions[Device2, Int](query)

  lazy val schema = devices.schema ++ device2s.schema

  override def beforeAll(): Unit = {
    super.beforeAll()

    Seq(
      schema.drop.asTry,
      schema.create
    ).run
  }

  override def afterAll(): Unit = {
    schema.drop.run
    super.afterAll()
  }

  test("Device with Optional Enum <-> String") {
    val nexus = devices.save(Device(None, "Nexus", Some(OSType.Android)))
    val iphone5 = devices.save(Device(None, "iPhone5", Some(OSType.iOS)))
    val noneType = devices.save(Device(None, "None", None))

    /*
    ┇ select x2."device_id", x2."device_name", x2."osType"
    ┇ from (
    ┇   select x3."device_id" as "device_id", x3."device_name" as "device_name", x3."osType" as "osType"
    ┇   from "enum_device_option" x3
    ┇   where x3."device_name" = ?
    ┇   limit 1
    ┇ ) x2
     */
    val nexusLoaded = devices.filter(_.name === "Nexus".bind).take(1).run.headOption
    val iphone5Loaded = devices.filter(_.name === "iPhone5".bind).take(1).run.headOption
    val noneTypeLoaded = devices.filter(_.name === "None".bind).take(1).run.headOption

    nexusLoaded shouldBe defined
    nexusLoaded.get shouldEqual nexus

    iphone5Loaded shouldBe defined
    iphone5Loaded.get shouldEqual iphone5

    noneTypeLoaded shouldBe defined
    noneTypeLoaded.get shouldEqual noneType
  }

  test("Device with Enum <-> String") {
    val nexus = device2s.save(Device2(None, "Nexus", OSType.Android))
    val iphone5 = device2s.save(Device2(None, "iPhone5", OSType.iOS))
    val noneType = device2s.save(Device2(None, "None"))

    /*
    ┇ select x2."device_id", x2."device_name", x2."osType"
    ┇ from (
    ┇   select x3."device_id" as "device_id", x3."device_name" as "device_name", x3."osType" as "osType"
    ┇   from "enum_device_option" x3
    ┇   where x3."device_name" = ?
    ┇   limit 1
    ┇ ) x2
     */
    val nexusLoaded = device2s.filter(_.name === "Nexus".bind).take(1).run.headOption
    val iphone5Loaded = device2s.filter(_.name === "iPhone5".bind).take(1).run.headOption
    val noneTypeLoaded = device2s.filter(_.name === "None".bind).take(1).run.headOption

    nexusLoaded shouldBe defined
    nexusLoaded.get shouldEqual nexus

    iphone5Loaded shouldBe defined
    iphone5Loaded.get shouldEqual iphone5

    noneTypeLoaded shouldBe defined
    noneTypeLoaded.get shouldEqual noneType
  }

}
