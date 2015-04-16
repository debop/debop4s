package debop4s.core.conversions

import java.util.concurrent.TimeUnit

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.time._

import scala.concurrent.duration.Duration

/**
 * TimeFunSuite
 * @author Sunghyouk Bae
 */
//@RunWith(classOf[JUnitRunner])
class TimeFunSuite extends AbstractCoreFunSuite {

  test("converts Duration.Zero") {
    0.seconds shouldEqual Duration.Zero
    0.milliseconds shouldEqual Duration.Zero
    0.seconds shouldEqual 0.seconds
  }

  test("converts nonzero durations") {
    1.seconds shouldEqual Duration(1, TimeUnit.SECONDS)
    123.milliseconds shouldEqual Duration(123, TimeUnit.MILLISECONDS)
  }

}
