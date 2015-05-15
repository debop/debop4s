package debop4s.core.concurrent

import java.io.EOFException

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.concurrent.Spool._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Promise }

/**
 * SpoolFunSuite
 * @author Sunghyouk Bae
 */
class SpoolFunSuite extends AbstractCoreFunSuite {

  val emptySpool = Spool.empty[Int]
  val simpleSpool = 1 **:: 2 **:: Spool.empty[Int]

  test("Empty Spool - iterate over all elements") {
    val xs = ArrayBuffer[Int]()
    emptySpool foreach { xs += _ }
    xs.size shouldEqual 0
  }

  test("Empty Spool - map") {
    assert((emptySpool map { x => x * 2 }) == Spool.empty[Int])
  }

  test("Empty Spool - deconstruct") {
    emptySpool match {
      case x **:: rest => fail()
      case _ =>
    }
  }

  test("Empty Spool - append via ++") {
    assert((emptySpool ++ Spool.empty[Int]) == Spool.empty[Int])
    assert((Spool.empty[Int] ++ emptySpool) == Spool.empty[Int])

    val s2 = emptySpool ++ (3 **:: 4 **:: Spool.empty[Int])
    Asyncs.result(s2.toSeq) shouldEqual Seq(3, 4)
  }

  test("Empty Spool - append via ++ with Future rhs") {
    Asyncs.result(emptySpool ++# Future(Spool.empty[Int])) shouldEqual Spool.empty[Int]
    Asyncs.result(Spool.empty[Int] ++# Future(emptySpool)) shouldEqual Spool.empty[Int]

    val s2 = emptySpool ++# Future(3 **:: 4 **:: Spool.empty[Int])
    Asyncs.result(s2 flatMap (_.toSeq)) shouldEqual Seq(3, 4)
  }

  test("Empty Spool - fold left") {
    val fold = emptySpool.foldLeft(0) { (x, y) => x + y }
    Asyncs.result(fold) shouldEqual 0
  }

  test("Empty Spool - reduce left") {
    val fold = emptySpool.reduceLeft { (x, y) => x - y }
    intercept[UnsupportedOperationException] {
      Asyncs.result(fold)
    }
  }

  test("Simple Spool - iterate over all elements") {
    val xs = ArrayBuffer[Int]()
    simpleSpool.foreach { xs += _ }
    Thread.sleep(10)
    xs shouldEqual Seq(1, 2)
  }

  test("Simple Spool - buffer to a sequence") {
    Asyncs.result(simpleSpool.toSeq) shouldEqual Seq(1, 2)
  }

  test("Simple Spool - map") {
    Asyncs.result(simpleSpool map { _ * 2 } toSeq) shouldEqual Seq(2, 4)
  }

  test("Simple Spool - deconstruct") {
    simpleSpool match {
      case x **:: rest =>
        assert(x === 1)
        log.debug(s"rest=$rest")
        rest match {
          case y **:: rest2 =>
            assert(y == 2 && rest2.isEmpty)
          case _ => fail("not spool")
        }
    }
  }

  test("Simple Spool - append via ++") {
    Asyncs.result((simpleSpool ++ Spool.empty[Int]).toSeq) shouldEqual Seq(1, 2)
    Asyncs.result((Spool.empty[Int] ++ simpleSpool).toSeq) shouldEqual Seq(1, 2)

    val s2 = simpleSpool ++ (3 **:: 4 **:: Spool.empty[Int])
    Asyncs.result(s2.toSeq) shouldEqual Seq(1, 2, 3, 4)
  }

  test("Simple Spool - append via ++ with Future rhs") {
    Asyncs.result(simpleSpool ++# Future(Spool.empty[Int]) flatMap (_.toSeq)) shouldEqual Seq(1, 2)
    Asyncs.result(Spool.empty[Int] ++# Future(simpleSpool) flatMap (_.toSeq)) shouldEqual Seq(1, 2)

    val s2 = simpleSpool ++# Future(3 **:: 4 **:: Spool.empty[Int])
    Asyncs.result(s2 flatMap (_.toSeq)) shouldEqual Seq(1, 2, 3, 4)
  }

  test("Simple Spool - flatMap") {
    val f = (x: Int) => Future(x.toString **:: (x * 2).toString **:: Spool.empty[String])
    val s2 = simpleSpool flatMap f
    Asyncs.result(s2 flatMap (_.toSeq)) shouldEqual Seq("1", "2", "2", "4")
  }

  test("Simple Spool fold left") {
    val fold = simpleSpool.foldLeft(0) { (x, y) => x + y }
    Asyncs.result(fold) shouldEqual 3
  }

  test("Simple Spool reduce left") {
    val fold = simpleSpool.reduceLeft { (x, y) => x + y }
    Asyncs.result(fold) shouldEqual 3
  }

  test("be roundtrippable through toSeq/toSpool") {
    val seq = (0 to 10).seq
    Asyncs.result(seq.toSpool.toSeq) shouldEqual seq
  }

  test("Simple Spool flatten via flatMap of toSpool") {
    val spool = Seq(1, 2) **:: Seq(3, 4) **:: Spool.empty[Seq[Int]]
    val seq: Seq[Seq[Int]] = Asyncs.result(spool.toSeq)

    val flatSpool = spool.flatMap { inner => Future(inner.toSpool) }
    Asyncs.result(flatSpool.flatMap(_.toSeq)) shouldEqual seq.flatten
  }

  test("Simple resolved spool with EOFException") {

    val p = Future.failed[Spool[Int]](new EOFException("sad panda"))
    val s = 1 **:: 2 *:: p

    val xs = new ArrayBuffer[Option[Int]]
    s foreachElem { xs += _ }
    // xs shouldBe ArrayBuffer(Some(1), Some(2), None)
    xs should contain allOf(Some(1), Some(2), None)

    val f = s foreach { _ => throw new Exception("sad panda") }
    intercept[Exception] {
      Asyncs.result(f)
    }

    val f2 = s foreach { _ => throw new EOFException("sad panda") }
    intercept[EOFException] {
      Asyncs.result(f2)
    }
  }

  test("Simple delayed Spool") {
    val p = Promise[Spool[Int]]()
    val p1 = Promise[Spool[Int]]()

    val s = 1 *:: p.future

    // iterate as results become available
    val xs = new ArrayBuffer[Int]
    s foreach { xs += _ }
    Thread.sleep(1)
    xs shouldEqual ArrayBuffer(1)

    p success (2 *:: p1.future)
    Thread.sleep(1)
    xs shouldEqual ArrayBuffer(1, 2)

    p1 success Spool.empty
    Thread.sleep(1)
    xs shouldEqual ArrayBuffer(1, 2)
  }

  test("EOF iteration on EOFException") {
    val p = Promise[Spool[Int]]()

    val s = 1 *:: p.future

    val xs = ArrayBuffer[Option[Int]]()
    s foreachElem { xs += _ }
    Thread.sleep(1)
    xs shouldEqual ArrayBuffer(Some(1))

    p failure new EOFException("sad panda")
    Thread.sleep(1)
    xs shouldEqual ArrayBuffer(Some(1), None)
  }

  test("return with exception on error") {
    val p = Promise[Spool[Int]]()

    val s = 1 *:: p.future

    val xs = ArrayBuffer[Option[Int]]()
    s foreachElem { xs += _ }
    xs should contain only Some(1)

    p failure new Exception("sad panda")
    intercept[Exception] {
      Asyncs.result(s.toSeq)
    }
  }

  test("return with exception on error in callback") {
    val p = Promise[Spool[Int]]()
    val p1 = Promise[Spool[Int]]()

    val s = 1 *:: p.future

    val f = s foreach { _ => throw new Exception("sad panda") }
    p success (2 *:: p1.future)
    intercept[Exception] { Asyncs.result(f) }
  }

  test("return with exception on EOFException in callback") {
    val p = Promise[Spool[Int]]()
    val p1 = Promise[Spool[Int]]()

    val s = 1 *:: p.future

    val f = s foreach { _ => throw new EOFException("sad panda") }
    p success (2 *:: p1.future)
    intercept[EOFException] { Asyncs.result(f) }
  }

  test("return a buffered seq when complete") {
    val p = Promise[Spool[Int]]()
    val p1 = Promise[Spool[Int]]()
    val s = 1 *:: p.future

    val f = s.toSeq
    f.value.isDefined shouldEqual false

    p success 2 *:: p1.future
    f.value.isDefined shouldEqual false

    p1 success Spool.empty[Int]
    f.value.isDefined shouldEqual false

    Asyncs.result(f) shouldEqual Seq(1, 2)
    f.value.isDefined shouldEqual true
  }

  test("deconstruct") {
    val p = Promise[Spool[Int]]()
    val s = 1 *:: p.future

    s match {
      case fst *:: rest => assert(fst == 1 && !rest.isCompleted)
      case _ => fail("not spool")
    }
  }

  test("collect") {
    val p = Promise[Spool[Int]]()
    val p1 = Promise[Spool[Int]]()
    val p2 = Promise[Spool[Int]]()
    val s = 1 *:: p.future

    val f = s collect {
      case x if x % 2 == 0 => x * 2
    }

    f.isCompleted shouldEqual false

    p success 2 *:: p1.future

    val s1 = Asyncs.result(f)
    s1 match {
      case x *:: rest => assert(x == 4 && !rest.isCompleted)
      case _ => fail("s1")
    }

    p1 success 3 *:: p2.future

    s1 match {
      case x *:: rest => assert(x == 4 && !rest.isCompleted)
      case _ => fail("s1")
    }

    p2 success 4 **:: Spool.empty[Int]

    val sls = s1.toSeq
    Asyncs.result(sls) shouldEqual Seq(4, 8)
  }

  test("fold left") {
    val p = Promise[Spool[Int]]()
    val p1 = Promise[Spool[Int]]()

    val s = 1 *:: p.future

    val f = s.foldLeft(0) { (x, y) => x + y }

    p success (2 *:: p1.future)
    p1 success Spool.empty[Int]

    Asyncs.result(f) shouldEqual 3
  }

  test("be lazy") {
    def mkSpool(i: Int = 0): Future[Spool[Int]] = {
      Future {
        if (i < 3)
          i *:: mkSpool(i + 1)
        else
          throw new AssertionError("Should not have produced " + i)
      }
    }
    mkSpool()
  }
}

