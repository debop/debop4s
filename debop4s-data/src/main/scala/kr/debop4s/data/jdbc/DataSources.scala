package kr.debop4s.data.jdbc

import javax.sql.DataSource
import org.apache.tomcat.jdbc.pool.PoolProperties
import org.slf4j.LoggerFactory

/**
 * [[javax.sql.DataSource]] 를 생성, 제공하는 Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:22
 */
object DataSources {

    lazy val log = LoggerFactory.getLogger(getClass)

    final val HSQL_DRIVER_CLASS_NAME: String = "org.hsql.jdbcDriver"
    final val H2_DRIVER_CLASS_NAME: String = "org.h2.Driver"

    /**
     * [[javax.sql.DataSource]] 를 빌드합니다. 기본적으로 Tomcat DataSource 를 사용합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    def getDataSource(driverClass: String, url: String): DataSource =
        getDataSource(driverClass, url, "", "")


    /**
     * [[javax.sql.DataSource]] 를 빌드합니다. 기본적으로 Tomcat DataSource 를 사용합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @param username    사용자 명
     * @param passwd      사용자 패스워드
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    def getDataSource(driverClass: String, url: String, username: String, passwd: String): DataSource =
        getTomcatDataSource(driverClass, url, username, passwd)

    /**
     * Tomcat DataSource 를 빌드합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @param username    사용자 명
     * @param passwd      사용자 패스워드
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    def getTomcatDataSource(driverClass: String, url: String, username: String, passwd: String): DataSource = {
        log.debug(s"Tomcat DataSource를 빌드합니다... " +
                  s"driverClass=[$driverClass], url=[$url], username=[$username], passwd=[$passwd]")

        val p: PoolProperties = new PoolProperties
        p.setUrl(url)
        p.setDriverClassName(driverClass)
        p.setUsername(username)
        p.setPassword(passwd)
        p.setJmxEnabled(true)
        p.setTestWhileIdle(true)
        p.setTestOnBorrow(true)
        p.setValidationQuery("SELECT 1")
        p.setTestOnReturn(false)
        p.setValidationInterval(30000)
        p.setTimeBetweenEvictionRunsMillis(30000)
        p.setMaxActive(200)
        p.setInitialSize(10)
        p.setMaxWait(10000)
        p.setRemoveAbandonedTimeout(60)
        p.setMinEvictableIdleTimeMillis(30000)
        p.setMinIdle(10)

        new org.apache.tomcat.jdbc.pool.DataSource(p)
    }

    /** 테스트에 사용하기 위해 메모리를 사용하는 HSql DB 에 대한 DataSource 를 반환합니다. */
    def getEmbeddedHSqlDataSource: DataSource =
        getDataSource(HSQL_DRIVER_CLASS_NAME, "jdbc:hsqldb:mem:test;MVCC=TRUE;", "sa", "")

    /** 테스트에 사용하기 위해 메모리를 사용하는 H2 DB 에 대한 DataSource 를 반환합니다. */
    def getEmbeddedH2DataSource: DataSource =
        getDataSource(H2_DRIVER_CLASS_NAME, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "sa", "")
}
