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
        assert(0.seconds === Duration.Zero)
        assert(0.milliseconds === Duration.Zero)
        assert(0.seconds === 0.seconds)
    }

    test("converts nonzero durations") {
        assert(1.seconds === Duration(1, TimeUnit.SECONDS))
        assert(123.milliseconds === Duration(123, TimeUnit.MILLISECONDS))
    }

}
