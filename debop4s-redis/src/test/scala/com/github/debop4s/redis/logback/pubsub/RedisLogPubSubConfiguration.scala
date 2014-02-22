package com.github.debop4s.redis.logback.pubsub

import com.github.debop4s.redis.RedisConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import redis.RedisClient

/**
 * RedisLogPubSubConfiguration
 * Created by debop on 2014. 2. 22.
 */
@Configuration
class RedisLogPubSubConfiguration extends RedisConfiguration {

    @Autowired private val redis: RedisClient = null

}
