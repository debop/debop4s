package debop4s.rediscala

import debop4s.core._
import debop4s.core.concurrent._

/**
 * RedisCappedCollectionTest
 * Created by debop on 2014. 3. 20.
 */
class RedisCappedCollectionFunSuite extends AbstractRedisFunSuite {

  test("크기가 제한된 List") {
    val name = "col-10"
    redis.del(name).stay

    val coll = RedisCappedCollection[Int](name, 10)

    coll.lpushAll(0 until 100: _*).stay

    val list = coll.getRange(0, 100).await

    println(list)
    list.toList shouldEqual List.range(90, 100).reverse

    redis.del(name).stay
  }

}
