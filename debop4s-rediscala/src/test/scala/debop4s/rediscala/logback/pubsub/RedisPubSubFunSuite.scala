package debop4s.rediscala.logback.pubsub

import java.net.InetSocketAddress

import akka.actor.Props
import debop4s.rediscala.AbstractRedisFunSuite
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * RedisPubSubTest
 * Created by debop on 2014. 2. 22.
 */
class RedisPubSubFunSuite extends AbstractRedisFunSuite {

  test("pub/sub test") {
    actorSystem.scheduler.schedule(10 millis, 50 millis) {
      redis.publish("time", System.currentTimeMillis())
    }
    actorSystem.scheduler.schedule(10 millis, 100 millis) {
      redis.publish("pattern.match", "pattern value")
    }

    val channels = Seq("time")
    val patterns = Seq("pattern.*")

    actorSystem.actorOf(Props(classOf[SubscribeActor], channels, patterns).withDispatcher("rediscala.rediscala-client-worker-dispatcher"))

    Thread.sleep(1000)
  }
}

class SubscribeActor(channels: Seq[String] = Nil, patterns: Seq[String] = Nil)
  extends RedisSubscriberActor(new InetSocketAddress("localhost", 6379), channels, patterns) {

  override def onMessage(m: Message) {
    log.debug(s"message received: $m")
  }

  override def onPMessage(pm: PMessage) {
    log.debug(s"pattern message received: $pm")
  }
}
