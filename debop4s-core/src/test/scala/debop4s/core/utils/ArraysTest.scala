package debop4s.core.utils

import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import org.slf4j.LoggerFactory
import scala.collection.mutable.ArrayBuffer

/**
 * debop4s.core.tests.tools.ArraysTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오전 12:04
 */
class ArraysTest extends FunSuite with Matchers with BeforeAndAfter {

  lazy val log = LoggerFactory.getLogger(getClass)

  test("iterable to array") {
    val buffer = new ArrayBuffer[Int](100)
    Range(0, 100).foreach {
      i => buffer += i
    }

    val array = Arrays.asArray[Int](buffer)

    assert(array.length == buffer.length)
    assert(array(0) == 0)
    assert(array(50) == 50)
  }

  test("asArray") {
    val set = Set("a", "b", "c")
    val arr = Arrays.asArray[String](set)

    assert(arr.sameElements(Array("a", "b", "c")))

    val arr2 = set.toArray
    assert(arr2.sameElements(Array("a", "b", "c")))
  }

}
