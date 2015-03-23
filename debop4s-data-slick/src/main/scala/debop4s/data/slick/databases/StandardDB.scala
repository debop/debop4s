package debop4s.data.slick.databases

import debop4s.config.server.DatabaseSetting
import debop4s.data.common.JdbcDrivers

import scala.collection.mutable

/**
 * [[SlickDB]] 를 생성해주는 Helper Class 입니다.
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
object StandardDB {

  lazy val Databases = mutable.Map[DatabaseSetting, SlickDB]()

  def getDB(dbSetting: DatabaseSetting): SlickDB = synchronized {
    Databases.getOrElseUpdate(dbSetting, SlickDB(dbSetting))
  }

  def H2Mem: SlickDB =
    getDB(DatabaseSetting(JdbcDrivers.DRIVER_CLASS_H2, "jdbc:h2:mem"))

  def H2Disk(dbName: String, username: String = "sa", password: String = ""): SlickDB =
    getDB(DatabaseSetting(JdbcDrivers.DRIVER_CLASS_H2, s"jdbc:h2:$dbName", username, password))

  def HSqlMem: SlickDB =
    getDB(DatabaseSetting(JdbcDrivers.DRIVER_CLASS_HSQL, "jdbc:hsqldb:mem"))

  def HSqlDisk(dbName: String, username: String = "sa", password: String = ""): SlickDB =
    getDB(DatabaseSetting(JdbcDrivers.DRIVER_CLASS_HSQL, s"jdbc:hsqldb:$dbName", username, password))

  def Postgres(dbSetting: DatabaseSetting): SlickDB = getDB(dbSetting)

  def MySql(dbSetting: DatabaseSetting): SlickDB = getDB(dbSetting)
}
