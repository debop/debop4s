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

    val ddl = devices.ddl ++ sites.ddl

    withTransaction { implicit session =>
      Try { ddl.drop }
    }

    withTransaction { implicit session =>
      ddl.create
      insertSampleData()
    }
  }

  private def insertSampleData()(implicit session: Session): Unit = {
    Seq(Site(name = "SKT"), Site(name = "HCT")) foreach sites.insert

    devices ++= Seq(Device(None, 5000, new DateTime(2013, 1, 1, 0, 0), 1),
                     Device(None, 1000, new DateTime(2014, 1, 1, 0, 0), 1),
                     Device(None, 500, new DateTime(2013, 1, 1, 0, 0), 2),
                     Device(None, 2000, new DateTime(2014, 1, 1, 0, 0), 2))
  }

  test("미리 정의된 쿼리에 inner join으로 결합하기") {
    val sitesToDevices = (s: Sites, d: Devices) => s.id === d.siteId
    val sitesById = sites.filter(_.id === 1)
    val devicesByPrice = devices.filter(_.price > 1000.0)

    val joinQuery = sitesById join devicesByPrice on sitesToDevices

    LOG.debug(s"Join=\n${ joinQuery.selectStatement }")

    withReadOnly { implicit session =>
      joinQuery.run foreach println
    }
  }

  implicit def siteAndDeviceJoinCondition: (AssociationDatabase.Sites, AssociationDatabase.Devices) => Column[Boolean] =
    (s: Sites, d: Devices) => s.id === d.siteId

  test("inner join") {
    val query = sites join devices on siteAndDeviceJoinCondition

    LOG.debug(s"Joins=\n${ query.selectStatement }")

    withReadOnly { implicit session =>
      query.run foreach println
    }
  }

  // 쿼리 확장 메소드를 Schema 에 미리 정의해 놓으면 사용하기 좋다.
  implicit class DeviceExtensions(val query: Query[Devices, Device, Seq]) {
    def withSites(s: Query[Sites, Site, Seq] = AssociationDatabase.sites): Query[(Devices, Sites), (Device, Site), Seq] = {
      query.join(s).on(_.siteId === _.id)
    }

    def sites(s: Query[Sites, Site, Seq] = AssociationDatabase.sites): Query[Sites, Site, Seq] = {
      query.withSites(s).map(_._2)
    }
  }

  test("join query with query extensions ") {
    val hctDevices = devices.withSites(sites.filter(_.name === "HCT".bind)).map { case (d, s) => (d.id, d.price, s.name) }
    LOG.debug(s"HCT devices=\n${ hctDevices.selectStatement }")

    withReadOnly { implicit session =>
      hctDevices.list foreach println
    }
  }

}
