package debop4s.data.slick.config

import debop4s.config.ConfigUtils
import debop4s.data.common.DataSources
import debop4s.data.slick.{AbstractSlickFunSuite, SlickContext}

/**
 * Slick 관련 환경설정 테스트
 * @author sunghyouk.bae@gmail.com
 */
class SlickConfigFunSuite extends AbstractSlickFunSuite {

  private def loadSlickConfig(conf: String): SlickConfig = {
    SlickConfig(ConfigUtils.load(conf, rootPath = "slick"))
  }

  private def testDatabaseSetting(conf: String) = {
    val slickConfig = loadSlickConfig(conf)

    val dbSetting = slickConfig.database.dbSetting
    LOG.debug(s"db setting=$dbSetting")

    dbSetting should not be null

    val ds = DataSources.getDataSource(dbSetting)
    ds should not be null

    slickConfig.masters.size shouldEqual 0
    slickConfig.slaves.size shouldEqual 0
  }

  test("기본 DM 설정 로드") {
    val confs = Seq("slick-h2", "slick-mariadb", "slick-mysql")

    confs foreach { conf =>
      LOG.debug(s"conf=$conf")
      testDatabaseSetting(conf)
    }
  }

  private def testMasterSlaveSetting(conf: String): Unit = {
    val slickConfig = loadSlickConfig(conf)

    val dbSetting = slickConfig.database.dbSetting
    LOG.debug(s"db setting=$dbSetting")

    dbSetting should not be null

    val ds = DataSources.getDataSource(dbSetting)
    ds should not be null

    slickConfig.masterSettings.length should be > 0
    slickConfig.masterSettings foreach { setting =>
      LOG.debug(s"Master Setting=$setting")
      val ds = DataSources.getDataSource(setting)
      ds should not be null
    }

    slickConfig.slaveSettings.length should be > 0
    slickConfig.slaveSettings foreach { setting =>
      LOG.debug(s"Slave Setting=$setting")
      val ds = DataSources.getDataSource(setting)
      ds should not be null
    }
  }

  test("Master-Slaves 환경 설정 테스트 - mariadb") {
    testMasterSlaveSetting("slick-mariadb-master-slaves")
  }

  test("SlickContext master, slaves") {

    val slickConfig = loadSlickConfig("slick-mariadb-master-slaves")

    slickConfig.masters.length should be > 0
    slickConfig.slaves.length should be > 0

    SlickContext.init(slickConfig)
    //      Thread.sleep(10)
    //      SlickContext.isMySQL shouldEqual true

    ( 0 until 100 ).par.foreach { x =>
      SlickContext.masterDB.withSession { implicit session =>
        LOG.debug(s"Master index=${ SlickContext.masterIndex.get() }")
        session should not be null
      }

      SlickContext.slaveDB.withSession { implicit session =>
        LOG.debug(s"Slave index=${ SlickContext.slaveIndex.get() }")
        session should not be null
      }
    }
  }

}
