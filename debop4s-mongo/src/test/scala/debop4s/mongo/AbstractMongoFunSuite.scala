package debop4s.mongo

import debop4s.core.Logging
import org.scalatest._

abstract class AbstractMongoFunSuite
  extends FunSuite with Matchers with BeforeAndAfter with BeforeAndAfterAll with OptionValues with Logging
