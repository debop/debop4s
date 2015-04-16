package debop4s.rediscala.benchmark

import debop4s.core._
import debop4s.rediscala._
import org.scalameter.{Gen, PerformanceTest}
import redis.{RedisClient, RedisClientPool, RedisServer}

import scala.async.Async._
import scala.collection.mutable
import scala.concurrent.Future

/**
 * RedisClient 와 RedisPoolClient 의 성능 비교
 *
 * @author sunghyouk.bae@gmail.com
 */
object RedisClientBenchmark extends PerformanceTest.Quickbenchmark {

  val sizes = Gen.range("size")(5000, 20000, 5000)
  val ranges = sizes.map(s => 0 until s)


  val redisClient = RedisClient()
  val redisPool4 = createRedisClientPool(4)
  val redisPool16 = createRedisClientPool(16)

  private def createRedisClientPool(size: Int): RedisClientPool = {
    RedisClientPool((0 until size).map(_ => RedisServer()))
  }

  val key = "redis:benchmark:key"
  val value = "redis-benchmark-value"

  performance of "set / get " in {

    measure method "by RedisClient" in {
      using(sizes) in { size =>
        run(size) { x =>
          async {
            val setted = await(redisClient.set(key, value + x.toString))
            val loaded = await(redisClient.get[String](key))
            setted && loaded.nonEmpty
          }
        }
      }
    }

    measure method "by RedisPool 4" in {
      using(sizes) in { size =>
        run(size) { x =>
          async {
            val setted = await(redisPool4.set(key, value + x.toString))
            val loaded = await(redisPool4.get[String](key))
            setted && loaded.nonEmpty
          }
        }
      }
    }

    measure method "by RedisPool 16" in {
      using(sizes) in { size =>
        run(size) { x =>
          async {
            val setted = await(redisPool16.set(key, value + x.toString))
            val loaded = await(redisPool16.get[String](key))
            setted && loaded.nonEmpty
          }
        }
      }
    }
  }

  val zkey = "redis:benchmark:zkey"

  performance of "zset" in {

    measure method "by RedisClient" in {
      using(sizes) in { size =>
        run(size) { x =>
          async {
            val setted = await(redisClient.zadd(zkey, (x.toDouble, value + x.toString)))
            val loaded = await(redisClient.zscore[String](zkey, x.toString))
            setted > 0 && loaded.nonEmpty
          }
        }
      }
    }

    measure method "by RedisPool 4" in {
      using(sizes) in { size =>
        run(size) { x =>
          async {
            val setted = await(redisPool4.zadd(zkey, (x.toDouble, value + x.toString)))
            val loaded = await(redisPool4.zscore[String](key, x.toString))
            setted > 0 && loaded.nonEmpty
          }
        }
      }
    }

    measure method "by RedisPool 16" in {
      using(sizes) in { size =>
        run(size) { x =>
          async {
            val setted = await(redisPool16.zadd(zkey, (x.toDouble, value + x.toString)))
            val loaded = await(redisPool16.zscore[String](key, x.toString))
            setted > 0 && loaded.nonEmpty
          }
        }
      }
    }
  }

  private def run(size: Int)(block: Int => Future[Boolean]): Unit = {
    val futures = mutable.ListBuffer[Future[Boolean]]()

    var i = 0
    while (i < size) {
      futures += block(i)
      i += 1
    }
    Future.sequence(futures).await
  }
}
