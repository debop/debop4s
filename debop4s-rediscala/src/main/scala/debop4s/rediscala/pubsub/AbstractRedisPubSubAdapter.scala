package debop4s.rediscala.pubsub

import debop4s.rediscala.RedisClientFactory
import redis.api.pubsub.Message
import redis.{RedisPubSub, RedisServer}

/**
 * `RedisPubSub` 를 사용하여, Redis Pub/Sub 의 Subscriber 를 쉽게 사용한 클래스입니다.
 * onMessage를 overriding 해서 사용하세요.
 *
 * @author sunghyouk.bae@gmail.com
 */
abstract class AbstractRedisPubSubAdapter(val server: RedisServer, channels: Seq[String]) {

  val subscriber: RedisPubSub = RedisClientFactory.createPubSub(server.host, server.port, channels)(onMessage)

  protected def onMessage(message: Message): Unit

}
