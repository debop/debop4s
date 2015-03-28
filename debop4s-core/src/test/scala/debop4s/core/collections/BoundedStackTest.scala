package debop4s.core.collections

import debop4s.core.AbstractCoreTest

/**
 * BoundedStackTest
 * @author Sunghyouk Bae
 */
class BoundedStackTest extends AbstractCoreTest {

  test("empty") {
    val buf = BoundedStack[String](Some(4))
    buf.length shouldEqual 0
    buf.size shouldEqual 0
    buf.isEmpty shouldEqual true
    intercept[IndexOutOfBoundsException] { buf(0) }
    intercept[NoSuchElementException] { buf.pop }
    buf.iterator.hasNext shouldEqual false
  }

  test("single element") {
    val buf = BoundedStack[String](Some(4))
    buf += "a"
    buf.size shouldEqual 1
    buf(0) shouldEqual "a"
    buf.toList shouldEqual List("a")
  }

  test("handle multiple element") {
    val buf = BoundedStack[String](Some(4))
    buf ++= List("a", "b", "c")
    buf.size shouldEqual 3
    buf(0) shouldEqual "c"
    buf(1) shouldEqual "b"
    buf(2) shouldEqual "a"
    buf.toList shouldEqual List("c", "b", "a")

    buf.pop shouldEqual "c"
    buf.size shouldEqual 2
    buf.pop shouldEqual "b"
    buf.size shouldEqual 1
    buf.pop shouldEqual "a"
    buf.size shouldEqual 0
  }

  test("handle overwrite/rollover") {
    val buf = BoundedStack[String](Some(4))
    buf ++= List("a", "b", "c", "d", "e", "f")
    buf.size shouldEqual 4
    buf(0) shouldEqual "f"
    buf.toList shouldEqual List("f", "e", "d", "c")
  }

  test("handle update") {
    val buf = BoundedStack[String](Some(4))
    buf ++= List("a", "b", "c", "d", "e", "f")
    for (i <- 0 until buf.size) {
      val old = buf(i)
      val updated = old + "2"
      buf(i) = updated
      buf(i) shouldEqual updated
    }
    buf.toList shouldEqual List("f2", "e2", "d2", "c2")
  }

  test("insert at 0 is same as +=") {
    val buf = BoundedStack[String](Some(3))

    buf.insert(0, "a")
    buf.size shouldEqual 1
    buf(0) shouldEqual "a"

    buf.insert(0, "b")
    buf.size shouldEqual 2
    buf(0) shouldEqual "b"
    buf(1) shouldEqual "a"

    buf.insert(0, "c")
    buf.size shouldEqual 3
    buf(0) shouldEqual "c"
    buf(1) shouldEqual "b"
    buf(2) shouldEqual "a"

    buf.insert(0, "d")
    buf.size shouldEqual 3
    buf(0) shouldEqual "d"
  }

  test("insert at count pushes to bottom") {
    val buf = BoundedStack[String](Some(3))
    buf.insert(0, "a")
    buf.insert(1, "b")
    buf.insert(2, "c")
    buf(0) shouldEqual "a"
    buf(1) shouldEqual "b"
    buf(2) shouldEqual "c"
    buf.toList shouldEqual List("a", "b", "c")
  }

  test("insert > count throws exception") {
    val buf = BoundedStack[String](Some(3))
    intercept[IndexOutOfBoundsException] { buf.insert(1, "a") }
    buf.insert(0, "a")
    intercept[IndexOutOfBoundsException] { buf.insert(2, "b") }
  }

}
