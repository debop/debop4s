package debop4s.rediscala.pubsub

import debop4s.core.io.{Serializers, SnappyFstSerializer}
import debop4s.rediscala.AbstractRedisFunSuite
import org.joda.time.DateTime
import redis.RedisPubSub
import redis.api.pubsub.Message

/**
 * RedisPubSubFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RedisPubSubFunSuite extends AbstractRedisFunSuite {

  val channel = "redis:pubsub:channel:string"
  val channelBigData = "redis:pubsub:channel:bigdata"


  val onMessage: Message => Unit = { message =>
    log.debug(s"수신 메시지=${ message.data }")
  }

  test("RedisPubSub 이용하기") {
    val pubsub = RedisPubSub(redis.host,
      redis.port,
      channels = Seq(channel),
      patterns = Nil,
      onMessage = onMessage)

    for (i <- 0 until 100) {
      redis.publish(channel, s"안녕하세요^^ $i = ${ DateTime.now }")
    }

    Thread.sleep(1000)
  }

  val serializer = SnappyFstSerializer()

  test("RedisPubSub 큰 데이터") {
    val pubsub = RedisPubSub(redis.host,
      redis.port,
      channels = Seq(channelBigData),
      patterns = Nil,
      onMessage = m => {
        val data = Serializers.deserializeFromString[PublishData](serializer, m.data, classOf[PublishData])
        log.debug(s"Subscriber Data=$data")
      })
    for (i <- 0 until 100) {
      val data = PublishData(i, DateTime.now, i * i, s"동해물과 백두산이 ~ $i")
      val text = Serializers.serializeAsString(serializer, data)
      redis.publish(channelBigData, text)
    }

    Thread.sleep(1000)
  }


}
// Inner class 면 예외가 발생합니다.
case class PublishData(index: Int, time: DateTime, score: Double, comment: String)
