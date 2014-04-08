package debop4s.redis.base

import akka.util.ByteString
import debop4s.redis.serializer.BinaryRedisSerializer
import redis.{RedisClient, ByteStringFormatter}

/**
 * AbstractRedis
 * @author Sunghyouk Bae
 */
abstract class AbstractRedis[T] {

    @Autowired val redis: RedisClient = null

    lazy val valueSerializer = new BinaryRedisSerializer[T]()
    implicit val byteStringFormatter = new ByteStringFormatter[T] {

        override def serialize(data: T): ByteString = {
            ByteString(valueSerializer.serialize(data))
        }

        override def deserialize(bs: ByteString): T = {
            valueSerializer.deserialize(bs.toArray)
        }
    }

}
