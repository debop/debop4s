package scalike.examples.hello

import org.scalatest.BeforeAndAfterAll
import scalike.examples.AbstractScalikeJdbcFunSuite
import scalike.examples.hello.setup.DBInitializer
import scalikejdbc._

/**
 * HelloExampleFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class HelloExampleFunSuite extends AbstractScalikeJdbcFunSuite with BeforeAndAfterAll {

  override def beforeAll() {
    DBInitializer.run()
  }

  test("verify initialize") {
    val id = sql"select id from programmer limit 1".map(_.long("id")).single().apply()
    id.getOrElse(0L) shouldEqual 1
  }

}
