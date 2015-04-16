package debop4s.rediscala.serializer

/**
 * Redis 데이터를 직렬화 합니다.
 * @author Sunghyouk Bae
 */
trait RedisSerializer[T] {

  val EMPTY_BYTES = Array[Byte]()

  /**
   * 객체를 직렬화 합니다.
   * @param graph serialized 될 객체
   * @return serialized 된 데이터
   */
  def serialize(graph: T): Array[Byte]

  /**
   * 객체를 역 직렬화 합니다.
   * @param bytes serialized 된 데이터
   * @return 원본 객체
   */
  def deserialize(bytes: Array[Byte]): T

}
