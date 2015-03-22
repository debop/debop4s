package debop4s.data.slick.databases

import debop4s.config.server.DatabaseSetting
import org.slf4j.LoggerFactory

import scala.slick.driver.JdbcDriver
import scala.slick.jdbc.GetResult._
import scala.slick.jdbc.{ ResultSetInvoker, StaticQuery }
import scala.slick.profile._
import scala.util.control.NonFatal

trait DB {
  type Driver <: BasicDriver

  /** Slick driver for the database */
  val driver: Driver

  /** The Slick driver profile for the database */
  lazy val profile: driver.profile.type = driver.asInstanceOf[driver.profile.type]

  /** Indicates whether the database persists after closing the last connection */
  def isPersistent = true
}

trait RelationalDB extends DB {
  type Driver <: RelationalDriver
}

trait SqlDB extends RelationalDB {
  type Driver <: SqlDriver
}

abstract class JdbcDB(val dbSetting: DatabaseSetting) extends SqlDB {

  private val LOG = LoggerFactory.getLogger(getClass)

  type Driver = JdbcDriver

  lazy val database = profile.backend.Database

  val jdbcDriver: String = dbSetting.driverClass

  def getLocalTables(implicit session: profile.Backend#Session): List[String] = {
    val tables = ResultSetInvoker[(String, String, String, String)](_.conn.getMetaData.getTables("", "", null, null))
    tables.list.filter(_._4.toUpperCase == "TABLE").map(_._3).sorted
  }

  def getLocalSequences(implicit session: profile.Backend#Session): List[String] = {
    val tables = ResultSetInvoker[(String, String, String, String)](_.conn.getMetaData.getTables("", "", null, null))
    tables.list.filter(_._4.toUpperCase == "SEQUENCE").map(_._3).sorted
  }

  def dropUserArtifacts(implicit session: profile.Backend#Session): Unit = {
    getLocalTables.foreach { table =>
      ( StaticQuery.u + s"drop table if exists ${ driver.quoteIdentifier(table) } cascade" ).execute
    }
    getLocalSequences.foreach { seq =>
      ( StaticQuery.u + s"drop sequence if exists ${ driver.quoteIdentifier(seq) } cascade" ).execute
    }
  }

  def assertTableExists(tables: String*)(implicit session: profile.Backend#Session): Unit = {
    tables.foreach { table =>
      try {
        ( StaticQuery[Int] + s"select 1 from ${ driver.quoteIdentifier(table) } where 1 > 0" ).list
      } catch {
        case NonFatal(e) => sys.error(s"Table not found. table name=$table")
      }
    }
  }

  def assertTableNotExists(tables: String*)(implicit session: profile.Backend#Session): Unit = {
    tables.foreach { table =>
      try {
        ( StaticQuery[Int] + s"select 1 from ${ driver.quoteIdentifier(table) } where 1 < 0" ).list
        sys.error(s"Table $table should not exists.")
      } catch {
        case NonFatal(e) =>
      }
    }
  }

  def canGetLocalTable = true
}

abstract class InternalJdbcDB(dbSetting: DatabaseSetting) extends JdbcDB(dbSetting) {
  val url: String = dbSetting.url
  override def toString = url
}

abstract class ExternalJdbcDB(dbSetting: DatabaseSetting) extends JdbcDB(dbSetting) {

  val url = dbSetting.url
  override def toString = url
}