package debop4s.core.collections

import debop4s.core.AbstractCoreTest

/**
 * RingBufferTest
 * @author Sunghyouk Bae
 */
class RingBufferTest extends AbstractCoreTest {

    test("RingBuffer empty") {
        val buff = RingBuffer[String](4)
        assert(buff.length == 0)
        assert(buff.size == 0)
        assert(buff.isEmpty)
        intercept[IndexOutOfBoundsException] { buff(0) }
        intercept[NoSuchElementException] { buff.next }
        assert(!buff.iterator.hasNext)
    }

    test("single element") {
        val buf = RingBuffer[String](4)
        buf += "a"
        assert(buf.size == 1)
        assert(buf(0) == "a")
        assert(buf.toList === List("a"))
    }

    test("handle multiple element") {
        val buf = RingBuffer[String](4)
        buf ++= List("a", "b", "c")

        assert(buf.size == 3)
        assert(buf(0) == "a")
        assert(buf(1) == "b")
        assert(buf(2) == "c")
        assert(buf.toList === List("a", "b", "c"))

        assert(buf.next == "a")
        assert(buf.size == 2)
        assert(buf.next == "b")
        assert(buf.size == 1)
        assert(buf.next == "c")
        assert(buf.size == 0)

        intercept[NoSuchElementException] { buf.next }
    }

    test("handle overwrite/rollover") {
        val buf = RingBuffer[String](4)
        buf ++= List("a", "b", "c", "d", "e", "f")
        assert(buf.size == 4)
        assert(buf(0) == "c")
        assert(buf.toList === List("c", "d", "e", "f"))
    }

    test("removeWhere") {
        val buf = RingBuffer[Int](6)
        buf ++= (0 until 10)
        assert(buf.toList === List(4, 5, 6, 7, 8, 9))
        buf.removeWhere(_ % 3 == 0)
        assert(buf.toList === List(4, 5, 7, 8))
    }

}
