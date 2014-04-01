package com.github.debop4s.redis.spring

import com.github.debop4s.core.io.BinarySerializer
import com.github.debop4s.core.parallels.Promises
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.Cache.ValueWrapper
import org.springframework.cache.support.SimpleValueWrapper
import redis.RedisClient
import redis.commands.TransactionBuilder
import redis.protocol.MultiBulk
import scala.concurrent.Future

/**
 * Redis를 캐시 서버로 사용하는 Spring @Cache 를 지원하는 Cache 입니다.
 * Created by debop on 2014. 2. 22.
 */
class RedisCache(val name: String,
                 prefix: String,
                 val redis: RedisClient,
                 expiration: Long = 0) extends Cache {

    private lazy val log = LoggerFactory.getLogger(getClass)

    log.trace(s"create RedisCache name=$name, prefix=$prefix, expiration=$expiration, redis=$redis")

    val PAGE_SIZE = 100

    val serializer = new BinarySerializer()

    val setName = s"cache:keys:$name"
    val cacheLockName = s"cache:lock:$name"

    var waitTimeoutForLock = 5 // msec

    override def getNativeCache: AnyRef = redis

    override def getName: String = name

    /**
    * 캐시 항목을 조회합니다.
    */
    def get(key: Any): ValueWrapper = {
        log.trace(s"캐시 조회. key=$key")

        Promises.await(redis.get(computeKey(key)))
        .map(bs => new SimpleValueWrapper(serializer.deserialize(bs.toArray, classOf[AnyRef])))
        .getOrElse(null)
    }

    /**
    * 캐시 항목을 조회합니다.
    * Spring 4.0 이상에서 지원합니다.
    */
    def get[T](key: Any, clazz: Class[T]): T = {
        log.trace(s"캐시 조회. key=$key, clazz=$clazz")

        waitForLock(redis)

        Promises.await(redis.get(computeKey(key)))
        .map(bs => serializer.deserialize(bs.toArray, clazz))
        .getOrElse(null.asInstanceOf[T])
    }

    /**
     * 캐시를 저장합니다.
     */
    override def put(key: Any, value: Any) {
        log.trace(s"Spring Cache를 저장합니다. key=$key, value=$value")
        val keyStr = computeKey(key)

        waitForLock(redis)
        withTransaction { tx =>
            tx.set(keyStr, serializer.serialize(value))
            tx.zadd(setName, (0.0, keyStr))
            if (expiration > 0) {
                tx.expire(keyStr, expiration)
                // 최신 캐시 항목에 의해 설정되기 때문에 매번 최대 값이 설정된다고 보면 된다.
                tx.expire(setName, expiration)
            }
        }
    }

    /**
    * 캐시 항목을 삭제합니다.
    */
    override def evict(key: Any) {
        log.trace(s"캐시를 삭제합니다. key=$key")
        val keyStr = computeKey(key)
        withTransaction {
            tx =>
                tx.del(keyStr)
                tx.zrem(setName, keyStr)
        }
    }

    /**
    * 모든 캐시 항목을 모두 삭제한다.
    */
    override def clear() {
        try {
            doClear()
        } catch {
            case e: Throwable => log.warn(s"캐시를 삭제하는데 예외가 발생했습니다. name=$name", e)
        }
    }

    private def doClear() {
        log.trace(s"Spring cache를 모두 제거합니다... name=$name")

        if (Promises.await(redis.exists(cacheLockName))) {
            return
        }

        try {
            // Lock을 설정한다. 다른 clear 작업을 못하도록...
            redis.set(cacheLockName, cacheLockName)

            var offset = 0
            var finished = false

            do {
                val futureKeys = redis.zrange(setName, offset * PAGE_SIZE, (offset + 1) * PAGE_SIZE - 1)
                val keys = Promises.await(futureKeys).map(x => x.utf8String)

                finished = keys.size < PAGE_SIZE
                offset += 1

                // 캐시 삭제
                if (keys.nonEmpty) {
                    Promises.await(redis.del(keys.map(key => computeKey(key)): _*))
                }
            } while (!finished)

            redis.del(setName)
        } finally {
            log.trace(s"Lock을 제거합니다. lock=$cacheLockName")
            redis.del(cacheLockName)
        }

        log.debug(s"Spring cache를 모두 삭제했습니다. name=$name")
    }


    def computeKey(key: Any): String = {
        prefix + key.toString
    }

    private def waitForLock(redis: RedisClient): Boolean = {
        var retry = false
        var foundLock = false
        do {
            retry = false
            if (Promises.await(redis.exists(cacheLockName))) {
                foundLock = true
                try {
                    Thread.sleep(waitTimeoutForLock)
                } catch {
                    case ignored: InterruptedException =>
                    case e: Exception => throw new RuntimeException("Fail to wait for lock.", e)
                }
                retry = true
            }
        } while (retry)
        foundLock
    }


    private def withTransaction(block: TransactionBuilder => Unit): Future[MultiBulk] = {
        val tx = redis.transaction()
        block(tx)
        tx.exec()
    }
}

object RedisCache {

    implicit val akkaSystem = akka.actor.ActorSystem()

    def apply(name: String, prefix: String, redis: RedisClient, expiration: Long): RedisCache =
        new RedisCache(name, prefix, redis, expiration)

    def apply(name: String,
              prefix: String,
              expiration: Long = 0,
              host: String = "locahost",
              port: Int = 6379): RedisCache = {
        val redis = RedisClient(host, port)
        new RedisCache(name, prefix, redis, expiration)
    }
}
