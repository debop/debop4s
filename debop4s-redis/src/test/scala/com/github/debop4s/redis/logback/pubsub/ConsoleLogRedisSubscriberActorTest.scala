package com.github.debop4s.redis.logback.pubsub

import akka.actor.Props
import com.github.debop4s.redis.{RedisConsts, AbstractRedisTest}
import org.slf4j.LoggerFactory
import org.springframework.test.context.ContextConfiguration
import redis.RedisClient
import com.github.debop4s.core.parallels.Parallels
import java.net.InetSocketAddress

/**
 * RedisConsoleLogSubscriberTest
 * Created by debop on 2014. 2. 22.
 */
class ConsoleLogRedisSubscriberActorTest extends AbstractRedisTest {

    lazy val log = LoggerFactory.getLogger(getClass)

    implicit val akkaSystem = akka.actor.ActorSystem()

    val redis = RedisClient()

    test("logging message subscribe") {

        val address = new InetSocketAddress("localhost", RedisConsts.DEFAULT_PORT)
        val channels = Seq(RedisConsts.DEFAULT_LOGBACK_CHANNEL)
        val patterns = Seq()

        akkaSystem.actorOf(Props(classOf[ConsoleLogRedisSubscriberActor], address, channels, patterns).withDispatcher("rediscala"))

        Parallels.runAction(10) {
            (0 until 1000).foreach(i => log.debug(s"로그를 씁니다. - $i"))
        }

        Thread.sleep(1000)
    }

}
