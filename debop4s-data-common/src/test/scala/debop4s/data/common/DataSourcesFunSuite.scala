package debop4s.data.common

import java.util.Properties
import javax.sql.DataSource

import com.typesafe.config.ConfigFactory
import debop4s.config.server.DatabaseElement
import org.scalatest.{ FunSuite, Matchers, OptionValues }

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * DataSourceFactoryFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class DataSourcesFunSuite extends FunSuite with Matchers with OptionValues {

  test("Embedded H2 DataSource 생성") {
    val ds = DataSources.getEmbeddedH2DataSource
    ds should not be null
    connectionTest(ds)
  }

  test("Embedded HSqlDb DataSource 생성") {
    val ds = DataSources.getEmbeddedHSqlDataSource
    ds should not be null
    // HSQL 에 대해서는 Multi Thread 가 제대로 동작하지 않습니다.
    val conn = ds.getConnection
    conn should not be null

    conn.close()
    conn.isClosed shouldEqual true
  }


  test("DataSource 직접 생성") {
    val ds = DataSources.getDataSource(JdbcDrivers.DRIVER_CLASS_H2, "jdbc:h2:mem:test", "sa", "", null)
    ds should not be null
    connectionTest(ds)
  }

  test("프로그램에서 MySQL 용 DataSource 생성") {

    val url = "jdbc:mysql://localhost:3306/test"
    val props = mutable.HashMap(
                                 "characterEncoding" -> "UTF-8",
                                 "useUnicode" -> "true",
                                 "cachePrepStmts" -> "true",
                                 "prepStmtCacheSize" -> "250",
                                 "prepStmtCacheSqlLimit" -> "2048",
                                 "useServerPrepStmts" -> "true",
                                 "idleTimeout" -> "60000"
                               ).asJava
    val ds = DataSources.getDataSource(JdbcDrivers.DRIVER_CLASS_MYSQL, url, "", "", props = props)
    ds should not be null

    connectionTest(ds)
  }

  //  test("프로그램에서 PostgreSQL 용 DataSource 생성") {
  //
  //    val url = "jdbc:postgresql://localhost/postgres"
  //    val ds = DataSources.getDataSource(JdbcDrivers.DRIVER_CLASS_POSTGRESQL, url, "root", "root")
  //    ds should not be null
  //
  //    connectionTest(ds)
  //  }

  test("환경설정 파일을 읽어 MySQL 용 DataSource 생성") {
    val props = new Properties()
    props.load(getClass.getClassLoader.getResourceAsStream("hikari-mysql.properties"))

    val ds = DataSources.getDataSource(props)
    ds should not be null

    connectionTest(ds)
  }

  test("환경설정 파일을 읽어 H2 용 DataSource 생성") {
    val props = new Properties()
    props.load(getClass.getClassLoader.getResourceAsStream("hikari-h2.properties"))

    val ds = DataSources.getDataSource(props)
    ds should not be null

    connectionTest(ds)
  }

  test("Typesafe Config 설정을 읽어 DataSoource 생성") {
    val config = ConfigFactory.load("database")
    val databaseElem = new DatabaseElement(config.getConfig("database"))

    val ds = DataSources.getDataSource(databaseElem.dbSetting)
    ds should not be null

    connectionTest(ds)
  }


  def connectionTest(ds: DataSource) {
    ( 0 until 500 ).par.foreach { index =>
      val conn = ds.getConnection

      conn should not be null
      val ps = conn.prepareStatement("SELECT 1;")
      ps.execute() shouldEqual true

      ps.close()
      conn.close()
    }
  }

}