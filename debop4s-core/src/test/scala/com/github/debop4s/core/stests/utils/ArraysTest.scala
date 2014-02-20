package com.github.debop4s.core.stests.utils

import com.github.debop4s.core.utils.Arrays
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import org.slf4j.LoggerFactory
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.core.tests.tools.ArraysTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오전 12:04
 */
class ArraysTest extends FunSuite with Matchers with BeforeAndAfter {

    lazy val log = LoggerFactory.getLogger(getClass)

    test("iterable to array") {
        val buffer = new ArrayBuffer[Int](100)
        Range(0, 100).foreach(i => buffer += i)
        val array = Arrays.asArray[Int](buffer)

        assert(array.length == buffer.length)
        assert(array(0) == 0)
        assert(array(50) == 50)
    }

}
