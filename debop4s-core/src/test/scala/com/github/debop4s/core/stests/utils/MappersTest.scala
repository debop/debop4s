package com.github.debop4s.core.stests.utils

import com.github.debop4s.core.utils.Mappers
import org.scalatest.BeforeAndAfter
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import com.github.debop4s.core.stests.AbstractCoreTest

/**
 * com.github.debop4s.core.tests.tools.MappersTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오전 10:24
 */
class MappersTest extends AbstractCoreTest {

    test("map") {
        val a = new A(100)
        val b = Mappers.map[B](a)
        assert(b.x == a.x)
    }

    test("map list") {
        val as = Range(0, 100).map(x => new A(x)) // for (x <- 0 until 100) yield new A(x)
        val bs = Mappers.mapAll[B](as)
        assert(bs.size == as.size)

        val bsf = Mappers.mapAllAsync[B](as)
        Await.result(bsf, 100 milli)
        bsf.value.get.get.size shouldEqual as.size
    }

    import scala.concurrent.ExecutionContext.Implicits.global

    test("map array") {
        val bs = Mappers.mapAll[B](List(new A(0), new A(1), new A(2)))
        assert(bs.size == 3)

        val bsf = Mappers.mapAllAsync[B](List(new A(0), new A(1), new A(2)))
        bsf onComplete {
            case Success(result) => result.size shouldEqual 3
            case Failure(t) => throw new RuntimeException(t)
        }
    }
}


class A(var x: Int) {
    def this() { this(0) }

    var y: String = _
}

class B(var x: Int) {
    def this() { this(0) }
}