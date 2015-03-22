package debop4s.data.slick.utils

import debop4s.config.ConfigUtils
import debop4s.core.io.FstSerializer
import debop4s.data.slick.config.SlickConfig

import scala.slick.driver._

/**
 * SlickUtils
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
object SlickUtils {

  private lazy val serializer = FstSerializer()

  def copyObject[E <: Serializable](entity: E): E = {
    serializer.deserialize(serializer.serialize(entity)).asInstanceOf[E]
  }

  lazy val drivers = Map(
                          "org.apache.derby.jdbc.EmbeddedDriver" -> DerbyDriver,
                          "org.h2.Driver" -> H2Driver,
                          "org.hsqldb.driver" -> HsqldbDriver,
                          "org.hsqldb.jdbc.JDBCDriver" -> HsqldbDriver,
                          "org.mariadb.jdbc.Driver" -> MySQLDriver,
                          "com.mysql.jdbc.Driver" -> MySQLDriver,
                          // "com.mysql.jdbc.ReplicationDriver" -> MySQLDriver, // 필요 없다. jdbcUrl에 replication 만 주면 된다.
                          "org.postgresql.Driver" -> PostgresDriver,
                          "org.sqlite.JDBC" -> SQLiteDriver
                        )

  lazy val driversInv = drivers map {
    case (k, v) if k != "org.mariadb.jdbc.driver" => (v, k)
  }

  def loadConfig(resourceBasename: String, rootPath: String = "slick"): SlickConfig =
    SlickConfig(ConfigUtils.load(resourceBasename, rootPath))

}
