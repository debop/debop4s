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
  test("HikariCP create multiple datasources by promgramatic") {
    val ds =
      DataSources.getHikariDataSource(DATASOURCE_CLASS_MYSQL,
        "jdbc:mysql://localhost:3306/test",
        "sa",
        "",
        HashMap("characterEncoding" -> "UTF-8", "useUnicode" -> "true"))

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

  test("HikariCP create datasource from properties file") {

    val props = new Properties()
    props.load(getClass.getClassLoader.getResourceAsStream("hikari.properties"))
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
