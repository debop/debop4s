package debop4s

import _root_.redis.ByteStringFormatter
import akka.util.ByteString
import debop4s.redis.serializer._


/**
 * package
 * @author Sunghyouk Bae
 */
package object redis {

  lazy val valueSerializer: RedisSerializer[Any] = new BinaryRedisSerializer[Any]()

  implicit val byteStringFormatter = new ByteStringFormatter[Any] {

    override def serialize(data: Any): ByteString = {
      ByteString(valueSerializer.serialize(data))
    }

    override def deserialize(bs: ByteString): Any = {
      valueSerializer.deserialize(bs.toArray)
    }
  }
}
