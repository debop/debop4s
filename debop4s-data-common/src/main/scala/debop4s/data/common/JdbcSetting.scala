package debop4s.data.common

/**
 * Database 연결을 위한 정보 
 * @param driverClass   Driver class name
 * @param jdbcUrl       Jdbc URL
 * @param username      User name
 * @param password      Password
 * @param minIdleSize   Minimum Idle size
 * @param maxPoolSize   Maximum Pool Size
 */
case class JdbcSetting(driverClass: String,
                       jdbcUrl: String,
                       username: String = "sa",
                       password: String = "",
                       minIdleSize: Int = 2,
                       maxPoolSize: Int = 32) {
}
