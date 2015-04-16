package debop4s.rediscala.serializer

import akka.util.ByteString
import debop4s.core.BinaryStringFormat
import debop4s.core.utils.Strings
import redis.ByteStringFormatter

/**
 * Redis에 저장할 Data 를 [[debop4s.core.io.BinarySerializer]] 를 통해 직렬화 후 HexDecimal 문자열로 변환하여 저장합니다.
 *
 * 이 것은 hconnect-backend-cache 1.3.6 에 있는 AbstractHashSetCacheService 를 재구성할 때 사용합니다.
 * hconnect 2.0.0 이상에서는 데이터 통신의 효율성을 위해 SnappyFstValueFormat 를 사용하도록 구성해야 합니다.
 * 새로 만드는 것은 AbstractRedis, AbstractSyncRedis 의 valueFormatter 를 override 하시면 됩니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
class BinaryHexStringValueFormatter[T] extends ByteStringFormatter[T] {

  val serializer = new BinaryRedisSerializer[T]()

  override def serialize(data: T): ByteString = {
    data match {
      case null => ByteString.empty
      case _ =>
        val hexStr = Strings.getStringFromBytes(serializer.serialize(data), BinaryStringFormat.HexDecimal)
        ByteString(hexStr)
    }
  }

  override def deserialize(bs: ByteString): T = {
    bs match {
      case null => null.asInstanceOf[T]
      case _ =>
        val bytes = Strings.getBytesFromHexString(bs.utf8String)
        serializer.deserialize(bytes)
    }
  }

}
