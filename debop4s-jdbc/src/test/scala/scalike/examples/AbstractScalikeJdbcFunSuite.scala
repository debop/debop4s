package scalike.examples

import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}
import org.slf4j.LoggerFactory
import scalikejdbc._
import scalikejdbc.config.DBsWithEnv

/**
 * AbstractScalikeJdbcFunSuite
 * @author sunghyouk.bae@gmail.com
 */
abstract class AbstractScalikeJdbcFunSuite extends FunSuite with Matchers with BeforeAndAfter {

  lazy val log = LoggerFactory.getLogger(getClass)

  DBsWithEnv("develop").setupAll()

  implicit val session = AutoSession

}
