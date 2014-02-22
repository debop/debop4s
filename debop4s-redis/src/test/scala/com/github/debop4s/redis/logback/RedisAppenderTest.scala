package com.github.debop4s.redis.logback

import org.springframework.test.context.TestContextManager
import com.github.debop4s.redis.AbstractRedisTest
import org.slf4j.LoggerFactory
import redis.RedisClient

/**
 * RedisAppenderTest
 * Created by debop on 2014. 2. 22.
 */
class RedisAppenderTest extends AbstractRedisTest {

    lazy val log = LoggerFactory.getLogger(getClass)

    implicit val akkaSystem = akka.actor.ActorSystem()

    private val redis: RedisClient = RedisClient()

    test("logging message") {
        log.debug("appender test")
    }

    test("clear logs") {
        redis.del(RedisAppender.DEFAULT_KEY)
    }
}
