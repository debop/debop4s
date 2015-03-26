package debop4s.data.slick.associations

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.associations.model._
import debop4s.data.slick.associations.schema.AssociationDatabase
import debop4s.data.slick.associations.schema.AssociationDatabase._
import debop4s.data.slick.associations.schema.AssociationDatabase.driver.simple._
import org.joda.time.DateTime

import scala.util.Try

/**
 * DeviceAndSiteFunSuite
 * @author sunghyouk.bae@gmail.com at 15. 3. 23.
 */
class DeviceAndSiteFunSuite extends AbstractSlickFunSuite {

  override protected def beforeAll() {
    super.beforeAll()

    val ddl = Devices.ddl ++ Sites.ddl

    withTransaction { implicit session =>
      Try {ddl.drop}
    }

    withTransaction { implicit session =>
      ddl.create
      insertSampleData()
    }
  }

  private def insertSampleData()(implicit session: Session): Unit = {
    Seq(Site(name = "SKT"), Site(name = "HCT")) foreach Sites.insert

    Devices ++= Seq(Device(None, 5000, new DateTime(2013, 1, 1, 0, 0), 1),
                     Device(None, 1000, new DateTime(2014, 1, 1, 0, 0), 1),
                     Device(None, 500, new DateTime(2013, 1, 1, 0, 0), 2),
                     Device(None, 2000, new DateTime(2014, 1, 1, 0, 0), 2))
  }

  test("미리 정의된 쿼리에 inner join으로 결합하기") {
    val sitesToDevices = (s: SiteT, d: DeviceT) => s.id === d.siteId
    val sitesById = Sites.filter(_.id === 1)
    val devicesByPrice = Devices.filter(_.price > 1000.0)

    val joinQuery = sitesById join devicesByPrice on sitesToDevices

    LOG.debug(s"Join=\n${ joinQuery.selectStatement }")

    withReadOnly { implicit session =>
      joinQuery.run foreach println
    }
  }

  implicit def siteAndDeviceJoinCondition: (AssociationDatabase.SiteT, AssociationDatabase.DeviceT) => Column[Boolean] =
    (s: SiteT, d: DeviceT) => s.id === d.siteId

  test("inner join") {
    val query = Sites join Devices on siteAndDeviceJoinCondition

    LOG.debug(s"Joins=\n${ query.selectStatement }")

    withReadOnly { implicit session =>
      query.run foreach println
    }
  }

  // 쿼리 확장 메소드를 Schema 에 미리 정의해 놓으면 사용하기 좋다.
  implicit class DeviceExtensions(val query: Query[DeviceT, Device, Seq]) {
    def withSites(s: Query[SiteT, Site, Seq] = Sites): Query[(DeviceT, SiteT), (Device, Site), Seq] = {
      query.join(s).on(_.siteId === _.id)
    }

    def sites(s: Query[SiteT, Site, Seq] = Sites): Query[SiteT, Site, Seq] = {
      query.withSites(s).map(_._2)
    }
  }

  test("join query with query extensions ") {
    val hctDevices = Devices.withSites(Sites.filter(_.name === "HCT".bind)).map { case (d, s) => (d.id, d.price, s.name) }
    LOG.debug(s"HCT Devices=\n${ hctDevices.selectStatement }")

    withReadOnly { implicit session =>
      hctDevices.list foreach println
    }
  }

}
