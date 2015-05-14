package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.utils.Closer._

class StreamsFunSuite extends AbstractCoreFunSuite {

  test("read stream") {
    using(Resources.getClassPathResourceStream("logback-test.xml")) { is =>
      val xml = Streams.toString(is)

      xml should not be empty
      log.debug(s"xml=$xml")
    }
  }
}
