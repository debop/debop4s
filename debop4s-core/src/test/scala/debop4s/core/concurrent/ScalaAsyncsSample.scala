package debop4s.core.concurrent

import java.util.concurrent.TimeUnit

import debop4s.core._

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration

/**
 * ScalaAsyncsTest
 * Created by debop on 2014. 4. 4.
 */
class ScalaAsyncsSample extends AbstractCoreTest {

  implicit val defaultDuration: Duration = Duration(60, TimeUnit.MINUTES)

  test("scala-async async/await example") {
    val future1: Future[Int] = Future { 42 }
    val future2: Future[Int] = Future { 84 }

    async {
      println("computing...")
      val answer = await(future1)
      println(s"found the answer: $answer")
    }

    val sum: Future[Int] = async { await(future1) + await(future2) }
    sum.await shouldBe (42 + 84)
    sum.asyncValue shouldBe (42 + 84)
  }

}
