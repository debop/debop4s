package org.hibernate.cache.rediscala.client

import akka.util.ByteString
import org.hibernate.cache.rediscala.serializer.{FstRedisSerializer, SnappyRedisSerializer}
import org.slf4j.LoggerFactory
import redis.RedisClient
import redis.api.Limit
import redis.commands.TransactionBuilder
import redis.protocol.MultiBulk
import scala.actors.threadpool.TimeUnit
import scala.annotation.varargs
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try


/**
 * Redis-Server 에 Cache 정보를 저장하고 로드하는 Class 입니다.
 * 참고: rediscala 라이브러리를 사용합니다 ( https://github.com/etaty/rediscala )
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 1:14
 */
class HibernateRedisCache(val redis: RedisClient) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    // Cache Value 를 Fast-Serialization 을 이용하면 속도가 5배 정도 빨라지고, Snappy를 이용하여 압축을 수행하면 15배 정도 빨라진다.
    //private val valueSerializer = new SnappyRedisSerializer[Any](new BinaryRedisSerializer[Any]())
    private val valueSerializer = new SnappyRedisSerializer[Any](new FstRedisSerializer[Any]())

    def ping: Future[String] = redis.ping()

    def dbSize: Future[Long] = redis.dbsize()

    /**
    * Cache item 존재 유무 확인
    */
    def exists(region: String, key: String): Future[Boolean] = {
        redis.hexists(region, key)
    }

    /**
     * Get client item
     *
     * @param region           region name
     * @param key              client key
     * @param timeoutInSeconds expiration timeout value
     * @return return cached entity, if not exists return null.
     */
    @inline
    def get(region: String, key: String, expireInSeconds: Long = 0): Future[Any] = {
        // 값을 가져오고, 값이 있고, expiration이 설정되어 있다면 갱신합니다.
        val promise = Promise[Any]()
        val get = redis.hget(region, key)

        get onComplete { v =>
            if (v.isSuccess) {
                val value = v.map(x => valueSerializer.deserialize(x.get.toArray)).getOrElse(null)
                promise success value

                // expiration이 설정되어 있다면 갱신합니다.
                if (expireInSeconds > 0 && !region.contains("UpdateTimestampsCache")) {
                    val score = System.currentTimeMillis + expireInSeconds * 1000L
                    redis.zadd(regionExpireKey(region), (score, key))
                }
            } else {
                promise complete null
            }
        }
        promise.future
    }

    /**
    * 지정한 region에 있는 모든 캐시 키를 조회합니다.
    */
    def keysInRegion(region: String): Future[Seq[String]] = {
        redis.hkeys(region)
    }

    def keySizeInRegion(region: String): Future[Long] = redis.hlen(region)

    def getAll(region: String): Future[Map[String, Any]] = {
        redis.hgetall(region).map { kvs =>
            kvs.map(x => (x._1, valueSerializer.deserialize(x._2.toArray)))
        }
    }

    @varargs
    def multiGet(region: String, keys: String*): Future[Seq[Any]] = {
        redis.hmget(region, keys: _*).map { results =>
            results.map { (x: Option[ByteString]) =>
                x.map(v => valueSerializer.deserialize(v.toArray)).getOrElse(null)
            }
        }
    }

    def multiGet(region: String, keys: Iterable[String]): Future[Seq[Any]] = {
        redis.hmget(region, keys.toSeq: _*).map { results =>
            results.map { x =>
                x.map(v => valueSerializer.deserialize(v.toArray))
                .getOrElse(null)
            }
        }
    }

    @inline
    def set(region: String, key: String, value: Any, expiry: Long = 0, unit: TimeUnit = TimeUnit.SECONDS): Future[Boolean] = {
        val p = Promise[Boolean]()
        // 값 변환
        val f = future {
            ByteString(valueSerializer.serialize(value))
        }

        f onComplete { (v: Try[ByteString]) =>
            val set = redis.hset(region, key, v.get)
            set onComplete { (ret: Try[Boolean]) =>
                p complete ret
                if (ret.isSuccess) {
                    val expireInSeconds = unit.toSeconds(expiry)
                    if (expireInSeconds > 0) {
                        val score: Long = System.currentTimeMillis + expireInSeconds * 1000L
                        redis.zadd(regionExpireKey(region), (score, key))
                    }
                }
            }
        }
        p.future
    }

    @inline
    def expire(region: String): Future[Any] = future {
        val regionExpire = regionExpireKey(region)
        val score = System.currentTimeMillis()

        val results = redis.zrangebyscore(regionExpire, Limit(0), Limit(score)).map(xs => xs.map(_.utf8String))
        val keysToExpire: Seq[String] = Await.result(results, 10 seconds)

        if (keysToExpire != null && keysToExpire.nonEmpty) {
            log.trace(s"cache item들을 expire 시킵니다. region=$region, keys=$keysToExpire")

            keysToExpire.par.foreach(key => redis.hdel(region, key))
            redis.zremrangebyscore(regionExpire, Limit(0), Limit(score))
        }
    }

    def delete(region: String, key: String): Future[Long] = {
        val deleted = redis.hdel(region, key)
        redis.zrem(regionExpireKey(region), key)
        deleted
    }

    @varargs
    def multiDelete(region: String, keys: String*): Future[Boolean] = {
        if (keys == null || keys.isEmpty)
            return Future(false)

        future {
            val regionExpire = regionExpireKey(region)
            keys.foreach { key =>
                redis.hdel(region, key)
                redis.zrem(regionExpire, key)
            }
            true
        }
    }

    def multiDelete(region: String, keys: Iterable[String]) {
        multiDelete(region, keys.toSeq: _*)
    }

    def deleteRegion(region: String) = future {
        redis.del(region)
        redis.del(regionExpireKey(region))
    }

    def flushDb() = future {
        log.info(s"Redis DB 전체를 flush 합니다...")
        redis.flushdb()
    }

    def withTransaction(block: TransactionBuilder => Unit): Future[MultiBulk] = {
        val tx = redis.transaction()
        block(tx)
        tx.exec()
    }

    private def regionExpireKey(region: String) = region + ":expire"
}

/**
 * HibernateRedisCache Companion Object
 */
object HibernateRedisCache {

    implicit val akkaSystem = akka.actor.ActorSystem()

    // Cache expiration 기본 값 (0 이면 expire 하지 않는다)
    val DEFAULT_EXPIRY_IN_SECONDS = 0

    // default resion name
    val DEFAULT_REGION_NAME = "hibernate"

    def apply(): HibernateRedisCache =
        apply(RedisClient())

    def apply(redis: RedisClient): HibernateRedisCache =
        new HibernateRedisCache(redis)

}
