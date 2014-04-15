package debop4s.core.concurrent

import debop4s.core.AbstractCoreTest
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * SpoolSourceTest
 * @author Sunghyouk Bae
 */
class SpoolSourceTest extends AbstractCoreTest {

    var source: SpoolSource[Int] = _
    before {
        source = new SpoolSource[Int]
    }

    test("add values to the spool, ignoreing values after close") {
        val futureSpool = source()
        source.offer(1)
        source.offer(2)
        source.offer(3)
        source.close()
        source.offer(4)
        source.offer(5)

        Asyncs.result(futureSpool flatMap (_.toSeq)) should contain only(1, 2, 3)
    }

    test("return multiple Future Spools that only see values added later") {
        val futureSpool1 = source()
        source.offer(1)
        val futureSpool2 = source()
        source.offer(2)
        val futureSpool3 = source()
        source.offer(3)
        val futureSpool4 = source()
        source.close()

        Asyncs.result(futureSpool1 flatMap (_.toSeq)) should contain only(1, 2, 3)
        Asyncs.result(futureSpool2 flatMap (_.toSeq)) should contain only(2, 3)
        Asyncs.result(futureSpool3 flatMap (_.toSeq)) should contain only 3
        Asyncs.result(futureSpool4).isEmpty shouldEqual true
    }

    test("throw exception and close spool when exception in raised") {
        val futureSpool1 = source()
        source.offer(1)
        source.raise(new Exception("sad panda"))
        val futureSpool2 = source()
        source.offer(1)

        intercept[Exception] { Asyncs.result(futureSpool1 flatMap (_.toSeq)) }
        Asyncs.result(futureSpool2).isEmpty shouldEqual true
    }

}
