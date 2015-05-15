package debop4s.core.retry

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

import debop4s.core.utils.JavaTimer
import debop4s.core._
import debop4s.core.concurrent._
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * PolicyFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PolicyFunSuite extends AbstractCoreFunSuite with BeforeAndAfterAll {

  implicit val timer = JavaTimer()

  override def afterAll() {
    timer.stop()
  }

  def forwardCountingFutureStream(value: Int = 0): Stream[Future[Int]] =
    Future(value) #:: forwardCountingFutureStream(value + 1)

  def backwardCountingFutureStream(value: Int): Stream[Future[Int]] = {
    if (value < 0) Stream.empty
    else Future(value) #:: backwardCountingFutureStream(value - 1)
  }

  def time[@miniboxed T](f: => T): Duration = {
    val before = System.currentTimeMillis()
    f
    Duration(System.currentTimeMillis() - before, MILLISECONDS)
  }

  test("Directly - 지정된 횟수만큼 재시도 합니다.") {
    implicit val successful = Successful[Int](_ == 3)
    val tries = forwardCountingFutureStream().iterator
    val result = Directly(3)(tries.next()).await(1 millis)

    successful.predicate(result) shouldEqual true
  }

  test("Directly - 작업 실패 시") {
    val successful = implicitly[Successful[Option[Int]]]
    val tries = Future(None: Option[Int])
    val result = Directly(2)(tries).await(1 millis)
    successful.predicate(result) shouldEqual false
  }

  test("Directly - 재시도 횟수 제한 여부") {
    implicit val successful = Successful.always
    val policy = Directly(3)
    val counter = new AtomicInteger()

    val future = policy {
      val c = counter.incrementAndGet()
      Future.failed(new RuntimeException(s"always failing - $c"))
    }
    // Asyncs.stay(future, Duration.Inf)
    future.stay(Duration.Inf)
    counter.get() shouldEqual 4
  }

  test("Directly - 성공한 결과에 대한 테스트") {
    implicit val successful = Successful.always
    val counter = new AtomicInteger()
    val future = Directly(1) {
      counter.getAndIncrement match {
        case 1 => Future.successful("yay!")
        case _ => Future.failed(new RuntimeException("failed"))
      }
    }
    val result = future.await(Duration.Inf)
    counter.get shouldBe 2
    result shouldEqual "yay!"
  }

  test("Pause - 실패 시 pause 하는지 여부") {
    implicit val successful = Successful[Int](_ == 3)
    val tries = forwardCountingFutureStream().iterator
    val policy = Pause(3, 30.millis)
    val took = time {
      val result = policy(tries.next()).await(90.millis + 50.millis)
      successful.predicate(result) shouldEqual true
    }

    log.debug(s"took = $took")
    (took >= 90.millis) shouldEqual true
    (took <= 140.millis) shouldEqual true
  }

  test("Backoff") {
    implicit val successful = Successful[Int](_ == 2)
    val tries = forwardCountingFutureStream().iterator
    val policy = Backoff(2, 30.millis)
    val took = time {
      val result = policy(tries.next()).await(90.millis + 50.millis)
      successful.predicate(result) shouldEqual true
    }

    log.debug(s"took = $took")
    (took >= 90.millis) shouldEqual true
    (took <= 150.millis) shouldEqual true
  }

  test("When - 재시도 조건이 왔을 때") {
    implicit val successful = Successful[Int](_ == 2)
    val tries = forwardCountingFutureStream().iterator
    val policy = When {
      case 0 => When {
        case 1 => Pause(delay = 2.seconds)
      }
    }
    val future = policy(tries.next())
    val result = future.await(2.seconds)

    successful.predicate(result) shouldEqual true
  }

  test("When - 조건을 맞났을 때") {
    implicit val successful = Successful[Int](_ == 2)
    val tries = forwardCountingFutureStream().iterator
    val policy = When {
      case 1 => Directly()
    }
    val future = policy(tries.next())
    val result = future.await(1.millis)

    successful.predicate(result) shouldEqual false
  }

  test("When - 예외 발생 시") {
    implicit val successful = Successful[Boolean](identity)
    case class RetryAfter(duration: FiniteDuration) extends RuntimeException
    val retried = new AtomicBoolean()

    def run() = {
      if (retried.get()) Future(true)
      else {
        retried.set(true)
        Future.failed(RetryAfter(1.seconds))
      }
    }

    val policy = When {
      case RetryAfter(duration) => Pause(delay = duration)
    }

    val result = policy(run()).await(Duration.Inf)

    result shouldEqual true
  }

}
