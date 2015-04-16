package debop4s.core

import debop4s.core.concurrent.Asyncs
import java.util.concurrent.atomic.AtomicReference
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * EventFunSuite
 * Created by debop on 2014. 4. 16.
 */
class EventFunSuite extends AbstractCoreFunSuite {

  test("pub/sub while active") {
    val e = Event[Int]()
    val ref = new AtomicReference[Seq[Int]](Seq.empty)
    val sub = e.build.register(Witness(ref))

    ref.get shouldEqual Seq.empty
    e.notify(1)
    ref.get shouldEqual Seq(1)
    e.notify(2)
    ref.get shouldEqual Seq(1, 2)

    Asyncs.ready(sub.close())
    e.notify(3)
    ref.get shouldEqual Seq(1, 2)
  }

  test("Event.collect") {
    val e = Event[Int]()
    val events = e collect { case i if i % 2 == 0 => i * 2 }
    val ref = new AtomicReference[Seq[Int]](Seq.empty)
    events.build.register(Witness(ref))

    e.notify(1)
    ref.get shouldEqual Seq.empty
    e.notify(2)
    ref.get shouldEqual Seq(4)
    e.notify(3)
    e.notify(4)
    ref.get shouldEqual Seq(4, 8)
  }

  test("Event.foldLeft") {
    val e = Event[Int]()
    val sum = e.foldLeft(0)(_ + _)
    val ref = new AtomicReference[Int](0)
    sum.register(Witness(ref))
    e.notify(0)
    ref.get shouldEqual 0
    e.notify(1)
    ref.get shouldEqual 1
    e.notify(2)
    ref.get shouldEqual 3
    e.notify(3)
    ref.get shouldEqual 6
    e.notify(10)
    ref.get shouldEqual 16
  }

  test("Event.sliding") {
    val e = Event[Int]()
    val w = e.sliding(3)
    val ref = new AtomicReference[Seq[Int]](Seq.empty)
    w.register(Witness(ref))

    e.notify(1)
    ref.get shouldEqual Seq(1)
    e.notify(2)
    ref.get shouldEqual Seq(1, 2)
    e.notify(3)
    ref.get shouldEqual Seq(1, 2, 3)
    e.notify(4)
    ref.get shouldEqual Seq(2, 3, 4)
  }

  test("Event.mergeMap") {
    val e = Event[Int]()
    val inners = ArrayBuffer[Witness[String]]()
    val e2 = e mergeMap { i =>
      val e = Event[String]()
      inners += e
      e
    }
    val ref = new AtomicReference[String]("")
    val closable = e2.register(Witness(ref))

    inners.isEmpty shouldEqual true

    e.notify(1)
    inners.size shouldEqual 1
    ref.get shouldEqual ""
    inners(0).notify("okay")
    ref.get shouldEqual "okay"

    e.notify(2)
    inners.size shouldEqual 2
    ref.get shouldEqual "okay"
    inners(0).notify("notokay")
    ref.get shouldEqual "notokay"
    inners(1).notify("yay")
    ref.get shouldEqual "yay"
  }

  test("Event.mergeMap closes constituent witnesses") {
    @volatile var n = 0

    val e1, e2 = new Event[Int] {
      def register(w: Witness[Int]) = {
        n += 1
        w.notify(1)
        Closable.make { _ => n -= 1; Future() }
      }
    }

    val e12 = e1 mergeMap { _ => e2 }

    val ref = new AtomicReference(Seq.empty[Int])
    val closable = e12.build.register(Witness(ref))

    ref.get shouldEqual Seq(1)
    n shouldEqual 2
    Asyncs.ready(closable.close())
    n shouldEqual 0
  }

  test("Event.select") {
    val e1 = Event[Int]()
    val e2 = Event[String]()
    val e = e1 select e2
    val ref = new AtomicReference[Seq[Either[Int, String]]](Seq.empty)
    e.build.register(Witness(ref))
    assert(ref.get.isEmpty)

    e1.notify(1)
    e1.notify(2)
    e2.notify("1")
    e1.notify(3)
    e2.notify("2")

    ref.get shouldEqual Seq(Left(1), Left(2), Right("1"), Left(3), Right("2"))
  }

  test("Event.zip") {
    val e1 = Event[Int]()
    val e2 = Event[String]()
    val e = e1 zip e2
    val ref = new AtomicReference[Seq[(Int, String)]](Seq.empty)
    e.build.register(Witness(ref))
    assert(ref.get.isEmpty)

    ( 0 until 50 ) foreach { i => e1.notify(i) }
    ( 0 until 50 ) foreach { i => e2.notify(i.toString) }
    ( 50 until 100 ) foreach { i => e2.notify(i.toString) }
    ( 50 until 100 ) foreach { i => e1.notify(i) }

    ref.get shouldEqual ( ( 0 until 100 ) zip ( ( 0 until 100 ) map ( _.toString ) ) )
  }

  test("Event.joinLast") {
    val e1 = Event[Int]()
    val e2 = Event[String]()
    val e = e1 joinLast e2
    val ref = new AtomicReference[(Int, String)]((0, ""))
    e.register(Witness(ref))

    ref.get shouldEqual(0, "")
    e1.notify(1)
    ref.get shouldEqual(0, "")
    e2.notify("ok")
    ref.get shouldEqual(1, "ok")
    e2.notify("ok1")
    ref.get shouldEqual(1, "ok1")
    e1.notify(2)
    ref.get shouldEqual(2, "ok1")
  }

  test("Event.take") {
    val e = Event[Int]()
    val e1 = e.take(5)
    val ref = new AtomicReference(Seq.empty[Int])
    e1.build.register(Witness(ref))

    e.notify(1)
    e.notify(2)
    ref.get shouldEqual Seq(1, 2)
    e.notify(3)
    e.notify(4)
    e.notify(5)
    ref.get shouldEqual Seq(1, 2, 3, 4, 5)
    e.notify(6)
    e.notify(7)
    ref.get shouldEqual Seq(1, 2, 3, 4, 5)
  }

  test("Event.merge") {
    val e1, e2 = Event[Int]()
    val e = e1 merge e2
    val ref = new AtomicReference(Seq.empty[Int])
    e.build.register(Witness(ref))

    for (i <- 0 until 100) e1.notify(i)
    for (i <- 100 until 200) e2.notify(i)
    for (i <- 200 until 300) {
      if (i % 2 == 0) e1.notify(i)
      else e2.notify(i)
    }

    ref.get shouldEqual Seq.range(0, 300)
  }

  test("Event.toVar") {
    // TODO: Implement Var
    //        val e = Event[Int]()
    //        val v = Var(0, e)
    //
    //        val ref = new AtomicReference[Seq[Int]](Seq.empty)
    //        v.changes.build.register(Witness(ref))
    //
    //        for (i <- 1 until 100) e.notify(i)
    //        assert(ref.get === Seq.range(0, 100))
  }

  test("Event.toFuture") {
    val e = Event[Int]()
    val f = e.toFuture

    f.value.isDefined shouldEqual false
    e.notify(123)

    f.value.isDefined shouldEqual true
    Asyncs.result(f) shouldEqual 123
  }

  test("Jake's composition test") {

    // TODO: Implement Var

    //        def sum(v: Var[Int]): Var[Int] = {
    //            val e = v.changes.foldLeft(0) (_+_)
    //            Var(0, e)
    //        }
    //
    //        def ite[T](i: Var[Boolean], t: Var[T], e: Var[T]) =
    //            i flatMap { i => if (i) t else e }
    //
    //        val b = Var(true)
    //        val x = Var(7)
    //        val y = Var(9)
    //
    //        val z = ite(b, sum(x), sum(y))
    //
    //        val ref = new AtomicReference[Int]
    //        z.changes.register(Witness(ref))
    //
    //        assert(ref.get === 7)
    //        x() = 10
    //        assert(ref.get === 17)
    //        b() = false
    //        assert(ref.get === 9)
    //        y() = 10
    //        assert(ref.get === 19)
    //        b() = true
    //        assert(ref.get === 17)
    //        x() = 3
    //        assert(ref.get === 20)
  }

}
