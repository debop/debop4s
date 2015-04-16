package debop4s.core.io

import debop4s.core._
import debop4s.core.concurrent._

/**
 * ReaderFunSuite
 * @author Sunghyouk Bae
 */
class ReaderFunSuite extends AbstractCoreFunSuite {

  def toArr(start: Int, end: Int) = Array.range(start, end).map(_.toByte)
  def toBuff(start: Int, end: Int) = Buff.ByteArray(toArr(start, end))
  def toSeq(b: Buff) = b.toArray.toSeq

  def assertRead(r: Reader, start: Int, end: Int) {
    val n = end - start
    val f = r.read(n)
    val b = Asyncs.result(f)
    f.await.toArray.toSeq shouldEqual Seq.range(start, end)
  }

  def assertWrite(w: Writer, start: Int, end: Int) {
    val buff = toBuff(start, end)
    val f = w.write(buff)
    f.await shouldEqual {}
  }

  def assertWriteEof(w: Writer) {
    val f = w.write(Buff.Eof)
    f.await shouldEqual {}
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

    all.await.toArray.toSeq shouldEqual Seq.range(0, 6)
  }

  test("Reader.writable - write before read") {
    val rw = Reader.writable()
    val wf = rw.write(toBuff(0, 6))
    val rf = rw.read(6)

    rf.await.toArray.toSeq shouldEqual Seq.range(0, 6)
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

    intercept[Exception] { rf.await }
  }

  test("Reader.writable - discard") {
    val rw = Reader.writable()
    rw.discard()
    val rf = rw.read(10)

    intercept[Reader.ReaderDiscarded] { rf.await }
  }

}
