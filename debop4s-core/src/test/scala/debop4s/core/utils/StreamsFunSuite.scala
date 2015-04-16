package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

/**
 * StreamsFunSuite
 * @author Sunghyouk Bae
 */
class StreamsFunSuite extends AbstractCoreFunSuite {

  test("read stream") {
    val is = Resources.getClassPathResourceStream("logback-test.xml")
    val xml = Streams.toString(is)

    xml should not be empty
    log.debug(s"xml=$xml")
  }
}
