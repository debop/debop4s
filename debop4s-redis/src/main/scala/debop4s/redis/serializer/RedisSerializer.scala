package debop4s.redis.serializer

/**
 * RedisSerializer
 * @author Sunghyouk Bae
 */
trait RedisSerializer[T] {

  val EMPTY_BYTES = Array[Byte]()

  def serialize(graph: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T

}
