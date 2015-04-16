package debop4s.rediscala.pubsub

import debop4s.rediscala.AbstractRedisFunSuite
import org.slf4j.LoggerFactory
import redis.RedisServer
import redis.api.pubsub.Message

/**
 * RedisPubSubAdapterFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RedisPubSubAdapterFunSuite extends AbstractRedisFunSuite {

  val channel = "redis:pubsub:channel:console"

  val server = RedisServer("localhost")
  val subscriber = new ConsoleRedisPubSubAdapter(server, Seq(channel))

  test("redis pubsub") {

    (0 until 10).foreach { i =>
      redis.publish(channel, s"세번째!!!! Adapter 테스트^^ $i = ${ System.currentTimeMillis() }")
    }

    Thread.sleep(1000)
  }

}

class ConsoleRedisPubSubAdapter(server: RedisServer, channels: Seq[String])
  extends AbstractRedisPubSubAdapter(server, channels) {

  val log = LoggerFactory.getLogger(getClass)

  override protected def onMessage(message: Message): Unit = {
    log.debug(s"Sub message: ${ message.data }")
  }
}
