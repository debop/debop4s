package debop4s.rediscala.client

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.rediscala._
import redis._
import redis.api.{LimitOffsetCount, Order}
import redis.commands.Transactions

@deprecated(message = "동기방식은 삭제할 것임", since = "2.0.0")
object RedisSyncMasterSlavesClient {

  def apply(): RedisSyncMasterSlavesClient = {
    val redisServer = RedisServer()
    new RedisSyncMasterSlavesClient(RedisClientMasterSlaves(redisServer, Seq(redisServer.copy())))
  }

  def apply(host: String,
            port: Int,
            password: Option[String] = None,
            db: Option[Int] = None): RedisSyncMasterSlavesClient = {
    val redisServer = RedisServer(host, port, password, db)
    val redisMasterSlaves = RedisClientMasterSlaves(redisServer, Seq(redisServer.copy()))
    new RedisSyncMasterSlavesClient(redisMasterSlaves)
  }

  def apply(redis: RedisClientMasterSlaves): RedisSyncMasterSlavesClient = {
    new RedisSyncMasterSlavesClient(redis)
  }
}

/**
 * RedisMasterSlaveSyncClient
 * @author sunghyouk.bae@gmail.com
 */
@deprecated(message = "동기방식은 삭제할 것임", since = "2.0.0")
class RedisSyncMasterSlavesClient(private[this] val _redis: RedisClientMasterSlaves)
  extends RedisSyncClient(_redis.masterClient) {

  def this() = this(RedisClientMasterSlaves(RedisServer(), Seq()))

  require(_redis != null)

  def hasSlaves: Boolean = _redis.slaves != null && _redis.slaves.length > 0

  override val redisTx: Transactions = _redis.masterClient

  override val redis: RedisCommands = {
    if (hasSlaves) _redis
    else {
      _redis.masterClient
      // RedisClientPool((0 until sys.runtime.availableProcessors()).map(i => _redis.master.copy()))
    }
  }


  /**
   * sort 는 Master에서 수행해야 되는데, RedisScala에서 Slave 를 사용해서 예외를 발생시킵니다.
   * 이를 회피하기 위해 sort 메소드는 master 에서 작업하도록 재정의했습니다.
   */
  override def sort[R: ByteStringDeserializer](key: String,
                                               byPattern: Option[String],
                                               limit: Option[LimitOffsetCount],
                                               getPatterns: Seq[String],
                                               order: Option[Order],
                                               alpha: Boolean): Seq[R] = {
    _redis.masterClient.sort(key, byPattern, limit, getPatterns, order, alpha).await
  }

}
