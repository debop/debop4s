package debop4s.core.stests.io

import debop4s.core.stests.AbstractCoreTest
import debop4s.core.io.Buff

/**
 * BufferTest
 * @author Sunghyouk Bae
 */
class BuffTest extends AbstractCoreTest {

  test("Buff.ByteArray.slice") {
    val arr = Array.range(0, 16).map(_.toByte)
    val buf = Buff.ByteArray(arr)
    for (i <- 0 until arr.length; j <- i until arr.length) {
      val w = new Array[Byte](j - i)
      buf.slice(i, j).write(w, 0)
      assert(w.toSeq === arr.slice(i, j).toSeq)
    }
  }

  test("Buff.concat") {
    val a1 = Array[Byte](1, 2, 3)
    val a2 = Array[Byte](4, 5, 6)

    val buff = Buff.ByteArray(a1) concat Buff.ByteArray(a2)
    assert(buff.length == 6)
    val x = Array.fill(6) {0.toByte}
    buff.write(x, 0)
    assert(x.toSeq == (a1 ++ a2).toSeq)
  }

  test("Buff.concat.slice") {
    val a1 = Array.range(0, 8).map(_.toByte)
    val a2 = Array.range(8, 16).map(_.toByte)
    val arr = a1 ++ a2
    val buff = Buff.ByteArray(a1) concat Buff.ByteArray(a2)

    for (i <- 0 until arr.length; j <- i until arr.length) {
      val w = new Array[Byte](j - i)
      buff.slice(i, j).write(w, 0)
      assert(w.toSeq == arr.slice(i, j).toSeq)
    }
  }

  test("Buff.Utf8: English") {
    val text = "Hello, world!"
    val buff = Buff.Utf8(text)
    assert(buff.length === text.length)

    val bytes = new Array[Byte](text.length)
    buff.write(bytes, 0)
    assert(text.toSeq === bytes.toSeq.map(_.toChar))

    // unapply 를 수행해서 buff 값을 s 에 저장합니다!!!
    val Buff.Utf8(s) = buff
    assert(s === text)
  }

  test("Buff.Utf8: Korean") {
    val text = "안녕하세요. 세계여!"
    val buff = Buff.Utf8(text)

    // unapply 를 수행해서 buff 값을 s 에 저장합니다!!!
    val Buff.Utf8(s) = buff
    assert(s === text)
  }

  test("Buff.Utf8.unapply with a Buff.ByteArray") {
    val str = "Hello, world!"
    val buff = Buff.Utf8(str)

    assert(Buff.Utf8.unapply(buff) === Some(str))
  }


}
