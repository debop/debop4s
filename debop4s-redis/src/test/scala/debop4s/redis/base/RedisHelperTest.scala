package debop4s.redis.base

import debop4s.core.concurrent.Asyncs
import debop4s.redis.AbstractRedisTest

/**
 * RedisHelperTest
 * Created by debop on 2014. 3. 20.
 */
class RedisHelperTest extends AbstractRedisTest {

    test("increment and get") {
        Asyncs.ready(redis.set("inc", 0))

        val helper = RedisHelper()
        assert(Asyncs.result(helper.increseAndGet("inc")) == 1)
        assert(Asyncs.result(helper.increseAndGet("inc")) == 2)
        assert(Asyncs.result(helper.increseAndGet("inc")) == 3)

        redis.del("inc")
    }

    test("decrement and get") {

        Asyncs.ready(redis.set("dec", 100))

        val helper = RedisHelper()

        assert(Asyncs.result(helper.decreaseAndGet("dec")) == 99)
        assert(Asyncs.result(helper.decreaseAndGet("dec")) == 98)
        assert(Asyncs.result(helper.decreaseAndGet("dec")) == 97)

        redis.del("dec")
    }

}
