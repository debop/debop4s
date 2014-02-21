package org.hibernate.cache.redis.client

import akka.util.ByteString
import org.hibernate.cache.redis.serializer.BinaryRedisSerializer
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


/**
 * Redis-Server 에 Cache 정보를 저장하고 로드하는 Class 입니다.
 * 참고: rediscala 라이브러리를 사용합니다 ( https://github.com/etaty/rediscala )
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 1:14
 */
class CacheClient(val redis: RedisClient) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    val DEFAULT_EXPIRY_IN_SECONDS = 0

    val DEFAULT_REGION_NAME = "hibernate"

    private val valueSerializer = new BinaryRedisSerializer[Any]()

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
    def get(region: String, key: String, expireInSeconds: Long = 0): Future[Any] = {
        log.trace(s"retrieve CacheItem... region=$region, key=$key")

        redis.hget(region, key).map((item: Option[ByteString]) => {
            if (expireInSeconds > 0 && !region.contains("UpdateTimestampsCache")) {
                val score = System.currentTimeMillis + expireInSeconds * 1000L
                redis.zadd(regionExpireKey(region), (score, key))
            }
            item.map((x: ByteString) => valueSerializer.deserialize(x.toArray))
            .getOrElse(null)
        })
    }

    /**
    * 지정한 region에 있는 모든 캐시 키를 조회합니다.
    */
    def keysInRegion(region: String): Future[Seq[String]] = {
        log.trace(s"retrieve all client item key in region=$region")

        redis.hkeys(region)
    }

    def keySizeInRegion(region: String): Future[Long] = redis.hlen(region)

    def getAll(region: String): Future[Map[String, Any]] = {

        redis.hgetall(region).map(kvs => {
            kvs.map(x => (x._1, valueSerializer.deserialize(x._2.toArray)))
        })
    }

    @varargs
    def multiGet(region: String, keys: String*): Future[Seq[Any]] = {
        redis.hmget(region, keys: _*).map(results => {
            results.map(x => {
                x.map(v => valueSerializer.deserialize(v.toArray))
                .getOrElse(null)
            })
        })
    }

    def multiGet(region: String, keys: Iterable[String]): Future[Seq[Any]] = {
        redis.hmget(region, keys.toSeq: _*).map(results => {
            results.map(x => {
                x.map(v => valueSerializer.deserialize(v.toArray))
                .getOrElse(null)
            })
        })
    }

    def set(region: String, key: String, value: Any, expiry: Long = 0, unit: TimeUnit = TimeUnit.SECONDS): Future[Any] = {

        log.trace(s"cache 값을 설정합니다... region=$region, key=$key, value=$value, expiry=$expiry, unit=$unit")

        val v = ByteString(valueSerializer.serialize(value))
        val expireInSeconds = unit.toSeconds(expiry)
        if (expireInSeconds > 0L) {
            withTransaction {
                tx => {
                    tx.hset(region, key, v)

                    if (expireInSeconds > 0) {
                        val score: Long = System.currentTimeMillis + expireInSeconds * 1000L
                        tx.zadd(regionExpireKey(region), (score, key))
                    }
                }
            }
        } else {
            redis.hset(region, key, v)
        }
    }

    def expire(region: String) {

        val regionExpire = regionExpireKey(region)
        val score = System.currentTimeMillis()

        val results = redis.zrangebyscore(regionExpire, Limit(0), Limit(score))
        val keysToExpire: Seq[ByteString] = Await.result(results, 10 seconds)

        if (keysToExpire != null && keysToExpire.nonEmpty) {
            log.trace(s"cache item들을 expire 시킵니다. region=$region, keys=$keysToExpire")
            withTransaction(tx => {
                keysToExpire.par.foreach(key => {
                    tx.hdel(region, key.toString())
                })
                tx.zremrangebyscore(regionExpire, Limit(0), Limit(score))
            })
        }
    }

    def delete(region: String, key: String) = {
        log.trace(s"캐시 항목을 삭제합니다. region=$region, key=$key")
        withTransaction(tx => {
            tx.hdel(region, key)
            tx.zrem(regionExpireKey(region), key)
        })
    }

    @varargs
    def multiDelete(region: String, keys: String*): Future[MultiBulk] = {
        val regionExpire = regionExpireKey(region)
        withTransaction(tx => {
            keys.foreach(key => {
                tx.hdel(region, key)
                tx.zrem(regionExpire, key)
            })
        })
    }

    def multiDelete(region: String, keys: Iterable[String]): Future[MultiBulk] = {
        multiDelete(region, keys.toSeq: _*)
    }

    def deleteRegion(region: String) = {
        log.debug(s"delete region... region=$region")
        withTransaction(tx => {
            tx.del(region)
            tx.del(regionExpireKey(region))
        })
    }

    def flushDb() = {
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

object CacheClient {

    implicit val akkaSystem = akka.actor.ActorSystem()

    def apply(): CacheClient =
        apply(RedisClient())

    def apply(redis: RedisClient): CacheClient =
        new CacheClient(redis)

}
