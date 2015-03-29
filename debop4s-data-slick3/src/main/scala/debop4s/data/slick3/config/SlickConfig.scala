package debop4s.data.slick3.config

import debop4s.config.server.{ DatabaseElement, DatabaseSetting, DatabaseSupport }
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 * Slick 에서 사용할 환경설정 정보를 나타냅니다.
 * {{{
 * slick {
 *    database {
 *      host = "127.0.0.1:3306"
 *      name = "slick"
 *      driverClass = "com.mysql.jdbc.Driver"
 *      url = "jdbc:mysql://"${slick.database.host}"/"${slick.database.name}
 *      username = "root"
 *      password = "root"
 *
 *      maxPoolSize = 64
 *    }
 * }
 * }}}
 */
case class SlickConfig(config: com.typesafe.config.Config) extends DatabaseSupport {

  private val LOG = LoggerFactory.getLogger(getClass)

  /** Master DB 의 Connection 정보를 가지는 환경설정 정보 */
  lazy val masters: IndexedSeq[DatabaseElement] = loadDatabases("masters")

  /** Master DB의 설정 정보 (Master가 하나인 경우는 `database` 를 사용하세요 */
  lazy val masterSettings: IndexedSeq[DatabaseSetting] = masters.map(_.dbSetting).toIndexedSeq

  /** Slave DB 의 Connection 정보를 가지는 환경설정 정보 */
  lazy val slaves: IndexedSeq[DatabaseElement] = loadDatabases("slaves")

  /** Slave DB 의 Connection 정보를 가지는 환경설정 정보 */
  lazy val slaveSettings: IndexedSeq[DatabaseSetting] = slaves.map(_.dbSetting).toIndexedSeq


  private def loadDatabases(nodeName: String = "masters"): IndexedSeq[DatabaseElement] = {
    try {
      config.getConfigList(nodeName).asScala.map { cfg =>
        val master = new DatabaseElement(cfg)
        LOG.debug(s"새로운 Database 환경을 로드했습니다. master=${ master.dbSetting }")
        master
      }.toIndexedSeq
    } catch {
      case NonFatal(e) =>
        LOG.warn(s"$nodeName 환경설정을 로드하는데 실패했습니다. $nodeName node가 정의되지 않았습니다.")
        IndexedSeq()
    }
  }
}
