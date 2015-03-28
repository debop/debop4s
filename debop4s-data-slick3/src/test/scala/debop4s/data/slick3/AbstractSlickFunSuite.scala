package debop4s.data.slick3

import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * AbstractSlickFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 28.
 */
abstract class AbstractSlickFunSuite
  extends FunSuite with Matchers with OptionValues with BeforeAndAfter with BeforeAndAfterAll {

  protected lazy val LOG = LoggerFactory.getLogger(getClass)

}
