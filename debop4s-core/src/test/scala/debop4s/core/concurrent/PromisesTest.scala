package debop4s.core.concurrent

import debop4s.core.AbstractCoreTest
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
 * PromisesTest
 * Created by debop on 2014. 3. 1.
 */
class PromisesTest extends AbstractCoreTest {
    val callable = () => {
        Thread.sleep(100)
        1
    }

    test("new task") {
        val task = Promises.exec[Int] {
            callable()
        }
        task.isCompleted should equal(false)

        Promises.await(task) should equal(1)
        task.isCompleted should equal(true)
        task.value.get.isSuccess should equal(true)
        task.value.get.isFailure should equal(false)
    }

    test("start new task") {
        val task = Promises.exec[Int] {
            callable()
        }
        task.isCompleted should equal(false)

        Promises.await(task) should equal(1)
        task.isCompleted should equal(true)
        task.value.get.isSuccess should equal(true)
        task.value.get.isFailure should equal(false)
    }

    test("wait all") {
        val count = 10
        val tasks = new ArrayBuffer[Future[Int]]()

        (0 until 10).foreach {
            _ =>
                tasks += Promises.exec[Int] {
                    callable()
                }
        }

        Promises.awaitAll(tasks).foreach {
            result =>
                result should equal(1)
        }

        tasks.foreach {
            task =>
                task.value.get.get should equal(1)
                task.isCompleted should equal(true)
                task.value.get.isSuccess should equal(true)
        }
    }
}
