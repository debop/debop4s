package debop4s.rediscala.spring

import java.util.concurrent.TimeUnit

import debop4s.core.concurrent._
import debop4s.rediscala.serializer.SnappyFstValueFormatter
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.Cache.ValueWrapper
import org.springframework.cache.support.SimpleValueWrapper
import redis.RedisClient
import redis.api.Limit

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NonFatal

/**
 * Redis를 캐시 서버로 사용하는 Spring @Cache 를 지원하는 Cache 입니다.
 * Created by debop on 2014. 2. 22.
 */
class RedisCache(val name: String,
                 prefix: String,
                 val redis: RedisClient,
                 expiration: Long = 0) extends Cache {

  private lazy val log = LoggerFactory.getLogger(getClass)

  log.info(s"Create RedisCache name=$name, prefix=$prefix, expiration=$expiration, redis=$redis")

  // cache value 를 압축저장하기 위해 Snappy 압축과 Fst Serialization을 사용합니다.
  implicit val valueFormatter = new SnappyFstValueFormatter[Any]()

  // redis 응답을 기다리는 기본 timeout 입니다.
  implicit val timeout = FiniteDuration(30, TimeUnit.SECONDS)

  // 캐시 키의 expiration을 관리하기 위해 따로 sorted set으로 관리합니다.
  val setName = s"spring:cache:keys:$name"

  // 캐시 값을 관리하기 위해 Hash Set 을 사용합니다.
  val itemName = s"spring:cache:items:$name"

  // 캐시 삭제 시 lock을 걸어 유효하지 못한 데이터를 삭제하기 위해 사용합니다.
  val cacheLockName = s"cache:lock:$name"

  var waitTimeoutForLock = 5 // msec

  override def getNativeCache: AnyRef = redis

  override def getName: String = name

  /** redis hash set에 저장된 캐시 항목을 조회합니다. */
  def get(key: Any): ValueWrapper = {
    val keyStr = computeKey(key)
    log.trace(s"캐시 조회. redis hashset=$itemName, field=$keyStr")

    waitForLock(redis)

    redis.hget[Any](itemName, keyStr).map(_.fold(null.asInstanceOf[ValueWrapper])(v => new SimpleValueWrapper(v))).await
    //    redis.hget[Any](itemName, keyStr).await
    //    .fold(null.asInstanceOf[ValueWrapper]) {
    //      value => new SimpleValueWrapper(value)
    //    }
  }

  /** redis hash set에 저장된 캐시 항목을 조회합니다. */
  def get[@miniboxed T](key: Any, clazz: Class[T]): T = {
    val keyStr = computeKey(key)
    log.trace(s"캐시 조회. redis hashset=$itemName, field=$keyStr, clazz=${ clazz.getSimpleName }")

    waitForLock(redis)
    redis.hget[Any](itemName, keyStr).map(_.orNull.asInstanceOf[T]).await
  }

  /**
   * 캐시 값을 hash set에 저장하고,
   * 캐시 expiration 관리를 위해 expiration 정보를 sorted set에 따로 저장합니다.
   */
  private def putAsync(key: Any, value: Any): Future[Boolean] = {
    val keyStr = computeKey(key)
    log.trace(s"Spring Cache를 저장합니다. redis hashset=$itemName, field=$keyStr, value=$value")

    waitForLock(redis)

    val f: Future[Boolean] = async {
      // 캐시 값 저장
      val r = await(redis.hset(itemName, keyStr, value))
      // 캐시 값의 Expiration 저장
      if (expiration > 0) {
        await(redis.zadd(setName, (System.currentTimeMillis() + expiration, keyStr)))
      }
      r
    }
    f onFailure { case e => log.error(s"cannot put $key", e) }
    f
  }

  override def put(key: Any, value: Any): Unit = {
    putAsync(key, value).stay
  }


  /** 해당 키에 캐시 값이 없으면 저장하고, 기존 캐시 값을 반환합니다. */
  override def putIfAbsent(key: scala.Any, value: scala.Any): ValueWrapper = {
    val result = get(key)
    if (result == null) {
      putAsync(key, value)
    }
    result
  }

  /**
   * 캐시 항목을 삭제합니다.
   */
  override def evict(key: Any) {
    val keyStr = computeKey(key)
    log.trace(s"캐시를 삭제합니다. redis hashset=$itemName, field=$keyStr")
    async {
      await(redis.hdel(itemName, keyStr))
      await(redis.zrem(setName, keyStr))
    }.stay
  }

  /**
   * 모든 캐시 항목을 모두 삭제한다.
   */
  override def clear() {
    try {
      doClear()
    } catch {
      case NonFatal(e) =>
        log.warn(s"Spring cache를 삭제하는데 예외가 발생했습니다. name=$name, hashset=$itemName", e)
    }
  }

  /**
   * 유효기간이 지난 캐시들을 찾아 삭제합니다.
   */
  def deleteExpiredItems(): Unit = {
    try {
      val expireTime = System.currentTimeMillis()
      async {
        val keys = await(redis.zrangebyscore[String](setName, Limit(0.0), Limit(expireTime)))

        if (keys.nonEmpty) {
          log.trace(s"유효기간이 지난 캐시 키를 삭제합니다. keys=$keys")
          await { redis.hdel(itemName, keys: _*) }
          await { redis.zrem(setName, keys: _*) }
        }
      }.stay
    } catch {
      case NonFatal(e) =>
        log.warn(s"Expired cache item을 삭제하는데 실패했습니다.", e)
    }
  }

  /**
   * 현재 수행 중인 캐시 정보를 모두 삭제합니다.
   */
  private def doClear() {
    log.trace(s"Spring cache를 모두 제거합니다... name=$name, itemName=$itemName, setName=$setName")

    // 현재 다른 Lock 이 걸려 있으면 작업을 취소한다.
    if (redis.exists(cacheLockName).await)
      return

    try {
      // Lock을 설정한다. 다른 clear 작업을 못하도록...
      redis.set(cacheLockName, cacheLockName)

      async {
        await(redis.del(itemName))
        await(redis.del(setName))
        log.debug(s"Spring cache를 모두 제거했습니다. name=$name, itemName=$itemName, setName=$setName")
      }.stay

    } finally {
      log.trace(s"Lock을 제거합니다. lock=$cacheLockName")
      redis.del(cacheLockName).await
    }
  }

  def computeKey(key: Any): String = {
    prefix + key.toString
  }

  /**
   * 캐시 값 삭제 작업 중에 Lock 을 걸어 사용하지 못하게 한다. 사용하려면, 이 Lock 이 해제될 때를 기다려야 한다.
   */
  private def waitForLock(redis: RedisClient): Boolean = {
    var retry = false
    var foundLock = false
    do {
      retry = false
      if (redis.exists(cacheLockName).await) {
        foundLock = true
        try {
          Thread.sleep(waitTimeoutForLock)
        } catch {
          case ignored: InterruptedException =>
          case NonFatal(e) =>
            throw new RuntimeException(s"Spring cache의 Lock 이 해제되기를 기다리는 동안 예외가 발생했습니다. name=$name", e)
        }
        retry = true
      }
    } while (retry)
    foundLock
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
            port: Int = 6379,
            database: Option[Int] = Some(0)): RedisCache = {
    val redis = RedisClient(host, port, db = database)
    new RedisCache(name, prefix, redis, expiration)
  }
}
