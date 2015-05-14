package debop4s.rediscala.serializer

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import debop4s.core._
import org.nustaq.serialization.FSTConfiguration

/**
 * FstRedisSerializer
 * Created by debop on 2014. 3. 20.
 */
class FstRedisSerializer[@miniboxed T] extends RedisSerializer[T] {

  lazy val conf = FSTConfiguration.createDefaultConfiguration()

  override def serialize(graph: T): Array[Byte] = {
    if (graph == null)
      return EMPTY_BYTES

    using(new ByteArrayOutputStream()) { bos =>
      val oos = conf.getObjectOutput(bos)
      oos.writeObject(graph, Seq[Class[_]](): _*)
      oos.flush()

      bos.toByteArray
    }
  }

  override def deserialize(bytes: Array[Byte]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    using(new ByteArrayInputStream(bytes)) { bis =>
      val ois = conf.getObjectInput(bis)
      ois.readObject().asInstanceOf[T]
    }
  }

}
