package com.github.debop4s.data.jdbc

import com.jolbox.bonecp.BoneCPDataSource
import javax.sql.DataSource
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
    def getDataSource(driverClass: String, url: String, username: String, passwd: String): DataSource = {
        getBoneCPDataSource(driverClass, url, username, passwd)
    }

    def getBoneCPDataSource(driverClass: String, url: String, username: String, passwd: String): DataSource = {
        log.debug("BoneCP DataSource를 빌드합니다... " +
                  s"driverClass=[$driverClass], url=[$url], username=[$username], passwd=[$passwd]")

        val ds = new BoneCPDataSource()
        ds.setDriverClass(driverClass)
        ds.setJdbcUrl(url)
        ds.setUsername(username)
        ds.setPassword(passwd)

        val processCount = Runtime.getRuntime.availableProcessors()

        ds.setMaxConnectionsPerPartition(50)
        ds.setMinConnectionsPerPartition(2)
        ds.setPartitionCount(processCount)

        ds
    }

    /** 테스트에 사용하기 위해 메모리를 사용하는 HSql DB 에 대한 DataSource 를 반환합니다. */
    def getEmbeddedHSqlDataSource: DataSource =
        getDataSource(HSQL_DRIVER_CLASS_NAME, "jdbc:hsqldb:mem:test;MVCC=TRUE;", "sa", "")

    /** 테스트에 사용하기 위해 메모리를 사용하는 H2 DB 에 대한 DataSource 를 반환합니다. */
    def getEmbeddedH2DataSource: DataSource =
        getDataSource(H2_DRIVER_CLASS_NAME, "jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE;", "sa", "")
}
