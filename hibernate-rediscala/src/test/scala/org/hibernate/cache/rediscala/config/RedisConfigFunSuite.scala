package org.hibernate.cache.rediscala.config

import com.typesafe.config.ConfigFactory
import org.scalatest.{FunSuite, Matchers}

/**
 * RedisConfigFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RedisConfigFunSuite extends FunSuite with Matchers {

  test("load hibernate-redis.conf") {

    val config = ConfigFactory.load("hibernate-redis.conf")
    val redisConfig = RedisConfig(config.getConfig("hibernate-redis"))

    redisConfig.master.host should not be null
    redisConfig.slaves.size should be >= 0
    redisConfig.existExpiryInSeconds shouldEqual true

    redisConfig.master.database shouldEqual 1
  }

}
