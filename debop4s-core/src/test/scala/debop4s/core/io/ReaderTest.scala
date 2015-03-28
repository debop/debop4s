package debop4s.core.io

import debop4s.core.AbstractCoreTest
import debop4s.core.concurrent.Asyncs

/**
 * ReaderTest
 * @author Sunghyouk Bae
 */
class ReaderTest extends AbstractCoreTest {

  def toArr(start: Int, end: Int) = Array.range(start, end).map(_.toByte)
  def toBuff(start: Int, end: Int) = Buff.ByteArray(toArr(start, end))
  def toSeq(b: Buff) = b.toArray.toSeq

  def assertRead(r: Reader, start: Int, end: Int) {
    val n = end - start
    val f = r.read(n)
    val b = Asyncs.result(f)
    assert(toSeq(b) === Seq.range(start, end))
  }

  def assertWrite(w: Writer, start: Int, end: Int) {
    val buff = toBuff(start, end)
    val f = w.write(buff)
    assert(Asyncs.result(f) ===())
  }

  def assertWriteEof(w: Writer) {
    val f = w.write(Buff.Eof)
    assert(Asyncs.result(f) ===())
  }

  test("Reader.writable") {
    val rw = Reader.writable()
    val wf = rw.write(toBuff(0, 6))
    assert(!wf.value.isDefined)
    assertRead(rw, 0, 3)
    assert(!wf.value.isDefined)
    assertRead(rw, 3, 6)
    assert(wf.value.isDefined)
  }

  test("Reader.readAll") {
    val rw = Reader.writable()
    val all = Reader.readAll(rw)

    assert(!all.value.isDefined)
    assertWrite(rw, 0, 3)
    assertWrite(rw, 3, 6)
    assertWriteEof(rw)

    val buf = Asyncs.result(all)
    assert(toSeq(buf) === Seq.range(0, 6))
  }

  test("Reader.writable - write before read") {
    val rw = Reader.writable()
    val wf = rw.write(toBuff(0, 6))
    val rf = rw.read(6)

    assert(toSeq(Asyncs.result(rf)) === Seq.range(0, 6))
  }

  test("Reader.writable - partial read, then short read") {
    val rw = Reader.writable()
    val wf = rw.write(toBuff(0, 6))

    val rf = rw.read(4)
    assert(toSeq(Asyncs.result(rf)) === Seq.range(0, 4))

    val rf2 = rw.read(4)
    assert(toSeq(Asyncs.result(rf2)) === Seq.range(4, 6))

    assert(Asyncs.result(wf) ===())
  }

  test("Reader.writable - fail while reading") {
    val rw = Reader.writable()
    val rf = rw.read(6)
    val exc = new Exception
    rw.fail(exc)
    val exc1 = intercept[Exception] { Asyncs.result(rf) }
    assert(exc == exc1)
  }

  test("Reader.writable - fail before reading") {
    val rw = Reader.writable()
    rw.fail(new Exception)
    val rf = rw.read(6)

    intercept[Exception] { Asyncs.result(rf) }
  }

  test("Reader.writable - discard") {
    val rw = Reader.writable()
    rw.discard()
    val rf = rw.read(10)

    intercept[Reader.ReaderDiscarded] { Asyncs.result(rf) }
  }

}
