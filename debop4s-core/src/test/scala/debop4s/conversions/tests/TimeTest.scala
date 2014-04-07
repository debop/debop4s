package debop4s.conversions.tests

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

/**
 * TimeTest
 * @author Sunghyouk Bae
 */
@RunWith(classOf[JUnitRunner])
class TimeTest extends FunSuite {

  import debop4s.conversions.time._

  test("converts Duration.Zero") {
    assert(0.seconds eq Duration.Zero)
    assert(0.milliseconds eq Duration.Zero)
    assert(0.seconds eq 0.seconds)
  }

  test("converts nonzero durations") {
    assert(1.seconds == Duration(1, TimeUnit.SECONDS))
    assert(123.milliseconds == Duration(123, TimeUnit.MILLISECONDS))
  }

}
