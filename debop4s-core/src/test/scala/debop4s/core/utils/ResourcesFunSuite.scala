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
    //    var is = None: Option[InputStream]
    //    try {
    //      is = Some(Resources.getClassPathResourceStream("globalization.xml"))
    //      is shouldBe defined
    //      println(Streams.toString(is.get))
    //    } finally {
    //      if (is.isDefined) is.get.close()
    //    }
  }

  test("load resources by classLoader") {
    using(Resources.getClassPathResourceStream("globalization.xml", getClass.getClassLoader)) { is =>
      is should not be null
      println(Streams.toString(is))
    }
    //    var is = None: Option[InputStream]
    //    try {
    //      is = Some(Resources.getClassPathResourceStream("globalization.xml", getClass.getClassLoader))
    //      is shouldBe defined
    //      println(Streams.toString(is.get))
    //    } finally {
    //      if (is.isDefined) is.get.close()
    //    }
  }

}
