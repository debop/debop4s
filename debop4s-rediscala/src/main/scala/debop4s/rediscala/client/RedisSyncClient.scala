package debop4s.rediscala.client

import redis._
import redis.commands.Transactions

/**
 * 동기 방식으로 Redis와 통신하는 Redis Client 입니다.
 */
@deprecated(message = "동기방식은 삭제할 것임", since = "2.0.0")
object RedisSyncClient {

  def apply(): RedisSyncClient = new RedisSyncClient(RedisClient())

  def apply(host: String, port: Int): RedisSyncClient =
    new RedisSyncClient(RedisClient(host, port))

  def apply(redis: RedisClient): RedisSyncClient =
    new RedisSyncClient(redis)
}

/**
 * 동기 방식으로 Redis와 통신하는 Redis Client 입니다.
 * @author debop created at 2014. 4. 29.
 */
@deprecated(message = "동기방식은 절대 사용하지 마세요. 삭제할 것임", since = "2.0.0")
class RedisSyncClient(private[this] val _redis: RedisClient)
  extends RedisSynchronizedSupport with RedisTransactionalSupport {

  def this() = this(RedisClient())

  require(_redis != null)

  override val redisTx: Transactions = _redis

  override val redis: RedisCommands = {
    _redis
    //    def server = RedisServer(_redis.host, _redis.port, db = _redis.db)
    //    RedisClientPool((0 until sys.runtime.availableProcessors()).map(i => server))
  }

  override lazy val redisBlocking =
    RedisBlockingClient(host = _redis.host,
      port = _redis.port,
      password = _redis.password,
      db = _redis.db,
      name = _redis.name
    )
}
