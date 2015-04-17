package debop4s.shiro.redis

import debop4s.rediscala.client.RedisSyncClient
import org.apache.shiro.SecurityUtils
import org.apache.shiro.config.IniSecurityManagerFactory
import org.apache.shiro.mgt.{DefaultSecurityManager, SecurityManager}
import org.apache.shiro.session.mgt.DefaultSessionManager
import org.springframework.context.annotation.{Bean, Configuration}
import redis.RedisClient

/**
 * ShiroRedisConfig
 * @author sunghyouk.bae@gmail.com
 */
@Configuration
class ShiroRedisConfig {

  implicit val akkaSystem = akka.actor.ActorSystem()

  @Bean(destroyMethod = "")
  def redisClient: RedisClient = RedisClient()

  @Bean(destroyMethod = "")
  def redisSyncClient: RedisSyncClient = RedisSyncClient(redisClient)

  @Bean
  def redisCacheManager: RedisCacheManager =
    new RedisCacheManager()

  @Bean
  def redisSessionDAO: RedisSessionRepository = {
    val dao = new RedisSessionRepository
    dao.setKeyPrefix("shiro:session:")
    dao.setTimeout(30 * 60 * 1000L)
    dao
  }

  @Bean
  def redisSessionManager: DefaultSessionManager = {
    val sessMan = new DefaultSessionManager()
    sessMan.setSessionDAO(redisSessionDAO)
    sessMan
  }

  @Bean
  def securityManager: SecurityManager = {
    val factory = new IniSecurityManagerFactory("classpath:shiro.ini")
    val sm = factory.getInstance().asInstanceOf[DefaultSecurityManager]

    sm.setSessionManager(redisSessionManager)
    sm.setCacheManager(redisCacheManager)

    SecurityUtils.setSecurityManager(sm)
    sm
  }

}
