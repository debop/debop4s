package com.github.debop4s.redis.logback.pubsub

import ch.qos.logback.classic.spi.LoggingEvent
import com.github.debop4s.redis.RedisConsts
import com.github.debop4s.redis.logback.RedisAppender
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 * 로그 정보를 Redis PUB/SUB Channel 로 publish 합니다.
 * Created by debop on 2014. 2. 22.
 */
class RedisLogPublisher extends RedisAppender {

    var channel = RedisConsts.DEFAULT_LOGBACK_CHANNEL

    def setChannel(channel: String) {
        this.channel = channel
    }

    override def append(eventObject: LoggingEvent) {
        val f = future {
            val doc = createLogDocument(eventObject)
            toJsonText(doc)
        }
        f onSuccess {
            case text: String => redis.publish(channel, text)
        }
    }
}
