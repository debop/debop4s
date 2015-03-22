package debop4s.data.common

/**
 * JdbcDrivers
 * @author sunghyouk.bae@gmail.com
 */
object JdbcDrivers {

  val DATASOURCE_CLASS_H2: String = "org.h2.jdbcx.JdbcDataSource"
  val DRIVER_CLASS_H2: String = "org.h2.Driver"
  val DIALECT_H2: String = "org.hibernate.dialect.H2Dialect"

  val DATASOURCE_CLASS_HSQL: String = "org.hsqldb.jdbc.JDBCDataSource"
  val DRIVER_CLASS_HSQL: String = "org.hsqldb.jdbcDriver"
  val DIALECT_HSQL: String = "org.hibernate.dialect.HSQLDialect"

  val DATASOURCE_CLASS_MYSQL: String = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
  val DRIVER_CLASS_MYSQL: String = "com.mysql.jdbc.Driver"
  val DIALECT_MYSQL: String = "org.hibernate.dialect.MySQL5InnoDBDialect"

  val DRIVER_CLASS_MARIADB: String = "org.mariadb.jdbc.Driver"

  val DATASOURCE_CLASS_POSTGRESQL: String = "org.postgresql.ds.PGSimpleDataSource"
  val DRIVER_CLASS_POSTGRESQL: String = "org.postgresql.Driver"
  val DIALECT_POSTGRESQL: String = "org.hibernate.dialect.PostgreSQL82Dialect"
}
