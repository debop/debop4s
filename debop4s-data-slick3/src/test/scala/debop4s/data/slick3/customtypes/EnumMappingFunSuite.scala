package debop4s.data.slick3.customtypes

import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._
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

  class Devices(tag: Tag) extends EntityTable[Device](tag, "enum_device_option") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.Length(128))

    def osType = column[Option[OSType]]("osType", O.Length(32))

    def * = (name, osType, id.?) <>(Device.tupled, Device.unapply)
  }
  lazy val devices = EntityTableQuery[Device, Devices](cons = tag => new Devices(tag), lens[Device] >> 'id)

  class Device2s(tag: Tag) extends EntityTable[Device2](tag, "enum_device2") {
    def id = column[Int]("device_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("device_name", O.Length(128))
    def osType = column[OSType]("osType", O.Length(32))

    def * = (name, osType, id.?) <>(Device2.tupled, Device2.unapply)
  }
  lazy val device2s = EntityTableQuery[Device2, Device2s](cons = tag => new Device2s(tag), lens[Device2] >> 'id)

  lazy val schema = devices.schema ++ device2s.schema

  before {
    commit {
      schema.drop.asTry >>
      schema.create
    }
  }
  after {
    commit { schema.drop.asTry }
  }

  test("Device with Optional Enum <-> String") {
    val (nexus, iphone5, noneType) = readonly {
      for {
        nexus <- devices.save(Device("Nexus", Some(OSType.Android)))
        iphone5 <- devices.save(Device("iPhone5", Some(OSType.iOS)))
        none <- devices.save(Device("None", None))
      } yield (nexus, iphone5, none)
    }

    val nexusLoaded = readonly { devices.filter(_.name === "Nexus".bind).take(1).result }.headOption
    val iphone5Loaded = readonly { devices.filter(_.name === "iPhone5".bind).take(1).result }.headOption
    val noneTypeLoaded = readonly { devices.filter(_.name === "None".bind).take(1).result }.headOption

    nexusLoaded shouldBe defined
    nexusLoaded.get shouldEqual nexus

    iphone5Loaded shouldBe defined
    iphone5Loaded.get shouldEqual iphone5

    noneTypeLoaded shouldBe defined
    noneTypeLoaded.get shouldEqual noneType
  }

  test("Device with Enum <-> String") {
    val (nexus, iphone5, noneType) = readonly {
      for {
        nexus <- device2s.save(Device2("Nexus", OSType.Android))
        iphone5 <- device2s.save(Device2("iPhone5", OSType.iOS))
        none <- device2s.save(Device2("None", OSType.Unknown))
      } yield (nexus, iphone5, none)
    }

    val nexusLoaded = readonly { device2s.filter(_.name === "Nexus".bind).take(1).result }.headOption
    val iphone5Loaded = readonly { device2s.filter(_.name === "iPhone5".bind).take(1).result }.headOption
    val noneTypeLoaded = readonly { device2s.filter(_.name === "None".bind).take(1).result }.headOption

    nexusLoaded shouldBe defined
    nexusLoaded.get shouldEqual nexus

    iphone5Loaded shouldBe defined
    iphone5Loaded.get shouldEqual iphone5

    noneTypeLoaded shouldBe defined
    noneTypeLoaded.get shouldEqual noneType
  }

}
