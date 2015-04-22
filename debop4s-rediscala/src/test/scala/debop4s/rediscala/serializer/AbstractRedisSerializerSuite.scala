package debop4s.rediscala.serializer

import akka.util.ByteString
import debop4s.core._
import debop4s.core.concurrent._
import debop4s.rediscala.AbstractRedisFunSuite
import debop4s.rediscala.client.RedisSyncClient
import redis.{ByteStringFormatter, RedisClient}

/**
 * AbstractRedisSerializerSuite
 * @author Sunghyouk Bae
 */
abstract class AbstractRedisSerializerSuite[T] extends AbstractRedisFunSuite {

  val serializer: RedisSerializer[T]

  // ByteString 을 변경해 줄 Formatter 가 있어야 합니다!!!
  implicit val byteStringFormatter = new ByteStringFormatter[T] {

    override def serialize(data: T): ByteString = {
      ByteString(serializer.serialize(data))
    }

    override def deserialize(bs: ByteString): T = {
      serializer.deserialize(bs.toArray)
    }
  }

  def createEmpty(): T

  def createSample(): T

  test("null data") {

    val empty = null.asInstanceOf[T]

    val ser = serializer.serialize(empty)
    ser shouldEqual Array.emptyByteArray

    val des = serializer.deserialize(ser)
    des shouldEqual empty
  }

  test("empty data") {
    val empty = createEmpty()

    val ser = serializer.serialize(empty)
    val des = serializer.deserialize(ser)

    des shouldEqual empty
  }

  test("some data") {
    val some = createSample()

    val ser = serializer.serialize(some)
    val des = serializer.deserialize(ser)

    des shouldEqual some
  }

  test("redis get/set HashSet by RedisClient") {
    val redis = RedisClient()
    val key = "serializerKey"

    redis.del(key).await

    val some = createSample()

    redis.hset(key, "field", some).await

    val loaded = redis.hget(key, "field").await.get

    loaded shouldEqual some
  }

  test("redis get/set HashSet by RedisSyncClient") {
    val redis = RedisSyncClient()
    val key = "serializerKey"

    redis.del(key)

    val some: T = createSample()

    // val ser = ByteString(serializer.serialize(some))
    // redis.hset(key, "field", ser)
    redis.hset(key, "field", some)

    val loaded = redis.hget(key, "field").get

    loaded shouldEqual some
  }
}
