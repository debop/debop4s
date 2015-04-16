package debop4s.rediscala.client

import java.util.{List => JList}

import debop4s.rediscala._
import redis._
import redis.api.{LimitOffsetCount, Order}
import redis.commands.Transactions

import scala.collection.JavaConverters._
import scala.concurrent.Future

object JRedisMasterSlavesClient {
  def apply(): JRedisMasterSlavesClient = {
    val redisServer = RedisServer()
    new JRedisMasterSlavesClient(RedisClientMasterSlaves(redisServer, Seq(redisServer.copy())))
  }

  def apply(host: String, port: Int, password: Option[String] = None, db: Option[Int] = None): JRedisMasterSlavesClient = {
    val redisServer = RedisServer(host, port, password, db)
    val redisMasterSlaves = RedisClientMasterSlaves(redisServer, Seq(redisServer.copy()))
    new JRedisMasterSlavesClient(redisMasterSlaves)
  }

  def apply(redis: RedisClientMasterSlaves): JRedisMasterSlavesClient = {
    new JRedisMasterSlavesClient(redis)
  }
}

/**
 * Java를 위한 Redis Master-Slave 용 Client 입니다.
 * @author sunghyouk.bae@gmail.com
 */
class JRedisMasterSlavesClient(private[this] val _redis: RedisClientMasterSlaves)
  extends JRedisClient(_redis.masterClient) with JRedisSupport with JRedisTransactionalSupport {

  def this() = this(RedisClientMasterSlaves(RedisServer(), Seq()))

  require(_redis != null)

  def hasSlaves: Boolean = _redis.slaves != null && _redis.slaves.length > 0

  override lazy val transactionalRedis: Transactions = _redis

  override val redis: RedisCommands = {
    if (hasSlaves) _redis
    else {
      _redis.masterClient
      //RedisClientPool((0 until sys.runtime.availableProcessors()).map(i => _redis.master.copy()))
    }
  }

  lazy val masterClient: RedisClient = _redis.masterClient

  /**
   * sort 는 Master에서 수행해야 되는데, RedisScala에서 Slave 를 사용해서 예외를 발생시킵니다.
   * 이를 회피하기 위해 sort 메소드는 master 에서 작업하도록 재정의했습니다.
   */
  override def sort(key: String): Future[JList[String]] = {
    log.trace(s"Sort key=$key by client=${ _redis.masterClient }")
    masterClient.sort[String](key).map(_.asJava)
  }
  override def sort(key: String, order: Order): Future[JList[String]] = {
    masterClient.sort[String](key, order = Some(order)).map(_.asJava)
  }
  override def sort(key: String, alpha: Boolean): Future[JList[String]] = {
    masterClient.sort[String](key, alpha = alpha).map(_.asJava)
  }

  override def sort(key: String, limit: LimitOffsetCount): Future[JList[String]] = {
    masterClient.sort[String](key, limit = Some(limit)).map(_.asJava)
  }

  override def sort(key: String,
                    order: Order,
                    alpha: Boolean,
                    limit: LimitOffsetCount): Future[JList[String]] = {
    masterClient.sort[String](key, order = Some(order), alpha = alpha, limit = Some(limit)).map(_.asJava)
  }
}
