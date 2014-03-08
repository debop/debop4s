package com.github.debop4s.redis

import org.springframework.context.annotation.{Bean, Configuration}
import redis.RedisClient

/**
 * RedisConfiguration
 * Created by debop on 2014. 2. 22.
 */
@Configuration
class RedisConfiguration {

  implicit val akkaSystem = akka.actor.ActorSystem()

  // BUG: RedisClient를 Bean으로 등록하면, destroy 시에 Redis Server를 죽인다.
  //
  @Bean(destroyMethod = "")
  def redisClient(): RedisClient = RedisClient()

}
