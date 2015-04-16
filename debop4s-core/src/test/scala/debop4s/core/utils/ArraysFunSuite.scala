package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

import scala.collection.mutable.ArrayBuffer

class ArraysFunSuite extends AbstractCoreFunSuite {

  test("iterable to array") {
    val buffer = new ArrayBuffer[Int](100)
    Range(0, 100).foreach {
      i => buffer += i
    }

    val array = Arrays.asArray[Int](buffer)

    array.length shouldEqual buffer.length
    array(0) shouldEqual 0
    array(50) shouldEqual 50
  }

  test("asArray") {
    val set = Set("a", "b", "c")
    val arr = Arrays.asArray[String](set)

    arr shouldEqual Seq("a", "b", "c")

    val arr2 = set.toArray
    arr shouldBe Array("a", "b", "c")
  }

}
