package com.github.debop4s.redis.logback.pubsub

import akka.actor.Props
import com.github.debop4s.redis.AbstractRedisTest
import java.net.InetSocketAddress
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * RedisPubSubTest
 * Created by debop on 2014. 2. 22.
 */
class RedisPubSubTest extends AbstractRedisTest {

    test("pub/sub test") {
        akkaSystem.scheduler.schedule(10 millis, 50 millis) {
            redis.publish("time", System.currentTimeMillis())
        }
        akkaSystem.scheduler.schedule(10 millis, 100 millis) {
            redis.publish("pattern.match", "pattern value")
        }

        val channels = Seq("time")
        val patterns = Seq("pattern.*")

        akkaSystem.actorOf(Props(classOf[SubscribeActor], channels, patterns).withDispatcher("rediscala.rediscala-client-worker-dispatcher"))

        Thread.sleep(1000)
    }
}

class SubscribeActor(channels: Seq[String] = Nil, patterns: Seq[String] = Nil)
    extends RedisSubscriberActor(new InetSocketAddress("localhost", 6379), channels, patterns) {

    override def onMessage(m: Message) {
        println(s"message received: $m")
    }

    override def onPMessage(pm: PMessage) {
        println(s"pattern message received: $pm")
    }
}
