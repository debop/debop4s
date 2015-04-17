package org.hibernate.cache.rediscala.serializer

/**
 * RedisSerializer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 11:21
 */
private[rediscala] trait RedisSerializer[T] {

  val EMPTY_BYTES = Array[Byte]()

  /**
   * 객체를 직렬화합니다.
   */
  def serialize(graph: T): Array[Byte]

  /**
   * 직렬화 정보를 역직렬화하여 객체로 변환합니다.
   */
  def deserialize(bytes: Array[Byte]): T

}

