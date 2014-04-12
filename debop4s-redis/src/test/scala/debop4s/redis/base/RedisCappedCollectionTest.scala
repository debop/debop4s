package debop4s.redis.base

import debop4s.core.parallels.Asyncs
import debop4s.redis.AbstractRedisTest

/**
 * RedisCappedCollectionTest
 * Created by debop on 2014. 3. 20.
 */
class RedisCappedCollectionTest extends AbstractRedisTest {

  test("크기가 제한된 List") {

    val name = "col-10"
    Asyncs.ready(redis.del(name))

    val coll = RedisCappedCollection[Int](name, 10)

    for (x <- 0 until 100) {
      coll.lpush(x)
    }
    val list = Asyncs.result(coll.getRange(0, 100))
    println(list)
    assert(list === Array.range(90, 100).reverse)

    redis.del(name)
  }

}
