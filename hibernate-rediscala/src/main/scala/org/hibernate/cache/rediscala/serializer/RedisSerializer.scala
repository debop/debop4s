package org.hibernate.cache.rediscala.serializer

/**
 * RedisSerializer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 11:21
 */
trait RedisSerializer[T] {

    val EMPTY_BYTES = Array[Byte]()

    def serialize(graph: T): Array[Byte]

    def deserialize(bytes: Array[Byte]): T

}
