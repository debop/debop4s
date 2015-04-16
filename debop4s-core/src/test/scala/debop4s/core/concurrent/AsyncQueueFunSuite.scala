package debop4s.core.concurrent

import debop4s.core.AbstractCoreFunSuite

import scala.util.Try

/**
 * AsyncQueueFunSuite
 * Created by debop on 2014. 4. 9.
 */
class AsyncQueueFunSuite extends AbstractCoreFunSuite {

  var q: AsyncQueue[Int] = _

  before {
    q = new AsyncQueue[Int]
  }

  test("queue pollers") {
    val p0 = q.poll()
    val p1 = q.poll()
    val p2 = q.poll()

    p0.isCompleted shouldEqual false
    p1.isCompleted shouldEqual false
    p2.isCompleted shouldEqual false

    Thread.sleep(10)

    q.offer(1)
    p0.future.value shouldEqual Some(Try(1))
    p1.isCompleted shouldEqual false
    p2.isCompleted shouldEqual false

    q.offer(2)
    p1.future.value shouldEqual Some(Try(2))
    p2.isCompleted shouldEqual false

    q.offer(3)
    p2.future.value shouldEqual Some(Try(3))
  }

  test("queue offers") {
    q.offer(1)
    q.offer(2)
    q.offer(3)

    Thread.sleep(10)

    q.poll().future.value shouldEqual Some(Try(1))
    q.poll().future.value shouldEqual Some(Try(2))
    q.poll().future.value shouldEqual Some(Try(3))
  }

  test("into idle state and back") {
    q.offer(1)
    q.poll().future.value shouldEqual Some(Try(1))

    val p = q.poll()
    p.isCompleted shouldEqual false
    q.offer(2)
    p.future.value shouldEqual Some(Try(2))

    q.offer(3)
    q.poll().future.value shouldEqual Some(Try(3))
  }

  test("fail pending and new pollers") {
    val exc = new Exception("sad panda")
    val p0 = q.poll()
    val p1 = q.poll()

    p0.isCompleted shouldEqual false
    p1.isCompleted shouldEqual false

    q.fail(exc)
    p0.future.failed.value shouldEqual Some(Try(exc))
    p1.future.failed.value shouldEqual Some(Try(exc))

    q.poll().future.failed.value shouldEqual Some(Try(exc))
  }

  test("fail doesn't blow up offer") {
    val exc = new Exception("blow up offer")
    q.fail(exc)
    q.offer(1)
    q.poll().future.failed.value shouldEqual Some(Try(exc))
  }

}
