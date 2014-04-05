package com.github.debop4s.data.tests.jdbc

import com.github.debop4s.data._
import com.github.debop4s.data.jdbc.DataSources
import java.util.Properties
import org.scalatest.{Matchers, FunSuite}
import scala.collection.immutable.HashMap

/**
 * DataSourcesTest
 * Created by debop on 2014. 3. 4.
 */
class DataSourcesTest extends FunSuite with Matchers {

    test("HikariCP create datasource by promgramatic") {

        val ds = DataSources.getHikariDataSource(DATASOURCE_CLASS_H2, "jdbc:h2:mem:test", "sa", "")
        val conn = ds.getConnection
        conn should not be null

        conn.close()
        conn.isClosed should equal(true)
    }

    test("HikariCP create multiple datasources by promgramatic for MySQL") {
        val url = "jdbc:mysql://localhost:3306/test"
        val props = HashMap(
            "characterEncoding" -> "UTF-8",
            "useUnicode" -> "true",
            "cachePrepStmts" -> "true",
            "prepStmtCacheSize" -> "250",
            "prepStmtCacheSqlLimit" -> "2048",
            "useServerPrepStmts" -> "true"
        )
        val ds = DataSources.getHikariDataSource(DATASOURCE_CLASS_MYSQL, url, props = props)

        ds should not be null

        (0 until 100).par.foreach { x =>
            val conn = ds.getConnection
            val ps = conn.prepareStatement("SELECT 1")
            ps.execute() should equal(true)

            Thread.sleep(10)
            conn.close()
            conn.isClosed should equal(true)
        }
    }

    test("HikariCP create datasource from properties file with MySQL") {

        val props = new Properties()
        props.load(getClass.getClassLoader.getResourceAsStream("hikari-mysql.properties"))
        val ds = DataSources.getHirakiDataSource(props)

        ds should not be null

        (0 until 100).par.foreach { x =>
            val conn = ds.getConnection
            val ps = conn.prepareStatement("SELECT 1")
            ps.execute() should equal(true)
            Thread.sleep(10)
            conn.close()
        }
    }

    test("HikariCP create datasource from properties file with H2") {

        val props = new Properties()
        props.load(getClass.getClassLoader.getResourceAsStream("hikari-h2.properties"))
        val ds = DataSources.getHirakiDataSource(props)

        ds should not be null

        (0 until 100).par.foreach { x =>
            val conn = ds.getConnection
            val ps = conn.prepareStatement("SELECT 1")
            ps.execute() should equal(true)
            Thread.sleep(10)
            conn.close()
        }
    }
}
