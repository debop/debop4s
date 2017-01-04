package debop4s.rediscala

import debop4s.config.server.{RedisElement, RedisServerElement}
import org.slf4j.LoggerFactory
import redis._
import redis.api.pubsub.Message

/**
 * Redis Client 에 대한 Factory
 * @author sunghyouk.bae@gmail.com
 */
object RedisClientFactory {

  private lazy val log = LoggerFactory.getLogger(getClass)

  /**
   * 환경설정 정보 `RedisElement` 로부터  `RedisServer` 를 생성합니다.
   */
  def createServer(element: RedisElement): RedisServer = {
    log.debug(s"create RedisSerer. host=${ element.host }, port=${ element.port }, db=${ element.database }")
    RedisServer(element.host, element.port, db = Some(element.database))
  }

  /**
   * `RedisServer` 를 생성합니다.
   */
  def createServer(host: String = "localhost",
                   port: Int = 6739,
                   password: Option[String] = None,
                   db: Option[Int] = None): RedisServer = {
    RedisServer(host, port, password, db)
  }

  /**
   * `RedisServer` 를 생성합니다.
   */
  def createServer(client: RedisClient): RedisServer = {
    RedisServer(client.host, client.port, client.password, client.db)
  }

  /**
   * `RedisClient` 를 생성합니다.
   */
  def createClient(element: RedisElement): RedisClient = {
    log.info(s"Create RedisClient. redisElement={}", element)

    RedisClient(element.host, element.port, db = Some(element.database))
  }

  def createPool(server: RedisServer, poolSize: Int = 4): RedisClientPool = {
    val servers = (0 until poolSize).map { _ => server.copy() }
    RedisClientPool(servers)
  }

  /**
   * `RedisClientPool` 을 생성합니다.
   */
  def createPool(servers: Seq[RedisServer], name: String): RedisClientPool = {
    RedisClientPool(servers, name)
  }

  def createMasterSlaves(redisConfig: RedisServerElement): RedisClientMasterSlaves = {
    val master = redisConfig.master
    val slaves = redisConfig.slaves

    val masterServer = createServer(master)

    // slave 서버가 정의가 안된 경우 읽기 성능향상을 위해 master를 추가하도록 한다.
    val slaveServers =
      if (slaves == null || slaves.isEmpty) {
        Seq(masterServer.copy())
      } else {
        slaves.map { slave =>
          createServer(slave)
        }
      }

    RedisClientMasterSlaves(masterServer, slaveServers)
  }

  def createMasterSlaves(masterElement: RedisElement,
                         slaveElements: Seq[RedisElement]): RedisClientMasterSlaves = {

    val master = createServer(masterElement)
    val slaves =
      if (slaveElements.isEmpty) Seq(master.copy())
      else slaveElements.map { elem => createServer(elem) }

    RedisClientMasterSlaves(master, slaves)
  }
  def createMasterSlaves(masterServer: RedisServer,
                         slaveServers: Seq[RedisServer]): RedisClientMasterSlaves = {

    val slaves =
      if (slaveServers.isEmpty) Seq(masterServer.copy())
      else slaveServers

    RedisClientMasterSlaves(masterServer, slaves)
  }


  /**
   * Redis PubSub 구성의 Subscriber 를 생성합니다. (참고: `RedisPubSub`)
   */
  def createPubSub(host: String, port: Int, channels: Seq[String], patterns: Seq[String] = Nil)
                  (onMessageBlock: (Message) => Unit): RedisPubSub = {
    RedisPubSub(host, port, channels, patterns, onMessage = onMessageBlock)
  }

  def createBlockingClient(server: RedisServer, name: String = "RedisBlockingClient"): RedisBlockingClient = {
    RedisBlockingClient(server.host,
      server.port,
      password = server.password,
      db = server.db,
      name = name)
  }

  def createBlockingClient(redisClient: RedisClient): RedisBlockingClient = {
    createBlockingClient(createServer(redisClient))
  }


}
