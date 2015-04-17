package org.hibernate.cache.rediscala.utils

import org.hibernate.cache.rediscala._
import org.hibernate.cache.rediscala.config.{RedisConfig, RedisServerElement}
import org.slf4j.LoggerFactory
import redis._

/**
 * Redis Client 에 대한 Factory
 * @author sunghyouk.bae@gmail.com
 */
private[rediscala] object RedisClientFactory {

  private lazy val log = LoggerFactory.getLogger(getClass)

  /**
   * 환경설정 정보 `RedisElement` 로부터  `RedisServer` 를 생성합니다.
   */
  def createServer(element: RedisServerElement): RedisServer = {
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
  def createClient(element: RedisServerElement): RedisClient = {
    log.info(s"Create RedisClient. redisElement=$element")

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

  def createMasterSlaves(redisConfig: RedisConfig): RedisClientMasterSlaves = {
    val master = redisConfig.master
    val slaves = redisConfig.slaves

    val masterServer = createServer(master)

    // slave 서버가 정의가 안된 경우 읽기 성능향상을 위해 master를 추가하도록 한다.
    val slaveServers =
      if (slaves == null || slaves.size == 0) {
        Seq(masterServer.copy())
      } else {
        slaves.map { slave =>
          createServer(slave)
        }
      }

    RedisClientMasterSlaves(masterServer, slaveServers)
  }

  def createMasterSlaves(masterElement: RedisServerElement,
                         slaveElements: Seq[RedisServerElement]): RedisClientMasterSlaves = {

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
