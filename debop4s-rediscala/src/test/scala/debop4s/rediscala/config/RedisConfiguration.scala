package debop4s.rediscala.config

import com.typesafe.config.ConfigFactory
import debop4s.rediscala.RedisClientFactory
import debop4s.rediscala.client._
import debop4s.rediscala.utils.AkkaUtil._
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import redis.{RedisClient, RedisClientMasterSlaves, RedisClientPool}

/**
 * Redis 서버를 사용하기 위한 환경설정입니다.
 */
@Configuration
@ComponentScan(basePackages = Array("debop4s.rediscala.set"), excludeFilters = Array(new Filter(Array(classOf[Configuration]))))
class RedisConfiguration {

  @Bean
  def appConfig: AppConfig = {
    val config = ConfigFactory.load("config/local.conf")
    AppConfig(config.getConfig("healthon"))
  }

  // NOTE: RedisClient를 Bean으로 등록하면, destroy 시에 redis server를 죽입니다.
  // NOTE: 이를 방지하기 위해 destroyMethod 를 지정하지 않도록 합니다.
  @Bean(destroyMethod = "")
  def redisClient: RedisClient = {
    RedisClientFactory.createClient(appConfig.redis.master)
  }

  @Bean(destroyMethod = "")
  def redisClientPool: RedisClientPool = {
    val server = RedisClientFactory.createServer(appConfig.redis.master)
    RedisClientFactory.createPool(server, 4)
  }

  @Bean(destroyMethod = "")
  def redisClientMasterSlaves: RedisClientMasterSlaves = {
    val master = appConfig.redis.master
    val slaves = appConfig.redis.slaves
    RedisClientFactory.createMasterSlaves(master, slaves)
  }

  //NOTE: 사용하지 마세요. (테스트 시에만 사용하세요)
  @Bean(destroyMethod = "")
  def redisSyncClient: RedisSyncClient = {
    if (isMasterSlaves) RedisSyncMasterSlavesClient(redisClientMasterSlaves)
    else RedisSyncClient(redisClient)
  }

  @Bean(destroyMethod = "")
  def jredisClient: JRedisClient = {
    if (isMasterSlaves) JRedisMasterSlavesClient(redisClientMasterSlaves)
    else JRedisClient(redisClient)
  }
  //
  //  //NOTE: 사용하지 마세요.
  //  @Bean(destroyMethod = "")
  //  def jredisSyncClient: JRedisSyncClient = {
  //    if (isMasterSlaves) JRedisSyncMasterSlavesClient(JRedisMasterSlavesClient(redisClientMasterSlaves))
  //    else JRedisSyncClient(jredisClient)
  //  }

  private def isMasterSlaves: Boolean = {
    val slaves = appConfig.redis.slaves
    slaves != null && slaves.length > 0
  }

}
