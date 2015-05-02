package debop4s.rediscala.set

import debop4s.rediscala.client.RedisSyncClient
import debop4s.rediscala.serializer.SnappyFstValueFormatter
import org.springframework.beans.factory.annotation.Autowired
import redis.ByteStringFormatter

/**
 * AbstractSyncRedis
 * @author debop created at 2014. 5. 2.
 */
@deprecated(message = "동기방식은 삭제할 것임", since = "0.5.0")
abstract class AbstractSyncRedis[@miniboxed T] {

  @Autowired val redis: RedisSyncClient = null

  implicit val valueFormatter: ByteStringFormatter[T] = new SnappyFstValueFormatter[T]()
}
