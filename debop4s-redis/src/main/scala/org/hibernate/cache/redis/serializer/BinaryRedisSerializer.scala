package org.hibernate.cache.redis.serializer

import java.io.{ObjectInputStream, ByteArrayInputStream, ObjectOutputStream, ByteArrayOutputStream}

/**
 * org.hibernate.cache.redis.serializer.BinaryRedisSerializer 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오전 11:20
 */
class BinaryRedisSerializer[T] extends RedisSerializer[T] {

  override def serialize(graph: T): Array[Byte] = {
    if (graph == null)
      return EMPTY_BYTES

    val os = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(os)
    oos.writeObject(graph)
    oos.flush()

    os.toByteArray
  }

  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    val is = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(is)

    ois.readObject().asInstanceOf[T]
  }


}
