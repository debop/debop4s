package debop4s.rediscala.set

import debop4s.rediscala.AbstractRedisFunSuite
import org.springframework.beans.factory.annotation.Autowired

class DefaultSyncRedisZSetFunSuite extends AbstractRedisFunSuite {

  val zsetKey = "sort-set"

  @Autowired val zset: DefaultSyncRedisZSet = null

  test("incrBy") {
    val x1 = zset.incrBy(zsetKey, 1, "m")
    val x2 = zset.incrBy(zsetKey, 1, "m")

    x2 shouldEqual x1 + 1
  }

}

