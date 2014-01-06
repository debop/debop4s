package kr.debop4s.core.stests.utils

import kr.debop4s.core.logging.Logger
import kr.debop4s.core.utils.Arrays
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.core.tests.tools.ArraysTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오전 12:04
 */
class ArraysTest extends AssertionsForJUnit {

    lazy val log = Logger[ArraysTest]

    @Test
    def iterableToArray() {
        val buffer = new ArrayBuffer[Int](100)
        Range(0, 100).foreach(i => buffer += i)
        val array = Arrays.asArray(buffer)

        assert(array.length == buffer.length)
        assert(array(0) == 0)
        assert(array(50) == 50)
    }

}
