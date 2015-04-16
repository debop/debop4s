package debop4s.rediscala.client

import java.lang.{Double => JDouble, Iterable => JIterable, Long => JLong}
import java.util.{List => JList, Map => JMap, Set => JSet}

import debop4s.rediscala._
import org.slf4j.LoggerFactory
import redis._
import redis.commands.Transactions

object JRedisClient {

  def apply(): JRedisClient = apply(RedisClient())

  def apply(host: String, port: Int): JRedisClient =
    apply(RedisClient(host, port))

  def apply(redisClient: RedisClient): JRedisClient = {
    require(redisClient != null)
    new JRedisClient(redisClient)
  }
}

/**
 * Redis 서버를 이용하기 위한 비동기 통신을 수행하는 client 입니다.
 * [[RedisClient]] 가 scala 전용의 수형을 사용하므로, Java에서도 쉽게 사용할 수 있도록 Wrapping 했습니다.
 *
 * @param _redis [[RedisClient]] instance.
 * @author Sunghyouk Bae
 */
class JRedisClient(private[this] val _redis: RedisClient) extends JRedisSupport with JRedisTransactionalSupport {

  def this() = this(RedisClient())

  require(_redis != null)

  protected lazy val log = LoggerFactory.getLogger(getClass)

  override val redis: RedisCommands = {
    _redis
    //    def server = RedisServer(_redis.host, _redis.port, db = _redis.db)
    //    RedisClientPool((0 until sys.runtime.availableProcessors()).map(i => server))
  }

  override lazy val redisBlocking: RedisBlockingClient =
    RedisBlockingClient(
      host = _redis.host,
      port = _redis.port,
      password = _redis.password,
      db = _redis.db,
      name = _redis.name
    )

  override lazy val transactionalRedis: Transactions = _redis
}


