package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import java.io.InputStream

/**
 * ResourcesFunSuite
 * @author Sunghyouk Bae
 */
class ResourcesFunSuite extends AbstractCoreFunSuite {

  test("load resources") {
    var is = None: Option[InputStream]
    try {
      is = Some(Resources.getClassPathResourceStream("globalization.xml"))
      is shouldBe defined
      println(Streams.toString(is.get))
    } finally {
      if (is.isDefined) is.get.close()
    }
  }

  test("load resources by classLoader") {
    var is = None: Option[InputStream]
    try {
      is = Some(Resources.getClassPathResourceStream("globalization.xml", getClass.getClassLoader))
      is shouldBe defined
      println(Streams.toString(is.get))
    } finally {
      if (is.isDefined) is.get.close()
    }
  }

}
