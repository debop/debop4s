package debop4s.core.parallels

import debop4s.core._
import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 * ScalaAsyncsTest
 * Created by debop on 2014. 4. 4.
 */
class ScalaAsyncsTest extends AbstractCoreTest {

  test("scala-async async/await example") {
    val future1 = Future { 42 }
    val future2 = future { 84 }

    async {
      println("computing...")
      val answer = await(future1)
      println(s"found the answer: $answer")
    }

    val sum = async { await(future1) + await(future2) }
    assert(sum.result() == (42 + 84))
  }

}
