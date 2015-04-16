package debop4s.rediscala.benchmark

import debop4s.rediscala._
import debop4s.rediscala.client.{RedisSyncClient, RedisSyncMasterSlavesClient}
import org.scalameter.{Gen, PerformanceTest}
import redis.{RedisClientMasterSlaves, RedisServer}

/**
 * 기존 RedisSyncClient는 Jedis보다 느리다.
 * 그래서 RedisSyncClient 내부의 RedisClient를 CPU * 2 개를 만들게 하고, 병렬로 작업을 수행하게 했다.
 * 그래도 Redis 를 직접 이용하는 것보다 5배정도 느리고, 다만 Jedis 보다는 30% 정도 빠르다.
 *
 * @author sunghyouk.bae@gmail.com
 */
object RedisSyncClientBenchmark extends PerformanceTest.Quickbenchmark {

  val sizes = Gen.range("size")(5000, 20000, 5000)
  val ranges = sizes.map(s => 0 until s)

  val redisClient = RedisSyncClient()
  val redisPool4 = RedisSyncMasterSlavesClient(RedisClientMasterSlaves(RedisServer(), createServer(4)))
  val redisPool16 = RedisSyncMasterSlavesClient(RedisClientMasterSlaves(RedisServer(), createServer(16)))

  def createServer(n: Int): Seq[RedisServer] = {
    (0 until n).map { i => RedisServer() }
  }

  val key = "redis:benchmark:key"
  val value = "redis-benchmark-value"

  performance of "set / get " in {

    measure method "by RedisClient" in {
      using(ranges) in { range =>
        range.par.foreach { x =>
          val setted = redisClient.set(key, value + x.toString)
          val loaded = redisClient.get[String](key)
          setted && loaded.nonEmpty
        }
      }
    }

    measure method "by RedisPool 4" in {
      using(ranges) in { range =>
        range.par.foreach { x =>
          val setted = redisPool4.set(key, value + x.toString)
          val loaded = redisPool4.get[String](key)
          setted && loaded.nonEmpty
        }
      }
    }

    measure method "by RedisPool 16" in {
      using(ranges) in { range =>
        range.par.foreach { x =>
          val setted = redisPool16.set(key, value + x.toString)
          val loaded = redisPool16.get[String](key)
          setted && loaded.nonEmpty
        }
      }
    }
  }

  val zkey = "redis:benchmark:zkey"

  performance of "zset" in {

    measure method "by RedisClient" in {
      using(ranges) in { range =>
        range.par.foreach { x =>
          val setted = redisClient.zadd(zkey, x.toDouble, value + x.toString)
          val loaded = redisClient.zscore[String](zkey, value + x.toString)
          setted > 0 && loaded.nonEmpty
        }
      }
    }

    measure method "by RedisPool 4" in {
      using(ranges) in { range =>
        range.par.foreach { x =>
          val setted = redisPool4.zadd(zkey, x.toDouble, value + x.toString)
          val loaded = redisPool4.zscore[String](zkey, value + x.toString)
          setted > 0 && loaded.nonEmpty
        }
      }
    }

    measure method "by RedisPool 16" in {
      using(ranges) in { range =>
        range.par.foreach { x =>
          val setted = redisPool16.zadd(zkey, x.toDouble, value + x.toString)
          val loaded = redisPool16.zscore[String](zkey, value + x.toString)
          setted > 0 && loaded.nonEmpty
        }
      }
    }
  }


}
