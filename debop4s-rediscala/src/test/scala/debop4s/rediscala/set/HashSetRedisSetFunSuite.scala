package debop4s.rediscala.set

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.rediscala.AbstractRedisFunSuite
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global


class HashSetRedisSetFunSuite extends AbstractRedisFunSuite {

  @Autowired val redisSet: HashSetRedisSet = null

  test("empty hashset") {

    val key = "hashset"

    redisSet.delete(key)

    val set = mutable.HashSet[String]()
    redisSet.set(key, "empty", set)
    val loaded = redisSet.get(key, "empty").map(_.orNull).await

    loaded shouldEqual set
    redisSet.delete(key)
  }

  test("add hashset") {
    val key = "hashset"

    redisSet.delete(key)

    val set = mutable.HashSet("a", "b", "1", "2", "가")

    redisSet.set(key, "add", set)

    val loaded = redisSet.get(key, "add").map(_.orNull).await

    loaded shouldEqual set
    redisSet.delete(key)
  }
}


