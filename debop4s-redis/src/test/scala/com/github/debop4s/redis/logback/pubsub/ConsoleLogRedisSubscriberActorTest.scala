package com.github.debop4s.redis.logback.pubsub

import akka.actor.Props
import com.github.debop4s.core.parallels.Parallels
import com.github.debop4s.redis.{RedisConsts, AbstractRedisTest}
import java.net.InetSocketAddress
import org.slf4j.LoggerFactory
import redis.RedisClient

/**
 * RedisConsoleLogSubscriberTest
 * Created by debop on 2014. 2. 22.
 */
class ConsoleLogRedisSubscriberActorTest extends AbstractRedisTest {

    lazy val log = LoggerFactory.getLogger(getClass)

    implicit val akkaSystem = akka.actor.ActorSystem()

    val redis = RedisClient()

    private def registSubscriberActor() {
        val address = new InetSocketAddress("localhost", RedisConsts.DEFAULT_PORT)
        val channels = Seq(RedisConsts.DEFAULT_LOGBACK_CHANNEL)
        val patterns = Seq()

        val props = Props(classOf[ConsoleLogRedisSubscriberActor], address, channels, patterns)
        akkaSystem.actorOf(props.withDispatcher("rediscala"))
    }

    before {
        registSubscriberActor()
    }

    test("logging message subscribe") {

        Parallels.runAction(10) {
            (0 until 1000).foreach(i => log.debug(s"로그를 씁니다. - $i"))
        }

        Thread.sleep(1000)
    }

}
