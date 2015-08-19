package debop4s.core.utils


import java.io.InputStream

import debop4s.core.{ AbstractCoreFunSuite, _ }

/**
 * ResourcesFunSuite
 * @author Sunghyouk Bae
 */
class ResourcesFunSuite extends AbstractCoreFunSuite {

  test("load resources") {
    using(Resources.getClassPathResourceStream("globalization.xml")) { is =>
      is should not be null
      println(Streams.toString(is))
    }
  }

  test("load resources by classLoader") {
    using(Resources.getClassPathResourceStream("globalization.xml", getClass.getClassLoader)) { is =>
      is should not be null
      println(Streams.toString(is))
    }
  }

}
