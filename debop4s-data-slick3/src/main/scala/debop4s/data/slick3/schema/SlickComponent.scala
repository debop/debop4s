package debop4s.data.slick3.schema

import debop4s.data.common.JdbcDrivers
import debop4s.data.slick3.SlickContext._
import org.slf4j.LoggerFactory
import slick.driver._

/**
 * Slick 사용 시 기본적으로 사용할 Database 용 trait 입니다.
 * {{{
 *    // 사용할 데이터베이스 정의
 *    object ApplicationDatabase extends SlickComponent {}
 *
 *    // 실제 DB 작업용 소스에서는 두개의 import 를 수행하고, withSession 구문을 사용해서 DB 작업을 정의합니다.
 *    import ApplicationDatabase._
 *    import ApplicationDatabase.driver.simple._
 *
 *    object Repository {
 *
 *      def get() = {
 *        withSession { implicit session =>
 *          // read data
 *        }
 *      }
 *    }
 * }}}
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait SlickComponent
  extends SlickQueryExtensions with SlickSchema with SlickProfile with SlickColumnMapper {

  protected val LOG = LoggerFactory.getLogger(getClass)

  def isMySQL: Boolean = ( driver == MySQLDriver ) || isMariaDB
  def isMariaDB: Boolean = jdbcDriver == JdbcDrivers.DRIVER_CLASS_MARIADB
  def isH2: Boolean = driver == H2Driver
  def isHsqlDB: Boolean = driver == HsqldbDriver
  def isPostgres: Boolean = driver == PostgresDriver
  def isSQLite: Boolean = driver == SQLiteDriver
  def isOracle: Boolean = driver.profile.toString.contains("OracleDriver")

}