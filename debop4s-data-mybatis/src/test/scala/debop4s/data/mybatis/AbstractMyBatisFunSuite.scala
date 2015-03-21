package debop4s.data.mybatis

import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * AbstractMyBatisFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
abstract class AbstractMyBatisFunSuite
  extends FunSuite
  with Matchers with OptionValues with BeforeAndAfter with BeforeAndAfterAll
  with DatabaseSupport {

  lazy val LOG = LoggerFactory.getLogger(getClass)


}
