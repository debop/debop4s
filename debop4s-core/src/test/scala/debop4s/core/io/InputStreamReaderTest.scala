package debop4s.core.io

import debop4s.core.AbstractCoreTest
import debop4s.core.parallels.Asyncs
import java.io.ByteArrayInputStream

/**
 * InputStreamReaderTest
 * @author Sunghyouk Bae
 */
class InputStreamReaderTest extends AbstractCoreTest {

  def toArr(start: Int, end: Int) = Array.range(start, end).map(_.toByte)
  def toBuff(start: Int, end: Int) = Buff.ByteArray(toArr(start, end))
  def toSeq(b: Buff) = b.toArray.toSeq

  test("read Buf.Eof") {
    val a = Array.empty[Byte]
    val s = new ByteArrayInputStream(a)
    val r = new InputStreamReader(s, 4096)

    val f = r.read(10)
    assert(Asyncs.result(f) === Buff.Eof)
  }

  test("read 0 bytes") {
    val a = toArr(0, 25)
    val s = new ByteArrayInputStream(a)
    val r = InputStreamReader(s)

    val f = r.read(0)
    assert(Asyncs.result(f) === Buff.Empty)
  }

  test("read positive bytes") {
    val a = toArr(0, 25)
    val s = new ByteArrayInputStream(a)
    val r = InputStreamReader(s)

    val f1 = r.read(10)
    assert(Asyncs.result(f1) === toBuff(0, 10))

    val f2 = r.read(10)
    assert(Asyncs.result(f2) === toBuff(10, 20))

    val f3 = r.read(10)
    a
    assert(Asyncs.result(f3) === toBuff(20, 25))

    val f4 = r.read(10)
    assert(Asyncs.result(f4) === Buff.Eof)
  }

  test("read up to maxBufferSize") {
    val a = toArr(0, 250)
    val s = new ByteArrayInputStream(a)
    val r = InputStreamReader(s, 100)

    val f1 = r.read(1000)
    assert(Asyncs.result(f1) === toBuff(0, 100))

    val f2 = r.read(1000)
    assert(Asyncs.result(f2) === toBuff(100, 200))

    val f3 = r.read(1000)
    assert(Asyncs.result(f3) === toBuff(200, 250))

    val f4 = r.read(1000)
    assert(Asyncs.result(f4) === Buff.Eof)
  }

  test("read all") {
    val a = toArr(0, 250)
    val s1 = new ByteArrayInputStream(a)
    val s2 = new ByteArrayInputStream(a)
    val r1 = InputStreamReader(s1, 100)
    val r2 = InputStreamReader(s2, 500)

    val f1 = Reader.readAll(r1)
    assert(Asyncs.result(f1) === toBuff(0, 250))

    val f2 = Reader.readAll(r1)
    assert(Asyncs.result(f2) === Buff.Eof)

    val f3 = Reader.readAll(r2)
    assert(Asyncs.result(f3) === toBuff(0, 250))

    val f4 = Reader.readAll(r2)
    assert(Asyncs.result(f4) === Buff.Eof)
  }

  test("discard") {
    val a = toArr(0, 25)
    val s = new ByteArrayInputStream(a)
    val r = InputStreamReader(s)

    val f1 = r.read(10)
    assert(Asyncs.result(f1) === toBuff(0, 10))

    r.discard()

    val f2 = r.read(10)
    intercept[Reader.ReaderDiscarded] {
      Asyncs.result(f2)
    }
  }
}
