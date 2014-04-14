package debop4s.core.utils

import debop4s.core.AbstractCoreTest
import java.io.InputStream

/**
 * ResourcesTest
 * @author Sunghyouk Bae
 */
class ResourcesTest extends AbstractCoreTest {

    test("load resources") {
        var is = None: Option[InputStream]
        try {
            is = Some(Resources.getClassPathResourceStream("globalization.xml"))
            assert(is != null)
            println(Streams.toString(is.get))
        } finally {
            if (is.isDefined) is.get.close()
        }
    }

    test("load resources by classLoader") {
        var is = None: Option[InputStream]
        try {
            is = Some(Resources.getClassPathResourceStream("globalization.xml", getClass.getClassLoader))
            assert(is != null)
            println(Streams.toString(is.get))
        } finally {
            if (is.isDefined) is.get.close()
        }
    }

}
