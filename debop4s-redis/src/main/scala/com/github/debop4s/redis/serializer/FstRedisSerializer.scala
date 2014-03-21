package com.github.debop4s.redis.serializer

import de.ruedigermoeller.serialization.FSTConfiguration
import java.io
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

/**
 * FstRedisSerializer
 * Created by debop on 2014. 3. 20.
 */
@deprecated(message = "Tomcat 등에서 Float 수형에 대한 변환이 제대로 안되는 버그가 있다.", since = "0.3.0")
class FstRedisSerializer[T] extends RedisSerializer[T] {

    lazy val conf = FSTConfiguration.createDefaultConfiguration()

    override def serialize(graph: T): Array[Byte] = {
        if (graph == null)
            return EMPTY_BYTES

        var bos = None: Option[ByteArrayOutputStream]

        try {
            bos = Some(new io.ByteArrayOutputStream())
            val oos = conf.getObjectOutput(bos.get)
            oos.writeObject(graph, Seq[Class[_]](): _*)
            oos.flush()

            bos.get.toByteArray
        } finally {
            if (bos.isDefined) bos.get.close()
        }
    }

    override def deserialize(bytes: Array[Byte]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        var bis = None: Option[ByteArrayInputStream]

        try {
            bis = Some(new ByteArrayInputStream(bytes))
            val ois = conf.getObjectInput(bis.get)
            ois.readObject.asInstanceOf[T]
        } finally {
            if (bis.isDefined) bis.get.close()
        }
    }

}
