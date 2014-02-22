package com.github.debop4s.redis.logback.pubsub

import akka.actor.Props
import com.github.debop4s.redis.AbstractRedisTest
import org.slf4j.LoggerFactory
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}
import redis.RedisClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * RedisConsoleLogSubscriberTest
 * Created by debop on 2014. 2. 22.
 */
@ContextConfiguration(classes = Array(classOf[RedisLogPubSubConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class RedisConsoleLogSubscriberTest extends AbstractRedisTest {

    lazy val log = LoggerFactory.getLogger(getClass)

    implicit val akkaSystem = akka.actor.ActorSystem()
    val redis = RedisClient()

    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)

    test("pubsub") {
        akkaSystem.scheduler.schedule(10 millis, 50 millis) {
            redis.publish("time", System.currentTimeMillis())
        }
        akkaSystem.scheduler.schedule(10 millis, 100 millis) {
            redis.publish("pattern.match", "pattern value")
        }

        val channels = Seq("time")
        val patterns = Seq("pattern.*")

        akkaSystem.actorOf(Props(classOf[SubscribeActor], channels, patterns).withDispatcher("rediscala"))

        Thread.sleep(1000)
    }

}
