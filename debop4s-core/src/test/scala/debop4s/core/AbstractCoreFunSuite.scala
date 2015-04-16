package debop4s.core

import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * AbstractCoreFunSuite
 * Created by debop on 2014. 2. 22.
 */
abstract class AbstractCoreFunSuite
  extends FunSuite with Matchers with BeforeAndAfter with OptionValues with Logging
