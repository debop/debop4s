package debop4s.rediscala.logback.pubsub

import java.net.InetSocketAddress

import debop4s.rediscala.pubsub.RedisSubscriberActorFactory
import debop4s.rediscala.{AbstractRedisFunSuite, RedisConsts}

/**
 * RedisLogPubshlier를 테스트 합니다.
 * Created by debop on 2014. 2. 22.
 */
class ConsoleLogRedisSubscriberActorFunSuite extends AbstractRedisFunSuite {

  private def registSubscriberActor() {
    val address = new InetSocketAddress("localhost", RedisConsts.DEFAULT_PORT)
    val channels = Seq(RedisConsts.DEFAULT_LOGBACK_CHANNEL)
    val patterns = Seq()

    RedisSubscriberActorFactory.create(
      classOf[ConsoleLogRedisSubscriberActor],
      address,
      channels,
      patterns
    )
  }

  before {
    registSubscriberActor()
  }

  test("logging message subscribe") {

    (0 until 10).par.foreach { _ =>
      var i = 0
      while (i < 100) {
        log.trace(s"로그를 씁니다. - $i")
        i += 1
      }
    }
    Thread.sleep(1000)
  }

}
