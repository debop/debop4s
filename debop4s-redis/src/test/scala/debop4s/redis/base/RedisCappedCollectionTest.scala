package debop4s.redis.base

import debop4s.core.concurrent._
import debop4s.redis.AbstractRedisTest

/**
 * RedisCappedCollectionTest
 * Created by debop on 2014. 3. 20.
 */
class RedisCappedCollectionTest extends AbstractRedisTest {

  test("크기가 제한된 List") {

    val name = "col-10"
    redis.del(name).await

    val coll = RedisCappedCollection[Int](name, 10)

    for (x <- 0 until 100) {
      coll.lpush(x)
    }
    val list = coll.getRange(0, 100).await
    println(list)
    assert(list === Array.range(90, 100).reverse)

    redis.del(name)
  }

}
