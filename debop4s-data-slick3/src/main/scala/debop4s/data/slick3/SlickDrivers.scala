package debop4s.data.slick3

import slick.driver._

/**
 * Slick 에서 제공하는 Driver 를 나타냅니다.
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 29.
 */
object SlickDrivers {

  lazy val drivers: Map[String, JdbcDriver] =
    Map(
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

  lazy val driversInv: Map[JdbcDriver, String] = drivers map {
    case (k, v) if k != "org.mariadb.jdbc.Driver" => (v, k)
  }

  def get(driverClass: String): Option[JdbcDriver] =
    drivers.get(driverClass)

  def getOrElse(driverClass: String, default: => JdbcDriver): JdbcDriver =
    drivers.getOrElse(driverClass, default)


}
