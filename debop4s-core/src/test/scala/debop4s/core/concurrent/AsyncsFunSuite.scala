package debop4s.core.concurrent

import debop4s.core.AbstractCoreFunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * debop4s.core.stests.async.AsyncsFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오후 1:40
 */
class AsyncsFunSuite extends AbstractCoreFunSuite {

  val callable = () => {
    Thread.sleep(100)
    1
  }

  test("FutureExtensions - await") {

    val task = Future { callable() }
    task.isCompleted shouldEqual false

    task.await() shouldEqual 1
    task.isCompleted shouldEqual true
    task.value.get.isSuccess shouldEqual true
    task.value.get.isFailure shouldEqual false
  }

  test("new task") {
    val task = Asyncs.run(callable())
    task.isCompleted should equal(false)

    Asyncs.result(task) should equal(1)
    task.isCompleted should equal(true)
    task.value.get.isSuccess should equal(true)
    task.value.get.isFailure should equal(false)
  }

  test("start new task") {
    val task = Asyncs.run(callable())

    task.isCompleted should equal(false)
    Asyncs.result(task) should equal(1)
    task.isCompleted should equal(true)
    task.value.get.isSuccess should equal(true)
    task.value.get.isFailure should equal(false)
  }

  test("wait all") {
    val count = 10
    val tasks = new ArrayBuffer[Future[Int]]()

    (0 until 10).foreach {
      _ => tasks += Asyncs.run(callable())
    }

    Asyncs.readyAll(tasks)

    tasks.foreach(task => {
      task.value.get.get should equal(1)
      task.isCompleted should equal(true)
      task.value.get.isSuccess should equal(true)
    })
  }

}
