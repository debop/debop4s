package com.github.debop4s.data.jdbc

import com.github.debop4s.data.{DRIVER_CLASS_HSQL, DRIVER_CLASS_H2}
import com.jolbox.bonecp.BoneCPDataSource
import com.zaxxer.hikari.{HikariDataSource, HikariConfig}
import java.util.Properties
import javax.sql.DataSource
import org.slf4j.LoggerFactory
import scala.collection.immutable.HashMap


/**
 * [[javax.sql.DataSource]] 를 생성, 제공하는 Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:22
 */
object DataSources {

    lazy val log = LoggerFactory.getLogger(getClass)
    lazy val processCount = Runtime.getRuntime.availableProcessors()

    /**
     * [[javax.sql.DataSource]] 를 빌드합니다. 기본적으로 Tomcat DataSource 를 사용합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @param username    사용자 명
     * @param passwd      사용자 패스워드
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    def getDataSource(driverClass: String, url: String, username: String = "", passwd: String = ""): DataSource = {
        getBoneCPDataSource(driverClass, url, username, passwd)
    }

    def getBoneCPDataSource(driverClass: String, url: String, username: String = "", passwd: String = ""): DataSource = {
        log.info("BoneCP DataSource를 빌드합니다... " +
                 s"driverClass=[$driverClass], url=[$url], username=[$username], passwd=[$passwd]")

        val ds = new BoneCPDataSource()
        ds.setDriverClass(driverClass)
        ds.setJdbcUrl(url)
        ds.setUsername(username)
        ds.setPassword(passwd)

        ds.setMaxConnectionsPerPartition(100)
        ds.setMinConnectionsPerPartition(2)
        ds.setPartitionCount(processCount)

        ds
    }

    /**
     * HikariCP DataSource를 생성합니다.
     * @param dataSourceClassName dataSourceClassName ( 기존 driverClass 가 아닙니다 : mysql용은 com.mysql.jdbc.jdbc2.optional.MysqlDataSource 입니다 )
     * @param url         Database 주소
     * @param username    사용자 명
     * @param passwd      사용자 패스워드
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    def getHikariDataSource(dataSourceClassName: String,
                            url: String,
                            username: String = "",
                            passwd: String = "",
                            props: Map[String, String] = HashMap()): DataSource = {
        log.info("Hikari DataSource를 빌드합니다... " +
                 s"dataSourceClassName=[$dataSourceClassName], url=[$url], username=[$username], passwd=[$passwd]")

        val config = new HikariConfig()

        config.setDataSourceClassName(dataSourceClassName)
        config.addDataSourceProperty("url", url)
        config.addDataSourceProperty("user", username)
        config.addDataSourceProperty("password", passwd)

        if (props != null) {
            for ((name, value) <- props) {
                config.addDataSourceProperty(name, value)
            }
        }

        config.setAcquireIncrement(processCount)
        config.setMaximumPoolSize(processCount * 100)
        config.setConnectionTestQuery("SELECT 1")
        config.setRegisterMbeans(true)
        config.setUseInstrumentation(false)

        new HikariDataSource(config)
    }

    /**
    * DataSource 속성을 가진 Properties 로 설정합니다.
    *
    * {{{
    *   acquireIncrement=3
    *   acquireRetryDelay=1000
    *   connectionTestQuery=SELECT 1
    *   dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
    *   dataSource.username=test
    *   dataSource.password=test
    *   dataSource.databaseName=mydb
    *   dataSource.serverName=localhost
    *   dataSource.encoding=UTF-8
    *   dataSource.useUnicode=true
    * }}}
    */
    def getHirakiDataSource(props: Properties): DataSource = {
        log.info(s"Hikari DataSource를 빌드합니다... props=$props")

        val config = new HikariConfig(props)
        new HikariDataSource(config)
    }

    /** 테스트에 사용하기 위해 메모리를 사용하는 HSql DB 에 대한 DataSource 를 반환합니다. */
    def getEmbeddedHSqlDataSource: DataSource = {
        getDataSource(DRIVER_CLASS_HSQL, "jdbc:hsqldb:mem:test;MVCC=TRUE;", "sa", "")
    }

    /** 테스트에 사용하기 위해 메모리를 사용하는 H2 DB 에 대한 DataSource 를 반환합니다. */
    def getEmbeddedH2DataSource: DataSource = {
        getDataSource(DRIVER_CLASS_H2, "jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE;", "sa", "")
    }
}
