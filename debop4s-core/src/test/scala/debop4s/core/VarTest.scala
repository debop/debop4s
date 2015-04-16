//package debop4s.core
//
//import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
//import org.slf4j.LoggerFactory
//import scala.collection.mutable
//import scala.concurrent.Future
//
///**
// * VarTest
// * @author Sunghyouk Bae
// */
//@RunWith(classOf[JUnitRunner])
//class VarTest extends AbstractCoreFunSuite {
//
//    private case class U[T](init: T) extends UpdatableVar[T](init) {
//
//        import Var.Observer
//
//        var observerCount = 0
//        var accessCount = 0
//
//        override def observe(d: Int, obs: Observer[T]) = {
//            accessCount += 1
//            observerCount += 1
//            Closable.all(
//                super.observe(d, obs),
//                Closable.make { deadline =>
//                    observerCount -= 1
//                    LOG.debug(s"close... observerCount=$observerCount")
//                    Future.successful(Unit)
//                }
//            )
//        }
//    }
//
//    test("Var.map") {
//        val v = Var(123)
//        val s = v map (_.toString)
//        Var.sample(s) shouldEqual "123"
//        v() = 8923
//        Var.sample(s) shouldEqual "8923"
//
//        var buf = mutable.Buffer[String]()
//        s observe { v => buf += v }
//        buf.toSeq shouldEqual Seq("8923")
//        v() = 111
//        buf.toSeq shouldEqual Seq("8923", "111")
//    }
//
//    test("depth ordering") {
//        val v0 = U(3)
//        val v1 = U(2)
//        val v2 = v1 flatMap { i => v1 }
//        val v3 = v2 flatMap { i => v1 }
//        val v4 = v3 flatMap { i => v0 }
//
//        var result = 1
//        v4 observe { i => result = result + 2 } // result 3
//        v0.observe { i => result = result * 2 } // result 3 * 2
//        result shouldEqual 6
//
//        result = 1 // reset the value, but this time the ordering will go v0, v4 because of depth
//        v0() = 4 // trigger recomputation, supllied value is unused
//        // v0 observation: result = result * 2 = 2
//        // v4 observation: result = result + 2 = 4
//        result shouldEqual 4
//    }
//
//    test("version ordering") {
//        val v1 = Var(2)
//        var result = 0
//
//        val o1 = v1 observe { i => result = result + i } // result = 0 + 2 = 2
//        val o2 = v1 observe { i => result = result * i * i } // result = 2 * 2 * 2 = 8
//        val o3 = v1 observe { i => result = result + result + i } // result = 8 + 8 + 2  = 18
//
//        result shouldEqual 18
//
//        result = 1 // just reset for sanity
//        v1() = 3 // this should invoke o1-o3 in order:
//        // result = 1 + 3 = 4
//        // result = 4 * 3 * 3 = 36
//        // result = 36 + 36 + 3 = 75
//        result shouldEqual 75
//    }
//
//    test("flatMap") {
//        val us = Seq.fill(5) { U(0) }
//        def short(us: Seq[Var[Int]]): Var[Int] = us match {
//            case Seq(hd, tl@_*) =>
//                hd flatMap {
//                    case 0 => short(tl)
//                    case i => Var(i)
//                }
//            case Seq() => Var(-1)
//        }
//
//        val s = short(us)
//        Var.sample(s) shouldEqual -1
//        assert(us forall (_.accessCount == 1), us map (_.accessCount) mkString ",")
//
//        Var.sample(s)
//        Var.sample(s)
//        assert(us forall (_.accessCount == 3))
//        assert(us forall (_.observerCount == 0), us map (_.observerCount) mkString ",")
//
//        // Now maintain a subscription.
//        var cur = Var.sample(s)
//        val sub = s.observe { cur = _ }
//        assert(cur === -1)
//    }
//
//}
