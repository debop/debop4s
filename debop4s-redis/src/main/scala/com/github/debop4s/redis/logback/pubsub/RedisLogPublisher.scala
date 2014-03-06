package com.github.debop4s.redis.logback.pubsub

import ch.qos.logback.classic.spi.LoggingEvent
import com.github.debop4s.core.parallels.Promises
import com.github.debop4s.redis.RedisConsts
import com.github.debop4s.redis.logback.RedisAppender
import scala.concurrent.Future

/**
 * RedisLogPublisher
 * Created by debop on 2014. 2. 22.
 */
class RedisLogPublisher extends RedisAppender {

    var channel = RedisConsts.DEFAULT_LOGBACK_CHANNEL

    def setChannel(channel: String) {
        this.channel = channel
    }

    override def append(eventObject: LoggingEvent) {

        Promises.exec[Future[Long]] {
            val doc = createLogDocument(eventObject)
            val jsonDoc = toJsonText(doc)

            redis.publish(channel, jsonDoc)
        }
    }
}
