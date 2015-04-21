package debop4s.data.slick3.customtypes

import debop4s.data.slick3.customtypes.EnumMapStringSupport
import debop4s.data.slick3._
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.model.IntEntity
import shapeless._

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

  case class Device(name: String, osType: Option[OSType] = None, var id: Option[Int] = None) extends IntEntity
  case class Device2(name: String, osType: OSType = Unknown, var id: Option[Int] = None) extends IntEntity

  class Devices(tag: Tag) extends IdTable[Device, Int](tag, "enum_device_option") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.Length(128))

    def osType = column[Option[OSType]]("osType", O.Length(32))

    def * = (name, osType, id.?) <>(Device.tupled, Device.unapply)
  }
  //  lazy val devices = TableQuery[Devices]
  //  implicit class DeviceQueryExt(query: TableQuery[Devices]) extends IdTableExtensions[Device, Int](query)
  lazy val devices = EntityTableQuery[Device, Devices](cons = tag => new Devices(tag), lens[Device] >> 'id)

  class Device2s(tag: Tag) extends IdTable[Device2, Int](tag, "enum_device2") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.Length(128))
    def osType = column[OSType]("osType", O.Length(32))

    def * = (name, osType, id.?) <>(Device2.tupled, Device2.unapply)
  }
  //  lazy val device2s = TableQuery[Device2s]
  //  implicit class Device2QueryExt(query: TableQuery[Device2s]) extends IdTableExtensions[Device2, Int](query)
  lazy val device2s = EntityTableQuery[Device2, Device2s](cons = tag => new Device2s(tag), lens[Device2] >> 'id)

  lazy val schema = devices.schema ++ device2s.schema

  override def beforeAll(): Unit = {
    super.beforeAll()

    {
      schema.drop.asTry >>
      schema.create
    }.commit
  }

  override def afterAll(): Unit = {
    schema.drop.asTry.commit
    super.afterAll()
  }

  test("Device with Optional Enum <-> String") {
    val nexus = devices.save(Device("Nexus", Some(OSType.Android))).commit
    val iphone5 = devices.save(Device("iPhone5", Some(OSType.iOS))).commit
    val noneType = devices.save(Device("None", None)).commit

    /*
    ┇ select x2."device_id", x2."device_name", x2."osType"
    ┇ from (
    ┇   select x3."device_id" as "device_id", x3."device_name" as "device_name", x3."osType" as "osType"
    ┇   from "enum_device_option" x3
    ┇   where x3."device_name" = ?
    ┇   limit 1
    ┇ ) x2
     */
    val nexusLoaded = devices.filter(_.name === "Nexus".bind).take(1).exec.headOption
    val iphone5Loaded = devices.filter(_.name === "iPhone5".bind).take(1).exec.headOption
    val noneTypeLoaded = devices.filter(_.name === "None".bind).take(1).exec.headOption

    nexusLoaded shouldBe defined
    nexusLoaded.get shouldEqual nexus

    iphone5Loaded shouldBe defined
    iphone5Loaded.get shouldEqual iphone5

    noneTypeLoaded shouldBe defined
    noneTypeLoaded.get shouldEqual noneType
  }

  test("Device with Enum <-> String") {
    val nexus = device2s.save(Device2("Nexus", OSType.Android)).commit
    val iphone5 = device2s.save(Device2("iPhone5", OSType.iOS)).commit
    val noneType = device2s.save(Device2("None")).commit

    /*
    ┇ select x2."device_id", x2."device_name", x2."osType"
    ┇ from (
    ┇   select x3."device_id" as "device_id", x3."device_name" as "device_name", x3."osType" as "osType"
    ┇   from "enum_device_option" x3
    ┇   where x3."device_name" = ?
    ┇   limit 1
    ┇ ) x2
     */
    val nexusLoaded = device2s.filter(_.name === "Nexus".bind).take(1).exec.headOption
    val iphone5Loaded = device2s.filter(_.name === "iPhone5".bind).take(1).exec.headOption
    val noneTypeLoaded = device2s.filter(_.name === "None".bind).take(1).exec.headOption

    nexusLoaded shouldBe defined
    nexusLoaded.get shouldEqual nexus

    iphone5Loaded shouldBe defined
    iphone5Loaded.get shouldEqual iphone5

    noneTypeLoaded shouldBe defined
    noneTypeLoaded.get shouldEqual noneType
  }

}
