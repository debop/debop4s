package debop4s.rediscala.utils

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.rediscala.AbstractRedisFunSuite


/**
 * RedisHelperTest
 * Created by debop on 2014. 3. 20.
 */
class RedisHelperFunSuite extends AbstractRedisFunSuite {

  test("increment and get") {
    redis.set("inc", 0).await

    val helper = RedisHelper()
    helper.increseAndGet("inc").await shouldBe 1
    helper.increseAndGet("inc").await shouldBe 2
    helper.increseAndGet("inc").await shouldBe 3

    redis.del("inc")
  }

  test("decrement and get") {

    redis.set("dec", 100).await

    val helper = RedisHelper()

    helper.decreaseAndGet("dec").await shouldBe 99
    helper.decreaseAndGet("dec").await shouldBe 98
    helper.decreaseAndGet("dec").await shouldBe 97

    redis.del("dec")
  }

}
