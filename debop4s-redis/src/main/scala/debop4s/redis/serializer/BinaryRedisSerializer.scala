package debop4s.redis.serializer

import java.io
import java.io.{ ObjectInputStream, ByteArrayInputStream, ObjectOutputStream, ByteArrayOutputStream }

/**
 * BinaryRedisSerializer
 * @author Sunghyouk Bae
 */
class BinaryRedisSerializer[@miniboxed T] extends RedisSerializer[T] {

  override def serialize(graph: T): Array[Byte] = {
    if (graph == null)
      return EMPTY_BYTES

    var bos = None: Option[ByteArrayOutputStream]
    var oos = None: Option[ObjectOutputStream]

    try {
      bos = Some(new io.ByteArrayOutputStream())
      oos = Some(new ObjectOutputStream(bos.get))
      oos.get.writeObject(graph)
      oos.get.flush()

      bos.get.toByteArray
    } finally {
      if (oos.isDefined) oos.get.close()
      if (bos.isDefined) bos.get.close()
    }
  }

  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    var bis = None: Option[ByteArrayInputStream]
    var ois = None: Option[ObjectInputStream]

    try {
      bis = Some(new ByteArrayInputStream(bytes))
      ois = Some(new ObjectInputStream(bis.get))

      ois.get.readObject.asInstanceOf[T]
    } finally {
      if (ois.isDefined) ois.get.close()
      if (bis.isDefined) bis.get.close()
    }
  }
}
