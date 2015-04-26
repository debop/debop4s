package debop4s.data.slick3.config

import debop4s.config.ConfigUtils
import debop4s.core.utils.Closer
import debop4s.core.utils.Closer._
import debop4s.data.common.DataSources
import debop4s.data.slick3.{SlickContext, AbstractSlickFunSuite}

/**
 * ConfigFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ConfigFunSuite extends AbstractSlickFunSuite {

  private def loadSlickConfig(conf: String): SlickConfig = {
    SlickConfig(ConfigUtils.load(conf, rootPath = "slick"))
  }

  private def assertDatabaseSettings(conf: String): Unit = {
    val slickConfig = loadSlickConfig(conf)
    val dbSetting = slickConfig.database.dbSetting
    log.debug(s"db setting=$dbSetting")

    dbSetting should not be null

    val ds = DataSources.getDataSource(dbSetting)
    using(SlickContext.forDataSource(ds)) { db =>
      db should not be null
    }

    slickConfig.masters.size shouldBe 0
    slickConfig.slaves.size shouldBe 0
  }

  test("기본 설정 로드") {
    val confs = Seq("slick-h2", "slick-mariadb", "slick-mysql")

    confs foreach { conf =>
      log.debug(s"conf=conf")
      assertDatabaseSettings(conf)
    }
  }

  private def assertMasterSlavesSettings(conf: String): Unit = {
    val slickConfig = loadSlickConfig(conf)
    val dbSetting = slickConfig.database.dbSetting
    dbSetting should not be null

    val ds = DataSources.getDataSource(dbSetting)
    ds should not be null

    slickConfig.masterSettings.length should be > 0
    slickConfig.masterSettings foreach { setting =>
      log.debug(s"Master setting=$setting")
      val ds = DataSources.getDataSource(setting)
      ds should not be null
    }

    slickConfig.slaveSettings.length should be > 0
    slickConfig.slaveSettings foreach { setting =>
      log.debug(s"Slave setting=$setting")
      val ds = DataSources.getDataSource(setting)
      ds should not be null
    }
  }

  test("Master-Slaves 환경 설정 테스트 - mariadb") {
    assertMasterSlavesSettings("slick-mariadb-master-slaves")
  }

  test("SlickContext masterDB, slaveDB") {
    val slickConfig = loadSlickConfig("slick-mariadb-master-slaves")

    slickConfig.masters.size should be > 0
    slickConfig.slaves.size should be > 0

    SlickContext.init(slickConfig)

    (0 until 100).par.foreach { x =>
      using(SlickContext.masterDB.createSession()) { session =>
        log.debug(s"Master index = ${ SlickContext.getMasterIndex }")
        session should not be null
      }
    }

    (0 until 100).par.foreach { x =>
      using(SlickContext.slaveDB.createSession()) { session =>
        log.debug(s"Slave index = ${ SlickContext.getSlaveIndex }")
        session should not be null
      }
    }
  }

}
