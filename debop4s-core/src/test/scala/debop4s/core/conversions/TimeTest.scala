package debop4s.core.conversions

import java.util.concurrent.TimeUnit
import org.scalatest.FunSuite
import scala.concurrent.duration.Duration

/**
 * TimeTest
 * @author Sunghyouk Bae
 */
//@RunWith(classOf[JUnitRunner])
class TimeTest extends FunSuite {

  import debop4s.core.conversions.time._

  test("converts Duration.Zero") {
    assert(0.toSeconds === Duration.Zero)
    assert(0.toMillis === Duration.Zero)
    assert(0.toSeconds === 0.toSeconds)
  }

  test("converts nonzero durations") {
    assert(1.toSeconds === Duration(1, TimeUnit.SECONDS))
    assert(123.toMillis === Duration(123, TimeUnit.MILLISECONDS))
  }

}
