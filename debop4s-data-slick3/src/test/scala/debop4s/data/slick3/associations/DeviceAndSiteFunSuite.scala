package debop4s.data.slick3.associations

import debop4s.core.utils.Closer
import debop4s.data.slick3.associations._
import debop4s.data.slick3._
import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._
import org.joda.time.DateTime
import slick.dbio
import slick.dbio.Effect.Write

/**
 * DeviceAndSiteFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class DeviceAndSiteFunSuite extends AbstractSlickFunSuite {

  lazy val schema = devices.schema ++ sites.schema

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    db.exec { schema.drop.asTry >> schema.create >> insertSampleData() }
  }

  private def insertSampleData() = {
    sites.forceInsertAll(Seq(Site(name = "SKT"), Site(name = "KT"))) andThen
    (devices ++= Seq(
      Device(None, 5000, new DateTime(2013, 1, 1, 0, 0), 1),
      Device(None, 1000, new DateTime(2014, 1, 1, 0, 0), 1),
      Device(None, 500, new DateTime(2013, 1, 1, 0, 0), 1),
      Device(None, 2000, new DateTime(2014, 1, 1, 0, 0), 1)
    ))
  }

  test("미리 정의된 쿼리에 inner join 으로 결합하기") {
    val sitesToDevices = (s: Sites, d: Devices) => s.id === d.siteId
    val sitesById = sites.filter(_.id === 1.bind)
    val devicesByPrice = devices.filter(_.price > 1000.0.bind)

    val qJoin = sitesById join devicesByPrice on sitesToDevices

    // LOG.debug(s"Query=\n${ qJoin.selectStatement }")

    db.result(qJoin) foreach println
    db.result(qJoin.map(_._1.name)).toSet shouldEqual Set("SKT")
  }

}
