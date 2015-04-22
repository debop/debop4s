package debop4s.rediscala.set

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.rediscala.{AbstractRedisFunSuite, _}
import org.springframework.beans.factory.annotation.Autowired

import scala.async.Async._

/**
 * DefaultRedisZSetFunSuite
 * @author Sunghyouk Bae
 */
class DefaultRedisZSetFunSuite extends AbstractRedisFunSuite {

  val zsetKey = "sort-set"

  @Autowired val zset: DefaultRedisZSet = null

  test("incrBy") {
    val task = async {
      val x1 = await(zset.incrBy(zsetKey, 1, "m"))
      val x2 = await(zset.incrBy(zsetKey, 1, "m"))

      x2 shouldEqual x1 + 1
    }
    task.await
  }

}
