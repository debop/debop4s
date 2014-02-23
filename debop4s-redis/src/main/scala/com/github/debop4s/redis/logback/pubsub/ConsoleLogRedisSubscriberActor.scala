package com.github.debop4s.redis.logback.pubsub

import com.github.debop4s.core.json.ScalaJacksonSerializer
import com.github.debop4s.core.logback.LogDocument
import com.github.debop4s.core.parallels.Promises
import java.net.InetSocketAddress
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * logback log message 를 Redis PubSub channel에서 받아와서 console에 쓰도록 하는 Subscriber 입니다.
 *
 *
 * Created by debop on 2014. 2. 22.
 */
class ConsoleLogRedisSubscriberActor(override val address: InetSocketAddress,
                                     val channels: Seq[String] = Seq("channel:logback:logs"),
                                     val patterns: Seq[String] = Nil)
    extends RedisSubscriberActor(address, channels, patterns) {

    private val serializer = ScalaJacksonSerializer()

    /**
    * Redis Pub/Sub Channel에서 메시지를 받았을 때 호출되는 메소드입니다.
    */
    override def onMessage(m: Message) {
        Promises.startNew[Boolean] {
            val doc = serializer.deserializeFromText(m.data, classOf[LogDocument])

            println(s"Received Log Document: ${doc.timestamp} [${doc.levelStr}] ${doc.message}")
            if (doc.stacktrace != null && doc.stacktrace.size > 0)
                println(doc.stacktrace)
            true
        }
    }

    /**
    * Redis Pub/Sub Channel에서 패턴 메시지를 받았을 때 호출되는 메소드입니다.
    */
    override def onPMessage(pm: PMessage) {
        Promises.startNew[Boolean] {
            val doc = serializer.deserializeFromText(pm.data, classOf[LogDocument])

            println(s"Received pattern message: ${doc.timestamp} [${doc.levelStr}] ${doc.message}")
            if (doc.stacktrace != null && doc.stacktrace.size > 0)
                println(doc.stacktrace)
            true
        }
    }
}

object ConsoleLogRedisSubscriberActor {

    def apply(address: InetSocketAddress = new InetSocketAddress("localhost", 6379),
              channels: Seq[String] = Seq("channel:logback:logs"),
              patterns: Seq[String] = Nil): ConsoleLogRedisSubscriberActor = {
        new ConsoleLogRedisSubscriberActor(address, channels, patterns)
    }
}
