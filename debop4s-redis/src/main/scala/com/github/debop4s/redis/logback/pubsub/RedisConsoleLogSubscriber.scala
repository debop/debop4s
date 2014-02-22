package com.github.debop4s.redis.logback.pubsub

import com.github.debop4s.core.json.JacksonSerializer
import com.github.debop4s.core.logback.LogDocument
import java.net.InetSocketAddress
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}

/**
 * logback log message 를 Redis PubSub channel에서 받아와서 console에 씁니다.
 * Created by debop on 2014. 2. 22.
 */
class RedisConsoleLogSubscriber(override val address: InetSocketAddress,
                                val channels: Seq[String] = Seq("channel:logback:logs"),
                                val patterns: Seq[String] = Nil)

    extends RedisSubscriberActor(address, channels, patterns) {

    private val serializer = JacksonSerializer()

    override def onMessage(m: Message) {
        try {
            val doc = serializer.deserializeFromText(m.data, classOf[LogDocument])
            print(s"Subscriber: ${doc.timestamp} [${doc.levelStr}] ${doc.message} ${doc.stacktrace}")
        } catch {
            case e: Throwable => System.err.print(e)
        }
    }

    override def onPMessage(pm: PMessage) {
        try {
            val doc = serializer.deserializeFromText(pm.data, classOf[LogDocument])
            print(s"Subscriber: ${doc.timestamp} [${doc.levelStr}] ${doc.message} ${doc.stacktrace}")
        } catch {
            case e: Throwable => System.err.print(e)
        }
    }
}

object RedisConsoleLogSubscriber {

    def apply(address: InetSocketAddress = new InetSocketAddress("localhost", 6379),
              channels: Seq[String] = Seq("channel:logback:logs"),
              patterns: Seq[String] = Nil): RedisConsoleLogSubscriber = {
        new RedisConsoleLogSubscriber(address, channels, patterns)
    }
}
