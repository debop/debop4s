package com.github.debop4s.redis.logback.pubsub

import com.github.debop4s.core.parallels.Parallels
import com.github.debop4s.redis.pubsub.RedisSubscriberActorFactory
import com.github.debop4s.redis.{RedisConsts, AbstractRedisTest}
import java.net.InetSocketAddress
import org.slf4j.LoggerFactory

/**
 * RedisLogPubshlier를 테스트 합니다.
 * Created by debop on 2014. 2. 22.
 */
class ConsoleLogRedisSubscriberActorTest extends AbstractRedisTest {

    // HINT: 테스트를 수행하려면, logback-test.xml에 RedisLogPublisher 를 등록해야 합니다.

    private lazy val log = LoggerFactory.getLogger(getClass)

    private def registSubscriberActor() {
        val address = new InetSocketAddress("localhost", RedisConsts.DEFAULT_PORT)
        val channels = Seq(RedisConsts.DEFAULT_LOGBACK_CHANNEL)
        val patterns = Seq()

        RedisSubscriberActorFactory.create(classOf[ConsoleLogRedisSubscriberActor],
                                              address,
                                              channels,
                                              patterns)
    }

    before {
        registSubscriberActor()
    }

    test("logging message subscribe") {

        Parallels.runAction(10) {
            (0 until 100).foreach(i => log.debug(s"로그를 씁니다. - $i"))
        }
        Thread.sleep(1000)
    }

}
