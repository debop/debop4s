package debop4s.rediscala.serializer

import java.io._

import debop4s.core._

/**
 * BinaryRedisSerializer
 * @author Sunghyouk Bae
 */
class BinaryRedisSerializer[@miniboxed T] extends RedisSerializer[T] {

  override def serialize(graph: T): Array[Byte] = {
    if (graph == null)
      return Array.emptyByteArray

    val bos = new ByteArrayOutputStream()

    using(new ObjectOutputStream(bos)) { oos =>
      oos.writeObject(graph)
      oos.flush()
      bos.toByteArray
    }
  }

  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    using(new ByteArrayInputStream(bytes)) { bis =>
      using(new ObjectInputStream(bis)) { ois =>
        ois.readObject().asInstanceOf[T]
      }
    }
  }
}
