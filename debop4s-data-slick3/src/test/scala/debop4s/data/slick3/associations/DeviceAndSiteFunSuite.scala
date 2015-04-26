package debop4s.data.slick3.associations

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

    commit {
      schema.drop.asTry >>
      schema.create >>
      insertSampleData()
    }
  }

  override protected def afterAll(): Unit = {
    commit { schema.drop }
    super.afterAll()
  }

  private def insertSampleData() = {
    (sites ++= Seq(Site(name = "SKT"), Site(name = "KT"))) >>
    (devices ++= Seq(
      Device(5000, new DateTime(2013, 1, 1, 0, 0), 1),
      Device(1000, new DateTime(2014, 1, 1, 0, 0), 1),
      Device(500, new DateTime(2013, 1, 1, 0, 0), 1),
      Device(2000, new DateTime(2014, 1, 1, 0, 0), 1)
    ))
  }

  test("미리 정의된 쿼리에 inner join 으로 결합하기") {
    val sitesToDevices = (s: Sites, d: Devices) => s.id === d.siteId
    val sitesById = sites.filter(_.id === 1.bind)
    val devicesByPrice = devices.filter(_.price > 1000.0.bind)

    val qJoin = sitesById join devicesByPrice on sitesToDevices

    commit {
      for {
        sd <- qJoin.result
        names <- qJoin.map(_._1.name).groupBy(identity).map(_._1).result
      } yield {
        sd foreach { x => log.debug(s"$x") }
        names shouldEqual Seq("SKT")
        // ()
      }
    }
    //    qJoin.exec foreach println
    //    qJoin.map(_._1.name).exec.toSet shouldEqual Set("SKT")
  }

  implicit def joinCondition1: (Sites, Devices) => Rep[Boolean] =
    (s: Sites, d: Devices) => s.id === d.siteId

  test("inner join with implicit join condition") {
    val query = sites join devices on joinCondition1

    commit {
      for {
        _ <- query.result.map(rs => rs.foreach { r => log.debug(r.toString) })
        _ <- query.length.result.map(_ shouldEqual 4)
      } yield {
        //        rs foreach { r => log.debug(r.toString) }
        //        count shouldEqual 4
        ()
      }
    }
  }

}
